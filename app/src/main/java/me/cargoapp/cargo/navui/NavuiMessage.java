package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.yoga.android.YogaLayout;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import me.cargoapp.cargo.NavuiActivity_;
import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.voice.ListeningDoneEvent;
import me.cargoapp.cargo.event.voice.SpeechDoneEvent;
import me.cargoapp.cargo.helper.ContactsHelper;
import me.cargoapp.cargo.helper.LocalizationHelper;
import me.cargoapp.cargo.helper.VoiceHelper;
import me.cargoapp.cargo.navui.adapter.ContactsAdapter;

/**
 * Created by Mathieu on 05/05/2017.
 */
@EFragment(R.layout.fragment_navui_contacts)
public class NavuiMessage extends Fragment {

    private String TAG = this.getClass().getSimpleName();

    final String UTTERANCE_MESSAGE = "NAVUI_MESSAGE_MESSAGE";
    final String UTTERANCE_VALIDATION = "NAVUI_MESSAGE_VALIDATION";
    final String UTTERANCE_DONE = "NAVUI_MESSAGE_DONE";

    final String LISTENING_MESSAGE_CONTENT = "NAVUI_MESSAGE_MESSAGE_CONTENT";
    final String LISTENING_MESSAGE_VALIDATION = "NAVUI_MESSAGE_MESSAGE_VALIDATION";

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

            VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE, LocalizationHelper.INSTANCE.getString(getContext(), NavuiActivity_.locale, R.string.tts_message_content, contact.name), NavuiActivity_.locale);
            }
        });
    }

    void _clear() {
        _contactView.setVisibility(View.GONE);
        _gridView.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onSpeechDone(SpeechDoneEvent event) {
        switch (event.getUtteranceId()) {
            case UTTERANCE_MESSAGE:
                VoiceHelper.INSTANCE.listen(LISTENING_MESSAGE_CONTENT, NavuiActivity_.locale);
                break;
            case UTTERANCE_VALIDATION:
                VoiceHelper.INSTANCE.listen(LISTENING_MESSAGE_VALIDATION, NavuiActivity_.locale);
                break;
        }
    }

    @Subscribe
    public void onListeningDone(ListeningDoneEvent event) {
        switch (event.getListeningId()) {
            case LISTENING_MESSAGE_CONTENT:
                _message = event.getText();

                VoiceHelper.INSTANCE.speak(UTTERANCE_VALIDATION, LocalizationHelper.INSTANCE.getString(getContext(), NavuiActivity_.locale, R.string.tts_message_validation, _message), NavuiActivity_.locale);
                break;
            case LISTENING_MESSAGE_VALIDATION:
                String text = event.getText().toLowerCase().trim();

                if (text.contains(LocalizationHelper.INSTANCE.getString(getContext(), NavuiActivity_.locale, R.string.stt_yes))) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(_numberToSendTo, null, _message, null, null);
                    VoiceHelper.INSTANCE.speak(UTTERANCE_DONE, LocalizationHelper.INSTANCE.getString(getContext(), NavuiActivity_.locale, R.string.tts_message_sent), NavuiActivity_.locale);

                    _clear();
                } else if (text.contains(LocalizationHelper.INSTANCE.getString(getContext(), NavuiActivity_.locale, R.string.stt_no))) {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_DONE, LocalizationHelper.INSTANCE.getString(getContext(), NavuiActivity_.locale, R.string.tts_message_cancel), NavuiActivity_.locale);

                    _clear();
                } else {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_VALIDATION, LocalizationHelper.INSTANCE.getString(getContext(), NavuiActivity_.locale, R.string.tts_message_validation_repeat), NavuiActivity_.locale);
                }
                break;
        }
    }
}
