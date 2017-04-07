package me.cargoapp.cargo.messaging;

import android.app.Notification;
import android.graphics.drawable.Icon;
import android.service.notification.StatusBarNotification;

public class NotificationParser {
    static public class NotificationParserResult {
        public Application application;
        public String author;
        public String message;
        public Icon icon;
        public StatusBarNotification sbn;

        NotificationParserResult (StatusBarNotification sbn) {
            application = Application.NONE;
            this.sbn = sbn;
        }
    }

    static public NotificationParserResult parseNotification (StatusBarNotification sbn) {
        NotificationParserResult result = new NotificationParserResult(sbn);

        Notification notification = sbn.getNotification();
        final String pkg = sbn.getPackageName();
        final String title = notification.extras.getString(Notification.EXTRA_TITLE);
        final String text = notification.extras.getString(Notification.EXTRA_TEXT);
        final Icon icon = notification.getLargeIcon();
        final Notification.Action[] actions = notification.actions;

        switch (pkg) {
            case "com.google.android.apps.messaging":
                result.application = Application.SMS;
                result.author = title;
                result.message = text;
                result.icon = icon;
                break;
            case "com.facebook.orca":
                if (notification.priority == Notification.PRIORITY_MIN) break; // foreground notification

                result.application = Application.MESSENGER;
                result.author = title;
                result.message = text;
                result.icon = icon;
                break;
        }

        return result;
    }
}
