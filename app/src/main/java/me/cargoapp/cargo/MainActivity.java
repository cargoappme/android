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
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import java.util.ArrayList;
import java.util.Locale;

import me.cargoapp.cargo.service.OverlayService_;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    @AfterViews
    void afterViews() {
        Intent overlayServiceIntent = new Intent(this, OverlayService_.class);
        startService(overlayServiceIntent);
    }

    @OptionsItem(R.id.action_settings)
    void onActionSettings() {
        startActivity(new Intent(this, SettingsActivity_.class));
    }
}
