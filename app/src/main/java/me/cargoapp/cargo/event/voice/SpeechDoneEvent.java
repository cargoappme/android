package me.cargoapp.cargo.event.voice;

public class SpeechDoneEvent {
    public String utteranceId;

    public SpeechDoneEvent(String utteranceId) {
        this.utteranceId = utteranceId;
    }
}
