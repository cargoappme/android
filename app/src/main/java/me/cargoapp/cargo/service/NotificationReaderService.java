package me.cargoapp.cargo.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.ReceivedMessageActivity_;
import me.cargoapp.cargo.event.DismissMessageNotificationAction;
import me.cargoapp.cargo.event.MessageReceivedEvent;
import me.cargoapp.cargo.messaging.Application;
import me.cargoapp.cargo.messaging.NotificationParser;

public class NotificationReaderService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    SharedPreferences _prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init(TAG);

        EventBus.getDefault().register(this);

        _prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Logger.i("onNotificationPosted");

        if (!_prefs.getBoolean("pref_notifications", true)) return;

        final NotificationParser.NotificationParserResult result = NotificationParser.parseNotification(sbn);
        if (result.application != Application.NONE) {
            EventBus.getDefault().post(new DismissMessageNotificationAction(result));

            Intent i = new Intent().setClass(getApplicationContext(), ReceivedMessageActivity_.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            EventBus.getDefault().postSticky(new MessageReceivedEvent(result));
            startActivity(i);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Logger.i("onNotificationRemoved");

        if (!_prefs.getBoolean("pref_notifications", true)) return;
    }

    @Subscribe()
    public void onMessageNotificationDismiss(DismissMessageNotificationAction event) {
        this.cancelNotification(event.result.sbn.getKey());
    }
}
