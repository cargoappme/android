package me.cargoapp.cargo.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import me.cargoapp.cargo.ReceivedMessageActivity_;
import me.cargoapp.cargo.event.MessageReceivedEvent;
import me.cargoapp.cargo.messaging.Application;
import me.cargoapp.cargo.messaging.NotificationParser;

public class NotificationReaderService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();

    TextToSpeech _tts;
    SharedPreferences _prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init(TAG);

        _tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    _tts.setLanguage(Locale.FRENCH);
                }
            }
        });

        _prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Logger.i("onNotificationPosted");

        if (!_prefs.getBoolean("pref_notifications", true)) return;

        final NotificationParser.NotificationParserResult result = NotificationParser.parseNotification(sbn);
        if (result.application != Application.NONE) {
            /* Toast.makeText(getApplicationContext(), "Message reçu", Toast.LENGTH_SHORT).show();
            Toast toast = new Toast(getApplicationContext());
            ImageView view = new ImageView(getApplicationContext());
            view.setImageIcon(result.icon);
            toast.setView(view);
            toast.show();
            _tts.speak("Message reçu de la part de " + result.author + " : " + result.message, TextToSpeech.QUEUE_ADD, null, "speak"); */

            Intent i = new Intent().setClass(getApplicationContext(), ReceivedMessageActivity_.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            EventBus.getDefault().postSticky(new MessageReceivedEvent(result));
            startActivity(i);
        }

        /*for (Notification.Action action : actions) {
            Toast.makeText(getApplicationContext(), action.title, Toast.LENGTH_SHORT).show();
            final RemoteInput[] remoteInputs = action.getRemoteInputs();
            Intent intent = new Intent();
            Bundle results = new Bundle();
            results.putString(remoteInputs[0].getResultKey(), "Ouaaaais !");
            RemoteInput.addResultsToIntent(remoteInputs, intent, results);

            try {
                action.actionIntent.send(getApplicationContext(), 0, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Logger.i("onNotificationRemoved");

        if (!_prefs.getBoolean("pref_notifications", true)) return;
    }
}
