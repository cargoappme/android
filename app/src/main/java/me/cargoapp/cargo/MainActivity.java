package me.cargoapp.cargo;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;
import java.util.Locale;

import me.cargoapp.cargo.service.OverlayService_;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private TextToSpeech _tts;
    private SpeechRecognizer _stt;
    private Intent _sttIntent;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @AfterViews
    void afterViews() {
        _stt = SpeechRecognizer.createSpeechRecognizer(this);
        _sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        _sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        _sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        _stt.setRecognitionListener(listener);

        _tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    _tts.setLanguage(Locale.FRENCH);
                }
            }
        });

        Intent overlayServiceIntent = new Intent(this, OverlayService_.class);
        startService(overlayServiceIntent);
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        _stt.startListening(_sttIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity_.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Click(R.id.btn_recognize)
    void onClick() {
        Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
        promptSpeechInput();
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {
        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            _stt.startListening(_sttIntent);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            _tts.speak(matches.get(0), TextToSpeech.QUEUE_FLUSH, null, "0");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }
    }
}
