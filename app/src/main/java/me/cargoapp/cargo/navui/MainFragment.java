package me.cargoapp.cargo.navui;

import android.app.Fragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.greenrobot.eventbus.EventBus;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.NavuiLaunchEvent;
import me.cargoapp.cargo.event.QuitAction;

@EFragment(R.layout.fragment_navui_main)
public class MainFragment extends Fragment {
    @Click(R.id.btn_call)
    public void onCall() {
        EventBus.getDefault().post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.CALL));
    }

    @Click(R.id.btn_message)
    public void onMessage() {
        EventBus.getDefault().post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.MESSAGE));
    }

    @Click(R.id.btn_music)
    public void onMusic() {
        EventBus.getDefault().post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.MUSIC));
    }

    @Click(R.id.btn_oil)
    public void onOil() {
        EventBus.getDefault().post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.OIL));
    }

    @Click(R.id.btn_parking)
    public void onParking() {
        EventBus.getDefault().post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.PARKING));
    }

    @Click(R.id.btn_quit)
    public void onQuit() {
        EventBus.getDefault().post(new QuitAction());
    }
}