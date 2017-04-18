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
import me.cargoapp.cargo.event.HideOverlayAction;
import me.cargoapp.cargo.event.ShowOverlayAction;

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
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!_overlayLayer.isShown()) {
            _overlayLayer.addToScreen();
        }

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
    public void onHideOverlay(HideOverlayAction event) {
        if (_overlayLayer.isShown()) {
            _overlayLayer.removeFromScreen();
        }
    }

    @Subscribe
    public void onShowOverlay(ShowOverlayAction event) {
        if (!_overlayLayer.isShown()) {
            _overlayLayer.addToScreen();
        }
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
