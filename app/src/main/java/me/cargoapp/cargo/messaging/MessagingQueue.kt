package me.cargoapp.cargo.messaging

import java.util.*

/**
 * Created by Marvin on 10/05/2017.
 */


object MessagingQueue {
    private var _messagesQueue = ArrayList<MessagingNotificationParser.NotificationParserResult>()

    fun add(result: MessagingNotificationParser.NotificationParserResult) {
        _messagesQueue.add(result)
    }

    val isFilled: Boolean
        get() = _messagesQueue.size != 0

    fun get(): MessagingNotificationParser.NotificationParserResult {
        return _messagesQueue.removeAt(0)
    }
}
