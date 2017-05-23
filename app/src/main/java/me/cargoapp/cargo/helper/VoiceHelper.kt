package me.cargoapp.cargo.helper

import me.cargoapp.cargo.event.voice.ListenAction
import me.cargoapp.cargo.event.voice.SpeakAction
import org.greenrobot.eventbus.EventBus

/**
 * Created by Marvin on 23/05/2017.
 */

object VoiceHelper {
    fun speak(utteranceId: String, text: String) {
        EventBus.getDefault().post(SpeakAction(utteranceId, text))
    }

    fun listen(listeningId: String) {
        EventBus.getDefault().post(ListenAction(listeningId))
    }
}
