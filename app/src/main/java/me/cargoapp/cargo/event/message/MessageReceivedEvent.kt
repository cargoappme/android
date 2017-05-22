package me.cargoapp.cargo.event.message

import me.cargoapp.cargo.messaging.MessagingNotificationParser

data class MessageReceivedEvent(var result: MessagingNotificationParser.NotificationParserResult)
