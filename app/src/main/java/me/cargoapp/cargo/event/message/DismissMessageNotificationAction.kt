package me.cargoapp.cargo.event.message

import me.cargoapp.cargo.messaging.MessagingNotificationParser

data class DismissMessageNotificationAction(var result: MessagingNotificationParser.NotificationParserResult)
