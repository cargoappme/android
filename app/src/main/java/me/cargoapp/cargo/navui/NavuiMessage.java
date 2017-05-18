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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Locale;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.helper.ContactsHelper;
import me.cargoapp.cargo.helper.IntentHelper;
import me.cargoapp.cargo.navui.adapter.ContactsAdapter;

/**
 * Created by Mathieu on 05/05/2017.
 */
@EFragment(R.layout.fragment_navui_contacts)
public class NavuiMessage extends Fragment implements TextToSpeech.OnInitListener {

    private final int REQ_MESSAGE_SPEECH_INPUT = 1;
    private final int REQ_VALIDATION_SPEECH_INPUT = 2;

    private final String UTTERANCE_MESSAGE = "MESSAGE";
    private final String UTTERANCE_VALIDATION = "VALIDATION";
    private final String UTTERANCE_DONE = "DONE";

    @ViewById(R.id.grid_view)
    GridView _gridView;

    TextToSpeech _tts;

    String _numberToSendTo;
    String _message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _tts = new TextToSpeech(getActivity(), this);
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

            _tts.speak(getString(R.string.tts_message_content, contact.name), TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_MESSAGE);
            }
        });
    }

    @Override
    @Background
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            _tts.setLanguage(Locale.getDefault());
            _tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {}

                @Override
                public void onDone(String utteranceId) {
                    switch (utteranceId) {
                        case UTTERANCE_MESSAGE:
                            startActivityForResult(IntentHelper.recognizeSpeechIntent(getString(R.string.stt_message_content_prompt), Locale.getDefault()), REQ_MESSAGE_SPEECH_INPUT);
                            break;
                        case UTTERANCE_VALIDATION:
                            startActivityForResult(IntentHelper.recognizeSpeechIntent(getString(R.string.stt_message_send_prompt), Locale.getDefault()), REQ_VALIDATION_SPEECH_INPUT);
                            break;
                    }
                }

                @Override
                public void onError(String utteranceId) {}
            });
        }
    }

    @OnActivityResult(REQ_MESSAGE_SPEECH_INPUT)
    void onMessageSpeech(int resultCode, @OnActivityResult.Extra(value = RecognizerIntent.EXTRA_RESULTS) ArrayList<String> results) {
        _message = results.get(0);

        _tts.speak(getString(R.string.tts_message_validation, _message), TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_VALIDATION);
    }

    @OnActivityResult(REQ_VALIDATION_SPEECH_INPUT)
    void onValidationSpeech(int resultCode, @OnActivityResult.Extra(value = RecognizerIntent.EXTRA_RESULTS) ArrayList<String> results) {
        String text = results.get(0).toLowerCase().trim();

        if (text.contains(getString(R.string.stt_yes))) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(_numberToSendTo, null, _message, null, null);
            _tts.speak(getString(R.string.tts_message_sent), TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_DONE);
        } else if (text.contains(getString(R.string.stt_no))) {
            _tts.speak(getString(R.string.tts_message_cancel), TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_DONE);
        } else {
            _tts.speak(getString(R.string.tts_message_validation_repeat), TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_VALIDATION);
        }
    }
}
