package me.cargoapp.cargo.messaging;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import me.cargoapp.cargo.event.message.DismissMessageNotificationAction;

public class MessagingNotificationInteracter {
    static public boolean reply(Context context, MessagingNotificationParser.NotificationParserResult result, String text) {
        if (result.application == MessagingApplication.NONE) return false;
        if (TextUtils.isEmpty(text.trim())) return false;

        int actionIndex;
        int remoteInputIndex;
        switch (result.application) {
            case SMS:
                actionIndex = 0;
                remoteInputIndex = 0;
                break;
            case MESSENGER:
                actionIndex = 1;
                remoteInputIndex = 0;
                break;
            default:
                return false;
        }

        final RemoteInput[] remoteInputs = result.sbn.getNotification().actions[actionIndex].getRemoteInputs();
        Intent intent = new Intent();
        Bundle results = new Bundle();
        results.putString(remoteInputs[remoteInputIndex].getResultKey(), text.trim());
        RemoteInput.addResultsToIntent(remoteInputs, intent, results);

        try {
            result.sbn.getNotification().actions[actionIndex].actionIntent.send(context, 0, intent);
            dismiss(result);
            return true;
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
            return false;
        }
    }

    static public void dismiss(MessagingNotificationParser.NotificationParserResult result) {
        EventBus.getDefault().post(new DismissMessageNotificationAction(result));
    }
}
