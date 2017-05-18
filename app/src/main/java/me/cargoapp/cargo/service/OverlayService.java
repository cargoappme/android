package me.cargoapp.cargo.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.NavuiActivity_;
import me.cargoapp.cargo.OverlayLayer;
import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.HideOverlayAction;
import me.cargoapp.cargo.event.OverlayClickedEvent;
import me.cargoapp.cargo.event.OverlaySetBackIconAction;
import me.cargoapp.cargo.event.ShowOverlayAction;
import me.cargoapp.cargo.event.StopOverlayServiceAction;

@EService
public class OverlayService extends Service {

    private String TAG = this.getClass().getSimpleName();

    private final static int FOREGROUND_ID = 999;

    public static boolean active = false;

    @EventBusGreenRobot
    EventBus _eventBus;

    @Bean
    OverlayLayer _overlayLayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Notification notification = _createNotification();
        startForeground(FOREGROUND_ID, notification);

        _eventBus.post(new ShowOverlayAction());

        active = true;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_overlayLayer.isShown()) {
            _overlayLayer.removeFromScreen();
            _overlayLayer = null;
        }

        active = false;

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
    public void onOverlaySetBackIcon(OverlaySetBackIconAction action) {
        _overlayLayer.setBackIcon(action.getBack());
    }

    @Subscribe
    public void onStopOverlayService(StopOverlayServiceAction event) {
        stopSelf();
    }

    @Subscribe
    public void onOverlayClicked(OverlayClickedEvent event) {
        if (NavuiActivity_.active) return;

        NavuiActivity_.intent(getApplicationContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
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
