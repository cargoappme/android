package me.cargoapp.cargo.event;

import me.cargoapp.cargo.messaging.NotificationParser;

public class MessageReceivedEvent {
    public NotificationParser.NotificationParserResult result;

    public MessageReceivedEvent (NotificationParser.NotificationParserResult result) {
        this.result = result;
    }
}
