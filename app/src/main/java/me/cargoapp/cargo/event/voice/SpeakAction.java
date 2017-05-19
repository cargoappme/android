package me.cargoapp.cargo.event.voice;

public class SpeakAction {
    public String utteranceId;
    public String text;

    public SpeakAction(String utteranceId, String text) {
        this.utteranceId = utteranceId;
        this.text = text;
    }
}
