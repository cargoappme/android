package me.cargoapp.cargo.messaging

import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils

import org.greenrobot.eventbus.EventBus

import me.cargoapp.cargo.event.message.DismissMessageNotificationAction

object MessagingNotificationInteracter {
    fun reply(context: Context, result: MessagingNotificationParser.NotificationParserResult, text: String): Boolean {
        if (result.application === MessagingApplication.NONE) return false
        if (TextUtils.isEmpty(text.trim { it <= ' ' })) return false

        val actionIndex: Int
        val remoteInputIndex: Int
        when (result.application) {
            MessagingApplication.SMS -> {
                actionIndex = 0
                remoteInputIndex = 0
            }
            MessagingApplication.MESSENGER -> {
                actionIndex = 1
                remoteInputIndex = 0
            }
            else -> return false
        }

        val remoteInputs = result.sbn.notification.actions[actionIndex].remoteInputs
        val intent = Intent()
        val results = Bundle()
        results.putString(remoteInputs[remoteInputIndex].resultKey, text.trim { it <= ' ' })
        RemoteInput.addResultsToIntent(remoteInputs, intent, results)

        try {
            result.sbn.notification.actions[actionIndex].actionIntent.send(context, 0, intent)
            dismiss(result)
            return true
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
            return false
        }

    }

    fun dismiss(result: MessagingNotificationParser.NotificationParserResult) {
        EventBus.getDefault().post(DismissMessageNotificationAction(result))
    }
}
