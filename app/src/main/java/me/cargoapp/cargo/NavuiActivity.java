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

import me.cargoapp.cargo.event.NavuiLaunchEvent;
import me.cargoapp.cargo.event.OverlayClickedEvent;
import me.cargoapp.cargo.event.OverlaySetBackIconAction;
import me.cargoapp.cargo.event.StopOverlayServiceAction;
import me.cargoapp.cargo.navui.MainFragment_;
import me.cargoapp.cargo.navui.NavuiCall_;
import me.cargoapp.cargo.navui.NavuiMessage_;
import me.cargoapp.cargo.navui.NavuiParking_;

@WindowFeature({Window.FEATURE_NO_TITLE})
@EActivity(R.layout.activity_navui)
public class NavuiActivity extends Activity {

    public static boolean active = false;

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
        if (!_onMenu) _eventBus.post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.MENU));
        else finish();
    }

    @Subscribe
    public void onNavuiLaunch(NavuiLaunchEvent event) {
        if (event.getType() == NavuiLaunchEvent.Type.QUIT) {
            Application_.isJourneyStarted = false;

            _eventBus.post(new StopOverlayServiceAction());
            finish();
            return;
        }

        Fragment fragment = new Fragment();

        switch (event.getType()) {
            case MENU:
                fragment = MainFragment_.builder().build();
                break;
            case CALL:
                fragment = NavuiCall_.builder().build();
                break;
            case MESSAGE:
                fragment = NavuiMessage_.builder().build();
                break;
            case MUSIC:
                break;
            case OIL:
                break;
            case PARKING:
                fragment = NavuiParking_.builder().build();
                break;
            default:
                fragment = MainFragment_.builder().build();
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();

        _onMenu = event.getType() == NavuiLaunchEvent.Type.MENU;
    }
}
