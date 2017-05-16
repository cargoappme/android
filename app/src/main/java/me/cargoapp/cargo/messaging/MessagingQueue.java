package me.cargoapp.cargo.messaging;

import java.util.ArrayList;

/**
 * Created by Marvin on 10/05/2017.
 */

public class MessagingQueue {
    static ArrayList<MessagingNotificationParser.NotificationParserResult> _messagesQueue = new ArrayList<MessagingNotificationParser.NotificationParserResult>();

    public static void add (MessagingNotificationParser.NotificationParserResult result) {
        _messagesQueue.add(result);
    }

    public static boolean isFilled () {
        return _messagesQueue.size() != 0;
    }

    public static MessagingNotificationParser.NotificationParserResult get () {
        return _messagesQueue.remove(0);
    }
}
