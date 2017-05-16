package me.cargoapp.cargo.navui;

import android.app.Fragment;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.greenrobot.eventbus.EventBus;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.NavuiLaunchEvent;

@EFragment(R.layout.navui_main)
public class MainFragment extends Fragment {

    @EventBusGreenRobot
    EventBus _eventBus;

    @Click(R.id.phone)
    void onPhone() {
        _eventBus.post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.CALL));
    }

    @Click(R.id.message)
    void onMessage() {
        _eventBus.post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.MESSAGE));
    }

    @Click(R.id.music)
    void onMusic() {
        _eventBus.post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.MUSIC));
    }

    @Click(R.id.oil)
    void onOil() {
        _eventBus.post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.OIL));
    }

    @Click(R.id.parking)
    void onParking() {
        _eventBus.post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.PARKING));
    }

    @Click(R.id.quit)
    void onQuit() {
        _eventBus.post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.QUIT));
    }
}