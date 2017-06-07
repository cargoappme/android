package me.cargoapp.cargo.helper

import me.cargoapp.cargo.event.voice.ListenAction
import me.cargoapp.cargo.event.voice.SpeakAction
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by Marvin on 23/05/2017.
 */

object VoiceHelper {
    fun speak(utteranceId: String, text: String, locale: Locale) {
        EventBus.getDefault().post(SpeakAction(utteranceId, text, locale))
    }

    fun listen(listeningId: String, locale: Locale) {
        EventBus.getDefault().post(ListenAction(listeningId, locale))
    }
}
