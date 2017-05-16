package me.cargoapp.cargo;

import android.app.Activity;
import android.app.Fragment;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.event.NavuiLaunchEvent;
import me.cargoapp.cargo.event.StopOverlayServiceAction;
import me.cargoapp.cargo.navui.MainFragment_;
import me.cargoapp.cargo.navui.NavuiCall_;
import me.cargoapp.cargo.navui.NavuiParking_;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity(R.layout.activity_navui)
public class NavuiActivity extends Activity {
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @AfterViews
    public void afterViews() {
        getFragmentManager().beginTransaction().add(R.id.fragment_container, MainFragment_.builder().build()).commit();
    }

    @Subscribe
    public void onNavuiLaunch(NavuiLaunchEvent event) {
        if (event.getType() == NavuiLaunchEvent.Type.QUIT) {
            EventBus.getDefault().post(new StopOverlayServiceAction());
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

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
