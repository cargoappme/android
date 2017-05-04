package me.cargoapp.cargo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.event.NavuiLaunchEvent;
import me.cargoapp.cargo.event.QuitAction;
import me.cargoapp.cargo.event.StopOverlayServiceAction;
import me.cargoapp.cargo.navui.MainFragment_;
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
        Fragment fragment;
        switch (event.getType()) {
            case MENU:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, MainFragment_.builder().build()).commit();
            case CALL:
                break;
            case MESSAGE:
                break;
            case MUSIC:
                break;
            case OIL:
                break;
            case PARKING:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, NavuiParking_.builder().build()).commit();
        }
    }

    @Subscribe
    public void onQuit(QuitAction event) {
        EventBus.getDefault().post(new StopOverlayServiceAction());
        finish();
    }
}
