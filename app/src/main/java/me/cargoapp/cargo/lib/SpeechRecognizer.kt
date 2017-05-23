package me.cargoapp.cargo.lib

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import me.cargoapp.cargo.event.voice.ListeningDoneEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by Marvin on 23/05/2017.
 */

class SpeechRecognizer(val _context: Context) {
    val _speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(_context)
    val _speechRecognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    val _mainHandler: Handler = Handler(Looper.getMainLooper())

    var _isListening: Boolean = false
    var _utteranceId: String = ""

    init {
        _speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        _speechRecognizer.setRecognitionListener(SpeechRecognitionListener())
    }

    fun listen(utteranceId: String) {
        if (_isListening) {
            _mainHandler.post({ _speechRecognizer.stopListening() })
            EventBus.getDefault().post(ListeningDoneEvent(_utteranceId, null))
        }

        _utteranceId = utteranceId
        _mainHandler.post({ _speechRecognizer.startListening(_speechRecognizerIntent) })
        _isListening = true
    }

    inner class SpeechRecognitionListener : RecognitionListener {
        override fun onBeginningOfSpeech() {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            _mainHandler.post({ _speechRecognizer.startListening(_speechRecognizerIntent) })
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onReadyForSpeech(params: Bundle?) {}

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            EventBus.getDefault().post(ListeningDoneEvent(_utteranceId, matches?.get(0)))
            _isListening = false
        }

        override fun onRmsChanged(rmsdB: Float) {}
    }
}