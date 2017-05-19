package me.cargoapp.cargo.helper;

import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;

import java.util.Locale;

/**
 * Created by Marvin on 16/05/2017.
 */

public class IntentHelper {

    public static Intent createNavigationIntent(double lat, double lon, String query) {
        Uri intentUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + query);
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, intentUri);

        return navigationIntent;
    }

    public static Intent createCallIntent(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        return callIntent;
    }

    public static Intent recognizeSpeechIntent(String prompt) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);

        return intent;
    }
}
