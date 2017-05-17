package me.cargoapp.cargo.messaging;

import android.app.Notification;
import android.graphics.Bitmap;
import android.service.notification.StatusBarNotification;

public class MessagingNotificationParser {
    static public class NotificationParserResult {
        public MessagingApplication application;
        public String author;
        public String message;
        public Bitmap picture;
        public StatusBarNotification sbn;

        NotificationParserResult(StatusBarNotification sbn) {
            application = MessagingApplication.NONE;
            this.sbn = sbn;
        }
    }

    static public NotificationParserResult parseNotification(StatusBarNotification sbn) {
        NotificationParserResult result = new NotificationParserResult(sbn);

        Notification notification = sbn.getNotification();
        final String pkg = sbn.getPackageName();
        final String title = notification.extras.getString(Notification.EXTRA_TITLE);
        final String text = notification.extras.getString(Notification.EXTRA_TEXT);
        final Bitmap picture = (Bitmap) notification.extras.get(Notification.EXTRA_LARGE_ICON);
        ;
        final Notification.Action[] actions = notification.actions;

        switch (pkg) {
            case "com.google.android.apps.messaging":
                result.application = MessagingApplication.SMS;
                result.author = title;
                result.message = text;
                result.picture = picture;
                break;
            case "com.facebook.orca":
                if (notification.priority == Notification.PRIORITY_MIN)
                    break; // foreground notification

                result.application = MessagingApplication.MESSENGER;
                result.author = title;
                result.message = text;
                result.picture = picture;
                break;
        }

        return result;
    }
}
