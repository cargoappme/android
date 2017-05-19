package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.voice.SpeakAction;
import me.cargoapp.cargo.event.voice.SpeechDoneEvent;
import me.cargoapp.cargo.helper.ContactsHelper;
import me.cargoapp.cargo.helper.IntentHelper;
import me.cargoapp.cargo.navui.adapter.ContactsAdapter;

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

    String _numberToSendTo;
    String _message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterViews() {
        final ArrayList<ContactsAdapter.ContactRepresentation> contacts = ContactsHelper.getStarred(getActivity());

        _gridView.setAdapter(new ContactsAdapter(getActivity(), contacts));

        _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
            ContactsAdapter.ContactRepresentation contact = contacts.get(position);
            _numberToSendTo = contact.phoneNumber;
            _eventBus.post(new SpeakAction(UTTERANCE_MESSAGE, getString(R.string.tts_message_content, contact.name)));
            }
        });
    }

    @Subscribe
    void onSpeechDone(SpeechDoneEvent event) {
        switch (event.utteranceId) {
            case UTTERANCE_MESSAGE:
                startActivityForResult(IntentHelper.recognizeSpeechIntent(getString(R.string.stt_message_content_prompt)), REQ_MESSAGE_SPEECH_INPUT);
                break;
            case UTTERANCE_VALIDATION:
                startActivityForResult(IntentHelper.recognizeSpeechIntent(getString(R.string.stt_message_send_prompt)), REQ_VALIDATION_SPEECH_INPUT);
                break;
        }
    }

    @OnActivityResult(REQ_MESSAGE_SPEECH_INPUT)
    void onMessageSpeech(int resultCode, @OnActivityResult.Extra(value = RecognizerIntent.EXTRA_RESULTS) ArrayList<String> results) {
        _message = results.get(0);

        _eventBus.post(new SpeakAction(UTTERANCE_VALIDATION, getString(R.string.tts_message_validation, _message)));
    }

    @OnActivityResult(REQ_VALIDATION_SPEECH_INPUT)
    void onValidationSpeech(int resultCode, @OnActivityResult.Extra(value = RecognizerIntent.EXTRA_RESULTS) ArrayList<String> results) {
        String text = results.get(0).toLowerCase().trim();

        if (text.contains(getString(R.string.stt_yes))) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(_numberToSendTo, null, _message, null, null);
            _eventBus.post(new SpeakAction(UTTERANCE_DONE, getString(R.string.tts_message_sent)));
        } else if (text.contains(getString(R.string.stt_no))) {
            _eventBus.post(new SpeakAction(UTTERANCE_DONE, getString(R.string.tts_message_cancel)));
        } else {
            _eventBus.post(new SpeakAction(UTTERANCE_VALIDATION, getString(R.string.tts_message_validation_repeat)));
        }
    }
}
