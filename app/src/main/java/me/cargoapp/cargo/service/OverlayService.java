package me.cargoapp.cargo.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.OverlayLayer;
import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.HideOverlayEvent;
import me.cargoapp.cargo.event.ShowOverlayEvent;

@EService
public class OverlayService extends Service {

    private String TAG = this.getClass().getSimpleName();

    private final static int FOREGROUND_ID = 999;

    @Bean
    OverlayLayer _overlayLayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().register(this);

        _overlayLayer.addToScreen();

        Notification notification = _createNotification();
        startForeground(FOREGROUND_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        _overlayLayer.removeFromScreen();
        _overlayLayer = null;

        stopForeground(true);
    }

    @Subscribe
    public void onHideOverlay(HideOverlayEvent event) {
        _overlayLayer.removeFromScreen();
    }

    @Subscribe
    public void onShowOverlay(ShowOverlayEvent event) {
        _overlayLayer.addToScreen();
    }

    private Notification _createNotification() {
        return new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.primary, null))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle(getResources().getString(R.string.notification_title))
                .setContentText(getResources().getString(R.string.notification_text))
                .build();
    }
}
