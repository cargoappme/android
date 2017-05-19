package me.cargoapp.cargo.event.message;

import me.cargoapp.cargo.messaging.MessagingNotificationParser;

public class DismissMessageNotificationAction {
    public MessagingNotificationParser.NotificationParserResult result;

    public DismissMessageNotificationAction(MessagingNotificationParser.NotificationParserResult result) {
        this.result = result;
    }
}
