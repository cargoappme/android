package me.cargoapp.cargo.event;

import me.cargoapp.cargo.messaging.NotificationParser;

public class MessageNotificationDismissEvent {
    public NotificationParser.NotificationParserResult result;

    public MessageNotificationDismissEvent(NotificationParser.NotificationParserResult result) {
        this.result = result;
    }
}
