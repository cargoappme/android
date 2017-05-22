package me.cargoapp.cargo.messaging

import android.app.Notification
import android.graphics.Bitmap
import android.service.notification.StatusBarNotification

object MessagingNotificationParser {
    class NotificationParserResult internal constructor(var sbn: StatusBarNotification) {
        var application = MessagingApplication.NONE
        var author: String? = null
        var message: String? = null
        var picture: Bitmap? = null
    }

    fun parseNotification(sbn: StatusBarNotification): NotificationParserResult {
        val result = NotificationParserResult(sbn)

        val notification = sbn.notification
        val pkg = sbn.packageName
        val title = notification.extras.getString(Notification.EXTRA_TITLE)
        val text = notification.extras.getString(Notification.EXTRA_TEXT)
        val picture = notification.extras.get(Notification.EXTRA_LARGE_ICON) as? Bitmap

        when (pkg) {
            "com.google.android.apps.messaging" -> {
                result.application = MessagingApplication.SMS
                result.author = title
                result.message = text
                result.picture = picture
            }
            "com.facebook.orca" -> if (notification.priority != Notification.PRIORITY_MIN) { // if not foreground notification
                result.application = MessagingApplication.MESSENGER
                result.author = title
                result.message = text
                result.picture = picture
            }
        }

        return result
    }
}
