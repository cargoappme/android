package me.cargoapp.cargo.lib

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import me.cargoapp.cargo.event.voice.ListeningDoneEvent
import me.cargoapp.cargo.event.voice.ListeningErrorEvent
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by Marvin on 23/05/2017.
 */

class SpeechRecognizer(val _context: Context) {

    val TAG = this.javaClass.simpleName

    val _speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(_context)
    val _mainHandler: Handler = Handler(Looper.getMainLooper())

    var _isListening: Boolean = false
    var _utteranceId: String = ""

    init {
        _speechRecognizer.setRecognitionListener(SpeechRecognitionListener())
    }

    fun listen(utteranceId: String, locale: Locale) {
        stop()

        var intent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.language)
        _utteranceId = utteranceId
        _mainHandler.post({ _speechRecognizer.startListening(intent) })
        _isListening = true
    }

    fun stop() {
        if (_isListening) {
            _mainHandler.post({ _speechRecognizer.stopListening() })
            _isListening = false
            EventBus.getDefault().post(ListeningDoneEvent(_utteranceId, null))
        }
    }

    fun shutdown() {
        _speechRecognizer.destroy()
    }

    inner class SpeechRecognitionListener : RecognitionListener {
        override fun onBeginningOfSpeech() {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            _isListening = false
            EventBus.getDefault().post(ListeningErrorEvent(_utteranceId))

            Log.d(TAG, "Errored: " + _utteranceId);
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onReadyForSpeech(params: Bundle?) {}

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            _isListening = false
            EventBus.getDefault().post(ListeningDoneEvent(_utteranceId, matches?.get(0)))

            Log.d(TAG, "Done: " + _utteranceId);
        }

        override fun onRmsChanged(rmsdB: Float) {}
    }
}