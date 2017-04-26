package me.cargoapp.cargo.navui;

import android.app.Fragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.greenrobot.eventbus.EventBus;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.QuitAction;

@EFragment(R.layout.fragment_navui_main)
public class MainFragment extends Fragment {
    @Click(R.id.btn_quit)
    public void onQuit() {
        EventBus.getDefault().post(new QuitAction());
    }
}