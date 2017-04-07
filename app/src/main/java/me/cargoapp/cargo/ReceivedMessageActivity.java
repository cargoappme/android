package me.cargoapp.cargo;

import android.app.Activity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.event.ShowOverlayAction;
import me.cargoapp.cargo.event.HideOverlayAction;
import me.cargoapp.cargo.event.MessageReceivedEvent;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity(R.layout.activity_received_message)
public class ReceivedMessageActivity extends Activity {
    @ViewById(R.id.contact_icon)
    ImageView _contactIcon;

    @ViewById(R.id.contact_name)
    TextView _contactName;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().post(new ShowOverlayAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void onMessageReceived(MessageReceivedEvent event) {
        EventBus.getDefault().post(new HideOverlayAction());

        _contactName.setText(event.result.author);
        _contactIcon.setImageIcon(event.result.icon);
    }
}
