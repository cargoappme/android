package me.cargoapp.cargo.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.Application_;
import me.cargoapp.cargo.ReceivedMessageActivity_;
import me.cargoapp.cargo.event.message.DismissMessageNotificationAction;
import me.cargoapp.cargo.event.message.HandleMessageQueueAction;
import me.cargoapp.cargo.event.message.MessageReceivedEvent;
import me.cargoapp.cargo.messaging.MessagingApplication;
import me.cargoapp.cargo.messaging.MessagingNotificationParser;
import me.cargoapp.cargo.messaging.MessagingQueue;

public class NotificationReaderService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    public static boolean active = false;

    EventBus _eventBus;
    SharedPreferences _prefs;

    boolean _messageActivityShown = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init(TAG);

        _eventBus = EventBus.getDefault();
        _eventBus.register(this);

        _prefs = PreferenceManager.getDefaultSharedPreferences(this);

        active = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        _eventBus.unregister(this);

        active = false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Logger.i("onNotificationPosted");

        if (!Application_.isJourneyStarted) return;
        if (!_prefs.getBoolean("pref_notifications", true)) return;

        final MessagingNotificationParser.NotificationParserResult result = MessagingNotificationParser.INSTANCE.parseNotification(sbn);
        if (result.getApplication() != MessagingApplication.NONE) {
            _eventBus.post(new DismissMessageNotificationAction(result));

            MessagingQueue.INSTANCE.add(result);

            _eventBus.post(new HandleMessageQueueAction(HandleMessageQueueAction.Type.RECEIVED));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Logger.i("onNotificationRemoved");

        if (!Application_.isJourneyStarted) return;
        if (!_prefs.getBoolean("pref_notifications", true)) return;
    }

    @Subscribe()
    public void onHandleMessageQueue(HandleMessageQueueAction action) {
        Intent i = new Intent().setClass(getApplicationContext(), ReceivedMessageActivity_.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (action.getType() == HandleMessageQueueAction.Type.RECEIVED) {
            if (_messageActivityShown) return;

            _messageActivityShown = true;
            _eventBus.postSticky(new MessageReceivedEvent(MessagingQueue.INSTANCE.get()));
            startActivity(i);
        } else if (action.getType() == HandleMessageQueueAction.Type.DONE) {
            if (MessagingQueue.INSTANCE.isFilled()) {
                _eventBus.postSticky(new MessageReceivedEvent(MessagingQueue.INSTANCE.get()));
                startActivity(i);
            } else {
                _messageActivityShown = false;
            }
        }
    }

    @Subscribe()
    public void onDismissMessageNotification(DismissMessageNotificationAction event) {
        this.cancelNotification(event.getResult().getSbn().getKey());
    }
}
