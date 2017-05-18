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
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Locale;

import me.cargoapp.cargo.event.HandleMessageQueueAction;
import me.cargoapp.cargo.event.HideOverlayAction;
import me.cargoapp.cargo.event.MessageReceivedEvent;
import me.cargoapp.cargo.event.OverlaySetBackIconAction;
import me.cargoapp.cargo.event.ShowOverlayAction;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity(R.layout.activity_received_message)
public class ReceivedMessageActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    public static boolean active = false;

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.application_image)
    ImageView _applicationImage;

    @ViewById(R.id.contact_image)
    ImageView _contactImage;

    @ViewById(R.id.contact_text)
    TextView _contactText;

    private TextToSpeech _tts;
    private SpeechRecognizer _stt;
    private Intent _sttIntent;

    private String _message;

    private final String MESSAGE_ASKING = "MESSAGE_ASKING";
    private final String MESSAGE_READING = "MESSAGE_READING";

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_tts != null) {
            _tts.stop();
            _tts.shutdown();
        }
    }

    @AfterInject
    void onInject() {
        Logger.init(TAG);

        _sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        _sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        _sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    }

    private void listenOnMainThread() {
        Handler loopHandler = new Handler(Looper.getMainLooper());
        loopHandler.post(new Runnable() {
            @Override
            public void run() {
                _stt.startListening(_sttIntent);
            }
        });
    }

    private void speakOnMainThread(final String text, final String utteranceId) {
        Handler loopHandler = new Handler(Looper.getMainLooper());
        loopHandler.post(new Runnable() {
            @Override
            public void run() {
                _tts.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
            }
        });
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageReceived(final MessageReceivedEvent event) {
        Logger.i("Received message");

        _eventBus.post(new HideOverlayAction());

        int applicationResId;
        switch (event.result.application) {
            case MESSENGER:
                applicationResId= R.drawable.messenger;
                break;
            default:
                applicationResId = R.drawable.sms;
        }
        _applicationImage.setImageResource(applicationResId);

        _contactImage.setImageBitmap(event.result.picture);
        _contactText.setText(event.result.author);

        _message = event.result.message;

        final Context appContext = getApplicationContext();

        Handler loopHandler = new Handler(Looper.getMainLooper());
        loopHandler.post(new Runnable() {
            @Override
            public void run() {
                // Recognition

                _stt = SpeechRecognizer.createSpeechRecognizer(appContext);
                SpeechRecognitionListener listener = new SpeechRecognitionListener();
                _stt.setRecognitionListener(listener);

                // Text to speech

                _tts = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        Logger.i("TTS initialized");

                        _tts.setLanguage(Locale.getDefault());
                        _tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String utteranceId) {}

                            @Override
                            public void onError(String utteranceId) {}

                            @Override
                            public void onDone(String utteranceId) {
                                Logger.i("TTS done speaking, utteranceId: " + utteranceId);
                                if (utteranceId.equals(MESSAGE_ASKING)) {
                                    listenOnMainThread();
                                } else if (utteranceId.equals(MESSAGE_READING)) {
                                    EventBus.getDefault().post(new HandleMessageQueueAction(HandleMessageQueueAction.Type.DONE));
                                    finish();
                                }
                            }
                        });
                        speakOnMainThread("Nouveau message de " + event.result.author + ". Voulez-vous le lire ?", MESSAGE_ASKING);
                    }
                    }
                });
            }
        });
    }


    protected class SpeechRecognitionListener implements RecognitionListener
    {
        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onReadyForSpeech(Bundle params) {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String match = matches.get(0);

            Logger.i("STT done", match);

            if (match.toLowerCase().trim().contains("oui")) {
                speakOnMainThread("Voici le message : " + ReceivedMessageActivity.this._message, MESSAGE_READING);
            } else if (match.toLowerCase().trim().contains("non")) {
                speakOnMainThread("Très bien, j'ignore le message.", MESSAGE_READING);
                EventBus.getDefault().post(new HandleMessageQueueAction(HandleMessageQueueAction.Type.DONE));
                finish();
            } else {
                speakOnMainThread("Je n'ai pas compris. Voulez-vous lire le message, oui ou non ?", MESSAGE_ASKING);
            }
        }
    }
}
