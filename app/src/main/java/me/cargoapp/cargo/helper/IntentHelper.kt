package me.cargoapp.cargo.helper

import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import java.util.*

/**
 * Created by Marvin on 16/05/2017.
 */

object IntentHelper {
    fun createNavigationIntent(lat: Double, lon: Double, query: String): Intent {
        val intentUri = Uri.parse("geo:$lat,$lon?q=$query")
        val navigationIntent = Intent(Intent.ACTION_VIEW, intentUri)

        return navigationIntent
    }

    fun createCallIntent(number: String): Intent {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + number)

        return callIntent
    }

    fun recognizeSpeechIntent(prompt: String): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)

        return intent
    }
}
