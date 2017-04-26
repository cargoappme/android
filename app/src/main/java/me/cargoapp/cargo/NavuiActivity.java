package me.cargoapp.cargo;

import android.app.Activity;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.event.QuitAction;
import me.cargoapp.cargo.event.StopOverlayServiceAction;
import me.cargoapp.cargo.navui.MainFragment_;

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
    public void onQuit(QuitAction event) {
        EventBus.getDefault().post(new StopOverlayServiceAction());
        finish();
    }
}
