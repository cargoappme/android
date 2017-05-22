package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.yoga.android.YogaLayout;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.voice.SpeakAction;
import me.cargoapp.cargo.event.voice.SpeechDoneEvent;
import me.cargoapp.cargo.helper.ContactsHelper;
import me.cargoapp.cargo.helper.IntentHelper;
import me.cargoapp.cargo.navui.adapter.ContactsAdapter;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Mathieu on 05/05/2017.
 */
@EFragment(R.layout.fragment_navui_contacts)
public class NavuiMessage extends Fragment {

    final int REQ_MESSAGE_SPEECH_INPUT = 1;
    final int REQ_VALIDATION_SPEECH_INPUT = 2;

    final String UTTERANCE_MESSAGE = "NAVUI_MESSAGE_MESSAGE";
    final String UTTERANCE_VALIDATION = "NAVUI_MESSAGE_VALIDATION";
    final String UTTERANCE_DONE = "NAVUI_MESSAGE_DONE";

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.grid_view)
    GridView _gridView;

    @ViewById(R.id.contact)
    YogaLayout _contactView;

    ImageView _contactImage;
    TextView _contactText;

    String _numberToSendTo;
    String _message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterViews() {
        _contactView.setVisibility(View.GONE);
        _contactImage = (ImageView) _contactView.findViewById(R.id.contact_image);
        _contactText = (TextView) _contactView.findViewById(R.id.contact_text);

        final ArrayList<ContactsAdapter.ContactRepresentation> contacts = ContactsHelper.INSTANCE.getStarred(getActivity());

        _gridView.setAdapter(new ContactsAdapter(getActivity(), contacts));

        _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
            ContactsAdapter.ContactRepresentation contact = contacts.get(position);
            _numberToSendTo = contact.phoneNumber;

            _gridView.setVisibility(View.GONE);
            _contactText.setText(contact.name);
            if (contact.photoUri != null) _contactImage.setImageURI(Uri.parse(contact.photoUri));
            _contactView.setVisibility(View.VISIBLE);

            _eventBus.post(new SpeakAction(UTTERANCE_MESSAGE, getString(R.string.tts_message_content, contact.name)));
            }
        });
    }

    void _clear() {
        _contactView.setVisibility(View.GONE);
        _gridView.setVisibility(View.VISIBLE);
    }

    void _handleError() {
        Toasty.error(getActivity(), getString(R.string.generic_error), Toast.LENGTH_LONG, true).show();
        _clear();
    }

    @Subscribe
    public void onSpeechDone(SpeechDoneEvent event) {
        switch (event.getUtteranceId()) {
            case UTTERANCE_MESSAGE:
                startActivityForResult(IntentHelper.INSTANCE.recognizeSpeechIntent(getString(R.string.stt_message_content_prompt)), REQ_MESSAGE_SPEECH_INPUT);
                break;
            case UTTERANCE_VALIDATION:
                startActivityForResult(IntentHelper.INSTANCE.recognizeSpeechIntent(getString(R.string.stt_message_send_prompt)), REQ_VALIDATION_SPEECH_INPUT);
                break;
        }
    }

    @OnActivityResult(REQ_MESSAGE_SPEECH_INPUT)
    void onMessageSpeech(int resultCode, @OnActivityResult.Extra(value = RecognizerIntent.EXTRA_RESULTS) ArrayList<String> results) {
        if (resultCode == RESULT_CANCELED) {
            _eventBus.post(new SpeakAction(UTTERANCE_DONE, getString(R.string.tts_message_cancel)));
            _clear();
            return;
        }

        if (resultCode != RESULT_OK) {
            _handleError();
            return;
        }

        _message = results.get(0);

        _eventBus.post(new SpeakAction(UTTERANCE_VALIDATION, getString(R.string.tts_message_validation, _message)));
    }

    @OnActivityResult(REQ_VALIDATION_SPEECH_INPUT)
    void onValidationSpeech(int resultCode, @OnActivityResult.Extra(value = RecognizerIntent.EXTRA_RESULTS) ArrayList<String> results) {
        if (resultCode == RESULT_CANCELED) {
            _eventBus.post(new SpeakAction(UTTERANCE_DONE, getString(R.string.tts_message_cancel)));
            _clear();
            return;
        }

        if (resultCode != RESULT_OK) {
            _handleError();
            return;
        }

        String text = results.get(0).toLowerCase().trim();

        if (text.contains(getString(R.string.stt_yes))) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(_numberToSendTo, null, _message, null, null);
            _eventBus.post(new SpeakAction(UTTERANCE_DONE, getString(R.string.tts_message_sent)));

            _clear();
        } else if (text.contains(getString(R.string.stt_no))) {
            _eventBus.post(new SpeakAction(UTTERANCE_DONE, getString(R.string.tts_message_cancel)));

            _clear();
        } else {
            _eventBus.post(new SpeakAction(UTTERANCE_VALIDATION, getString(R.string.tts_message_validation_repeat)));
        }
    }
}
