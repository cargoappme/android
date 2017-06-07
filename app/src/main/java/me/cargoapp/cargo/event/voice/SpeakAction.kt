package me.cargoapp.cargo.event.voice

import java.util.*

data class SpeakAction(val utteranceId: String, val text: String, val locale: Locale)
