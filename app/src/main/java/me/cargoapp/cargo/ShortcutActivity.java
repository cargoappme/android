package me.cargoapp.cargo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.race604.drawable.wave.WaveDrawable;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import me.cargoapp.cargo.event.navui.HandleNavuiActionAction;
import me.cargoapp.cargo.event.overlay.SetOverlayVisibilityAction;
import me.cargoapp.cargo.event.service.ServiceBootedEvent;
import me.cargoapp.cargo.event.service.StopBackgroundServiceAction;
import me.cargoapp.cargo.event.vibrator.VibrateAction;
import me.cargoapp.cargo.event.voice.ListenAction;
import me.cargoapp.cargo.event.voice.ListeningDoneEvent;
import me.cargoapp.cargo.event.voice.ListeningErrorEvent;
import me.cargoapp.cargo.event.voice.SpeechDoneEvent;
import me.cargoapp.cargo.helper.IntentHelper;
import me.cargoapp.cargo.helper.LocalizationHelper;
import me.cargoapp.cargo.helper.VoiceHelper;
import me.cargoapp.cargo.navui.NavuiCall_;
import me.cargoapp.cargo.navui.NavuiLanguage_;
import me.cargoapp.cargo.navui.NavuiMenu_;
import me.cargoapp.cargo.navui.NavuiMessage_;
import me.cargoapp.cargo.navui.NavuiMusic_;
import me.cargoapp.cargo.navui.NavuiOil_;
import me.cargoapp.cargo.navui.NavuiParking_;
import me.cargoapp.cargo.service.BackgroundService_;

import static me.cargoapp.cargo.event.navui.HandleNavuiActionAction.Type.MENU;

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
