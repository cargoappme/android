package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.yoga.android.YogaLayout;

import org.androidannotations.annotations.AfterInject;
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

import static android.app.Activity.RESULT_OK;

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

                _tts.speak("Je vais envoyer un SMS à " + contact.name + ". Quel message voulez-vous envoyer ?", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_MESSAGE);
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
                            startActivityForResult(IntentHelper.recognizeSpeechIntent("Contenu du message", Locale.getDefault()), REQ_MESSAGE_SPEECH_INPUT);
                            break;
                        case UTTERANCE_VALIDATION:
                            startActivityForResult(IntentHelper.recognizeSpeechIntent("Envoyer ?", Locale.getDefault()), REQ_VALIDATION_SPEECH_INPUT);
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

        _tts.speak("J'ai compris le message suivant : " + _message + ". Voulez-vous l'envoyer ?", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_VALIDATION);
    }

    @OnActivityResult(REQ_VALIDATION_SPEECH_INPUT)
    void onValidationSpeech(int resultCode, @OnActivityResult.Extra(value = RecognizerIntent.EXTRA_RESULTS) ArrayList<String> results) {
        String text = results.get(0).toLowerCase().trim();

        if (text.contains("oui")) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(_numberToSendTo, null, _message, null, null);
            _tts.speak("Le message a été envoyé.", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_DONE);
        } else if (text.contains("non")) {
            _tts.speak("Très bien, j'annule.", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_DONE);
        } else {
            _tts.speak("Je n'ai pas compris. Voulez-vous envoyer le message ? Répondez par oui ou par non.", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_VALIDATION);
        }
    }
}
