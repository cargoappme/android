package me.cargoapp.cargo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.ISlidePolicy;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;

import es.dmoral.toasty.Toasty;
import me.cargoapp.cargo.helper.PermissionHelper;



@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity
public class WelcomeActivity extends AppIntro2 {

    final static String NOTIFICATION_ARG = "notifications";

    private String TAG = this.getClass().getSimpleName();

    @Pref
    Preferences_ _preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] dangerousAndSpecialPermissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
        };

        // Checking for first time launch or all perms - before calling setContentView()
        if (!_preferences.isFirstRun().get() && PermissionHelper.isPermittedTo(this, dangerousAndSpecialPermissions)) {
            launchHomeScreen();
            finish();
            return;
        }

        addSlide(AppIntro2Fragment.newInstance(getString(R.string.welcome_welcome_title), getString(R.string.welcome_welcome_description), R.drawable.welcome_welcome, getResources().getColor(R.color.slide_primary_background, null)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.welcome_microphone_title), getString(R.string.welcome_microphone_description), R.drawable.welcome_microphone, getResources().getColor(R.color.slide_primary_background, null)));
        askForPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2);
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.welcome_gps_title), getString(R.string.welcome_gps_description), R.drawable.welcome_maps, getResources().getColor(R.color.slide_primary_background, null)));
        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        Fragment notificationFragment = new WelcomeActivity_.SpecialFragment_();
        Bundle notificationArgs = new Bundle();
        notificationArgs.putBoolean(NOTIFICATION_ARG, true);
        notificationFragment.setArguments(notificationArgs);
        addSlide(notificationFragment);
        Fragment overlayFragment = new WelcomeActivity_.SpecialFragment_();
        Bundle overlayArgs = new Bundle();
        overlayArgs.putBoolean(NOTIFICATION_ARG, false);
        overlayFragment.setArguments(overlayArgs);
        addSlide(overlayFragment);
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.welcome_contacts_title), getString(R.string.welcome_contacts_description), R.drawable.welcome_contacts, getResources().getColor(R.color.slide_secondary_background, null)));
        askForPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 6);

        showSkipButton(false);

        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        launchHomeScreen();
    }

    private void launchHomeScreen() {
        _preferences.isFirstRun().put(false);
        MainActivity_.intent(this).start();
        finish();
    }

    @EFragment(R.layout.fragment_welcome_special)
    public static class SpecialFragment extends Fragment implements ISlidePolicy {
        @ViewById(R.id.layout)
        LinearLayout _layout;

        @ViewById(R.id.title)
        TextView _title;

        @ViewById(R.id.image)
        ImageView _image;

        @ViewById(R.id.description)
        TextView _description;

        @ViewById(R.id.button)
        TextView _button;

        boolean _isNotification;

        @AfterInject
        public void afterInject() {
            _isNotification = getArguments().getBoolean(NOTIFICATION_ARG);
        }

        @AfterViews
        public void afterViews() {
            if (_isNotification) {
                _layout.setBackgroundColor(getResources().getColor(R.color.slide_primary_background, null));
                _title.setText(R.string.welcome_notifications_title);
                _description.setText(R.string.welcome_notifications_description);
            } else {
                _layout.setBackgroundColor(getResources().getColor(R.color.slide_primary_background, null));
                _title.setText(R.string.welcome_overlay_title);
                _description.setText(R.string.welcome_overlay_description);
            }

            _image.setImageResource(R.drawable.welcome_check);
            _button.setText(R.string.welcome_special_button);
        }

        @Click(R.id.button)
        public void onClick() {
            Toasty.info(getContext(), getString(R.string.welcome_enable_permission), Toast.LENGTH_LONG, true).show();

            Intent intent = new Intent(_isNotification ? Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS : Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, 0);
        }

        @Override
        public boolean isPolicyRespected() {
            if (_isNotification) {
                return PermissionHelper.isPermittedTo(getContext(), Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE);
            } else {
                return PermissionHelper.isPermittedTo(getContext(), Manifest.permission.SYSTEM_ALERT_WINDOW);
            }
        }

        @Override
        public void onUserIllegallyRequestedNextPage() {
            Toasty.warning(getContext(), getString(R.string.welcome_permission_needed), Toast.LENGTH_LONG, true).show();
        }
    }
}
