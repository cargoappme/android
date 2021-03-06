package me.cargoapp.cargo.navui;

import android.Manifest;
import android.app.Fragment;
import android.graphics.Color;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.yoga.android.YogaLayout;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import me.cargoapp.cargo.NavuiActivity_;
import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.navui.HandleNavuiActionAction;
import me.cargoapp.cargo.helper.LocalizationHelper;
import me.cargoapp.cargo.helper.PermissionHelper;
import me.cargoapp.cargo.helper.VoiceHelper;

@EFragment(R.layout.navui_menu)
public class NavuiMenu extends Fragment {

    final String[] PHONE_PERMISSIONS = { Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE };
    final String[] SMS_PERMISSIONS = { Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO, Manifest.permission.SEND_SMS };
    final String[] MUSIC_PERMISSIONS = {};
    final String[] GAS_PERMISSIONS = {};
    final String[] PARKING_PERMISSIONS = { Manifest.permission.ACCESS_FINE_LOCATION };

    final String UTTERANCE_NO_PERMISSIONS = "NAVUI_MENU_NO_PERMISSIONS";

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.phone_icon)
    ImageView _phoneIcon;

    @ViewById(R.id.message_icon)
    ImageView _messageIcon;

    @ViewById(R.id.music_icon)
    ImageView _musicIcon;

    @ViewById(R.id.oil_icon)
    ImageView _oilIcon;

    @ViewById(R.id.parking_icon)
    ImageView _parkingIcon;

    @ViewById(R.id.language_icon)
    ImageView _languageIcon;

    @ViewById(R.id.quit_icon)
    ImageView _quitIcon;

    @AfterViews
    void afterViews() {
        ArrayList<ImagePermissions> imagePermissions = new ArrayList<ImagePermissions>();
        imagePermissions.add(new ImagePermissions(_phoneIcon, PHONE_PERMISSIONS));
        imagePermissions.add(new ImagePermissions(_messageIcon, SMS_PERMISSIONS));
        imagePermissions.add(new ImagePermissions(_musicIcon, MUSIC_PERMISSIONS));
        imagePermissions.add(new ImagePermissions(_oilIcon, GAS_PERMISSIONS));
        imagePermissions.add(new ImagePermissions(_parkingIcon, PARKING_PERMISSIONS));

        for (ImagePermissions iImagePermissions : imagePermissions) {
            if (!PermissionHelper.INSTANCE.isPermittedTo(getActivity(), iImagePermissions.permissions))
                iImagePermissions.imageView.setAlpha(0.3f);
        }

        // language flag

        String language = NavuiActivity_.locale.getLanguage();

        int langFlagResId;
        boolean tint = false;
        if (language.equals("fr")) {
            langFlagResId = R.drawable.ic_flag_fr;
        } else if (language.equals("en")) {
            langFlagResId = R.drawable.ic_flag_en;
        } else {
            langFlagResId = R.drawable.gradient_navui_language;
            tint = true;
        }

        _languageIcon.setImageDrawable(getResources().getDrawable(langFlagResId, null));
        if (tint) {
            _languageIcon.setColorFilter(Color.argb(255, 255, 255, 255));
        } else {
            _languageIcon.setColorFilter(null);
            _languageIcon.setImageTintMode(null);
        }
    }

    boolean checkPermissions(String[] permissions) {
        if (PermissionHelper.INSTANCE.isPermittedTo(getActivity(), permissions)) return true;

        String text = LocalizationHelper.INSTANCE.getString(getContext(), NavuiActivity_.locale, R.string.navui_item_no_permissions);
        VoiceHelper.INSTANCE.speak(UTTERANCE_NO_PERMISSIONS, text, NavuiActivity_.locale);
        Toasty.warning(getActivity(), text, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Touch(R.id.phone)
    public boolean onPhoneTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.phone);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;
                // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
    }

    @Click(R.id.phone)
    void onPhone() {
        if (!checkPermissions(PHONE_PERMISSIONS)) return;

        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.CALL));
    }
    @Touch(R.id.message)
    public boolean onMessageTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.message);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;
            // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
    }
    @Click(R.id.message)
    void onMessage() {
        if (!checkPermissions(SMS_PERMISSIONS)) return;

        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.MESSAGE));
    }

    @Touch(R.id.music)
    public boolean onMusicTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.music);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;
            // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
    }
    @Click(R.id.music)
    void onMusic() {
        if (!checkPermissions(MUSIC_PERMISSIONS)) return;

        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.MUSIC));
    }

    @Touch(R.id.oil)
    public boolean onOilTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.oil);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;
            // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
    }
    @Click(R.id.oil)
    void onOil() {
        if (!checkPermissions(GAS_PERMISSIONS)) return;

        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.OIL));
    }

    @Touch(R.id.parking)
    public boolean onParkingTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.parking);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;
            // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
    }

    @Click(R.id.parking)
    void onParking() {
        if (!checkPermissions(PARKING_PERMISSIONS)) return;

        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.PARKING));
    }


    @Touch(R.id.language)
    public boolean onLanguageTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.language);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;
            // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
    }
    @Click(R.id.language)
    void onLanguage() {
        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.LANGUAGE));
    }

    @Touch(R.id.quit)
    public boolean onQuitTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.quit);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;
            // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
    }
    @Click(R.id.quit)
    void onQuit() {
        _eventBus.post(new HandleNavuiActionAction(HandleNavuiActionAction.Type.QUIT));
    }

    class ImagePermissions {
        public ImageView imageView;
        public String[] permissions;

        ImagePermissions(ImageView imageView, String[] permissions) {
            this.imageView = imageView;
            this.permissions = permissions;
        }
    }
}