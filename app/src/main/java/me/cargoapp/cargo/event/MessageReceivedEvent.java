package me.cargoapp.cargo.event;

import me.cargoapp.cargo.messaging.MessagingNotificationParser;

public class MessageReceivedEvent {
    public MessagingNotificationParser.NotificationParserResult result;

    public MessageReceivedEvent(MessagingNotificationParser.NotificationParserResult result) {
        this.result = result;
    }
}
