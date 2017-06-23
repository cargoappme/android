package me.cargoapp.cargo;

import android.app.Activity;
import android.content.Intent;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.EActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.event.overlay.SetOverlayVisibilityAction;
import me.cargoapp.cargo.event.service.ServiceBootedEvent;
import me.cargoapp.cargo.service.BackgroundService_;

@EActivity()
public class ShortcutActivity extends Activity {

    @EventBusGreenRobot
    EventBus _eventBus;

    @Override
    public void onStart() {
        super.onStart();

        if (Application_.isJourneyStarted) {
            finish();
            return;
        }

        if (!BackgroundService_.active) startService(new Intent(this, BackgroundService_.class));
    }

    @Subscribe
    public void onServiceBooted(ServiceBootedEvent event) {
        Application_.isJourneyStarted = true;
        Application_.journeyWithSharing = false;

        _eventBus.post(new SetOverlayVisibilityAction(true));

        finish();
    }
}
