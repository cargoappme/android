package me.cargoapp.cargo.navui;

import android.app.Fragment;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import me.cargoapp.cargo.NavuiActivity_;
import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.navui.HandleNavuiActionAction;

@EFragment(R.layout.fragment_navui_language)
public class NavuiLanguage extends Fragment {

    @EventBusGreenRobot
    EventBus _eventBus;

    @Click(R.id.fr)
    void onFrench() {
        _updateLang("fr");
    }

    @Click(R.id.en)
    void onEnglish() {
        _updateLang("en");
    }

    void _updateLang(String lang) {
        Locale myLocale = new Locale(lang);
        NavuiActivity_.locale = myLocale;

        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.MENU));
    }
}