package me.cargoapp.cargo.event;

import me.cargoapp.cargo.messaging.NotificationParser;

public class DismissMessageNotificationAction {
    public NotificationParser.NotificationParserResult result;

    public DismissMessageNotificationAction(NotificationParser.NotificationParserResult result) {
        this.result = result;
    }
}
