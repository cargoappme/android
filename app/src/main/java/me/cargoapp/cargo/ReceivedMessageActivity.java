package me.cargoapp.cargo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.SmsManager;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Locale;

import me.cargoapp.cargo.event.message.HandleMessageQueueAction;
import me.cargoapp.cargo.event.message.MessageReceivedEvent;
import me.cargoapp.cargo.event.overlay.HideOverlayAction;
import me.cargoapp.cargo.event.overlay.ShowOverlayAction;
import me.cargoapp.cargo.event.voice.SpeakAction;
import me.cargoapp.cargo.event.voice.SpeechDoneEvent;
import me.cargoapp.cargo.helper.IntentHelper;

@WindowFeature({Window.FEATURE_NO_TITLE})
@EActivity(R.layout.activity_received_message)
public class ReceivedMessageActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    public static boolean active = false;

    final int REQ_VALIDATION_SPEECH_INPUT = 1;

    private final String UTTERANCE_MESSAGE_ASKING = "NAVUI_MESSAGE_RECEIVED_ASKING";
    private final String UTTERANCE_MESSAGE_READING = "NAVUI_MESSAGE_RECEIVED_READING";

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.application_image)
    ImageView _applicationImage;

    @ViewById(R.id.contact_image)
    ImageView _contactImage;

    @ViewById(R.id.contact_text)
    TextView _contactText;

    private String _message;

    @Override
    public void onCreate(Bundle savedInstanceParams) {
        super.onCreate(savedInstanceParams);

        Logger.init(TAG);
    }

    @Override
    public void onStart() {
        super.onStart();

        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();

        active = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        _eventBus.post(new ShowOverlayAction());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageReceived(final MessageReceivedEvent event) {
        _eventBus.post(new HideOverlayAction());

        int applicationResId;
        switch (event.result.application) {
            case MESSENGER:
                applicationResId = R.drawable.messenger;
                break;
            default:
                applicationResId = R.drawable.sms;
        }
        _applicationImage.setImageResource(applicationResId);

        _contactImage.setImageBitmap(event.result.picture);
        _contactText.setText(event.result.author);

        _message = event.result.message;

        _eventBus.post(new SpeakAction(UTTERANCE_MESSAGE_ASKING, getString(R.string.tts_received_message_confirmation, event.result.author)));
    }

    @Subscribe
    void onSpeechDone(SpeechDoneEvent event) {
        switch (event.utteranceId) {
            case UTTERANCE_MESSAGE_ASKING:
                startActivityForResult(IntentHelper.recognizeSpeechIntent(getString(R.string.stt_received_message_read_prompt)), REQ_VALIDATION_SPEECH_INPUT);
                break;
            case UTTERANCE_MESSAGE_READING:
                _eventBus.post(new HandleMessageQueueAction(HandleMessageQueueAction.Type.DONE));
                finish();
                break;
        }
    }

    @OnActivityResult(REQ_VALIDATION_SPEECH_INPUT)
    void onValidationSpeech(int resultCode, @OnActivityResult.Extra(value = RecognizerIntent.EXTRA_RESULTS) ArrayList<String> results) {
        String text = results.get(0).toLowerCase().trim();

        if (text.contains(getString(R.string.stt_yes))) {
            _eventBus.post(new SpeakAction(UTTERANCE_MESSAGE_READING, getString(R.string.tts_received_message_reading, ReceivedMessageActivity.this._message)));
        } else if (text.contains(getString(R.string.stt_no))) {
            _eventBus.post(new SpeakAction(UTTERANCE_MESSAGE_READING, getString(R.string.tts_received_message_ignore)));
        } else {
            _eventBus.post(new SpeakAction(UTTERANCE_MESSAGE_ASKING, getString(R.string.tts_received_message_confirmation_repeat)));
        }
    }
}
