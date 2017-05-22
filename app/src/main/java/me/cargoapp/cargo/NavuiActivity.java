package me.cargoapp.cargo;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.Window;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.event.navui.HandleNavuiActionAction;
import me.cargoapp.cargo.event.overlay.OverlayClickedEvent;
import me.cargoapp.cargo.event.overlay.OverlaySetBackIconAction;
import me.cargoapp.cargo.event.overlay.StopOverlayServiceAction;
import me.cargoapp.cargo.event.vibrator.VibrateAction;
import me.cargoapp.cargo.event.voice.SpeakAction;
import me.cargoapp.cargo.navui.MainFragment_;
import me.cargoapp.cargo.navui.NavuiCall_;
import me.cargoapp.cargo.navui.NavuiMessage_;
import me.cargoapp.cargo.navui.NavuiOil_;
import me.cargoapp.cargo.navui.NavuiParking_;

import static me.cargoapp.cargo.event.navui.HandleNavuiActionAction.Type.MENU;

@WindowFeature({Window.FEATURE_NO_TITLE})
@EActivity(R.layout.activity_navui)
public class NavuiActivity extends Activity {

    public static boolean active = false;

    final String UTTERANCE_NAVUI_SPEAK_ITEM = "NAVUI_SPEAK_ITEM";

    boolean _onMenu = true;

    @EventBusGreenRobot
    EventBus _eventBus;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        _eventBus.post(new OverlaySetBackIconAction(true));
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;

        _eventBus.post(new OverlaySetBackIconAction(false));
    }

    @AfterViews
    public void afterViews() {
        getFragmentManager().beginTransaction().add(R.id.fragment_container, MainFragment_.builder().build()).commit();
    }

    @Subscribe
    public void onOverlayClick(OverlayClickedEvent event) {
        if (!_onMenu) _eventBus.post(new HandleNavuiActionAction(MENU));
        else finish();
    }

    @Subscribe
    public void onHandleNavuiAction(HandleNavuiActionAction action) {
        if (action.getType() == HandleNavuiActionAction.Type.QUIT) {
            Application_.isJourneyStarted = false;

            _eventBus.post(new StopOverlayServiceAction());
            finish();
            return;
        }

        Fragment fragment = MainFragment_.builder().build();
        String item = "";

        switch (action.getType()) {
            case CALL:
                fragment = NavuiCall_.builder().build();
                item = getString(R.string.navui_item_phone);
                break;
            case MESSAGE:
                fragment = NavuiMessage_.builder().build();
                item = getString(R.string.navui_item_sms);
                break;
            case MUSIC:
                item = getString(R.string.navui_item_music);
                break;
            case OIL:
                fragment = NavuiOil_.builder().build();
                item = getString(R.string.navui_item_oil);
                break;
            case PARKING:
                item = getString(R.string.navui_item_parking);
                fragment = NavuiParking_.builder().build();
                break;
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();

        _eventBus.post(new SpeakAction(UTTERANCE_NAVUI_SPEAK_ITEM, item));
        _eventBus.post(new VibrateAction());

        _onMenu = action.getType() == MENU;
    }
}
