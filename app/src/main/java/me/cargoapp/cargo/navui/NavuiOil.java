package me.cargoapp.cargo.navui;

/**
 * Created by Mathieu on 04/05/2017.
 */

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.EFragment;
import org.greenrobot.eventbus.EventBus;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.navui.HandleNavuiActionAction;

@EFragment
public class NavuiOil extends Fragment {

    @EventBusGreenRobot
    EventBus _eventBus;

    @Override
    public void onStart() {
        super.onStart();

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + getString(R.string.navui_item_oil));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.MENU));
    }
}

