package me.cargoapp.cargo.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.NavuiActivity_;
import me.cargoapp.cargo.OverlayLayer;
import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.HideOverlayAction;
import me.cargoapp.cargo.event.OverlayClickedEvent;
import me.cargoapp.cargo.event.ShowOverlayAction;
import me.cargoapp.cargo.event.StopOverlayServiceAction;

@EService
public class OverlayService extends Service {

    private String TAG = this.getClass().getSimpleName();

    private final static int FOREGROUND_ID = 999;

    public static boolean isStarted = true;

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
        Notification notification = _createNotification();
        startForeground(FOREGROUND_ID, notification);

        EventBus.getDefault().post(new ShowOverlayAction());

        OverlayService.isStarted = true;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        if (_overlayLayer.isShown()) {
            _overlayLayer.removeFromScreen();
            _overlayLayer = null;
        }

        OverlayService.isStarted = false;

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

    @Subscribe
    public void onStopOverlayService(StopOverlayServiceAction event) {
        stopSelf();
    }

    @Subscribe
    public void onOverlayClicked(OverlayClickedEvent event) {
        Intent i = new Intent().setClass(getApplicationContext(), NavuiActivity_.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
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
