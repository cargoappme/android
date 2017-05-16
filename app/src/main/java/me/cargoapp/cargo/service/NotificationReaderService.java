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
import me.cargoapp.cargo.event.HandleMessageQueueAction;
import me.cargoapp.cargo.event.MessageReceivedEvent;
import me.cargoapp.cargo.messaging.MessagingApplication;
import me.cargoapp.cargo.messaging.MessagingNotificationParser;
import me.cargoapp.cargo.messaging.MessagingQueue;

public class NotificationReaderService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    SharedPreferences _prefs;

    boolean _messageActivityShown = false;

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

        if (!OverlayService.isStarted) return;
        if (!_prefs.getBoolean("pref_notifications", true)) return;

        final MessagingNotificationParser.NotificationParserResult result = MessagingNotificationParser.parseNotification(sbn);
        if (result.application != MessagingApplication.NONE) {
            EventBus.getDefault().post(new DismissMessageNotificationAction(result));

            MessagingQueue.add(result);

            EventBus.getDefault().post(new HandleMessageQueueAction(HandleMessageQueueAction.Type.RECEIVED));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Logger.i("onNotificationRemoved");

        if (!OverlayService.isStarted) return;
        if (!_prefs.getBoolean("pref_notifications", true)) return;
    }

    @Subscribe()
    public void onHandleMessageQueueAction(HandleMessageQueueAction action) {
        Intent i = new Intent().setClass(getApplicationContext(), ReceivedMessageActivity_.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (action.getType() == HandleMessageQueueAction.Type.RECEIVED) {
            if (_messageActivityShown) return;

            _messageActivityShown = true;
            EventBus.getDefault().postSticky(new MessageReceivedEvent(MessagingQueue.get()));
            startActivity(i);
        } else if (action.getType() == HandleMessageQueueAction.Type.DONE) {
            if (MessagingQueue.isFilled()) {
                EventBus.getDefault().postSticky(new MessageReceivedEvent(MessagingQueue.get()));
                startActivity(i);
            } else {
                _messageActivityShown = false;
            }
        }
    }

    @Subscribe()
    public void onMessageNotificationDismiss(DismissMessageNotificationAction event) {
        this.cancelNotification(event.result.sbn.getKey());
    }
}
