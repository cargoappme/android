package me.cargoapp.cargo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.race604.drawable.wave.WaveDrawable;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import me.cargoapp.cargo.event.navui.HandleNavuiActionAction;
import me.cargoapp.cargo.event.overlay.SetOverlayVisibilityAction;
import me.cargoapp.cargo.event.service.StopBackgroundServiceAction;
import me.cargoapp.cargo.event.vibrator.VibrateAction;
import me.cargoapp.cargo.event.voice.ListenAction;
import me.cargoapp.cargo.event.voice.ListeningDoneEvent;
import me.cargoapp.cargo.event.voice.ListeningErrorEvent;
import me.cargoapp.cargo.event.voice.SpeechDoneEvent;
import me.cargoapp.cargo.helper.LocalizationHelper;
import me.cargoapp.cargo.helper.VoiceHelper;
import me.cargoapp.cargo.navui.NavuiCall_;
import me.cargoapp.cargo.navui.NavuiLanguage_;
import me.cargoapp.cargo.navui.NavuiMenu_;
import me.cargoapp.cargo.navui.NavuiMessage_;
import me.cargoapp.cargo.navui.NavuiMusic_;
import me.cargoapp.cargo.navui.NavuiOil_;
import me.cargoapp.cargo.navui.NavuiParking_;

import static me.cargoapp.cargo.event.navui.HandleNavuiActionAction.Type.MENU;

@WindowFeature({Window.FEATURE_NO_TITLE})
@EActivity(R.layout.activity_navui)
public class NavuiActivity extends Activity {

    public static boolean active = false;
    public static Locale locale = Locale.getDefault();

    final String UTTERANCE_SPEAK_ITEM = "NAVUI_SPEAK_ITEM";
    final String UTTERANCE_SPEECH_ERROR = "NAVUI_SPEECH_ERROR";

    boolean _onMenu = true;
    boolean _listening = false;
    String _erroredListeningId;
    WaveDrawable _waveDrawable;

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.icon)
    ImageView _navIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _waveDrawable = new WaveDrawable(getDrawable(R.drawable.microphone));
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(2000);
        _waveDrawable.setIndeterminateAnimator(animator);
        _waveDrawable.setIndeterminate(true);

        getFragmentManager().beginTransaction().add(R.id.fragment_container, NavuiMenu_.builder().build()).commit();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        _eventBus.post(new SetOverlayVisibilityAction(false));
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;

        _eventBus.post(new SetOverlayVisibilityAction(true));
    }

    @Click(R.id.nav_menu)
    void onNavClick() {
        if (!_onMenu) _eventBus.post(new HandleNavuiActionAction(MENU));
        else finish();
    }

    void _setImageResource() {
        int navIconResId;
        if (_onMenu) navIconResId = R.drawable.ic_cargo_c;
        else navIconResId = R.drawable.ic_chevron_left_black_24dp;

        _navIcon.setImageResource(navIconResId);
    }

    @Subscribe
    public void onHandleNavuiAction(HandleNavuiActionAction action) {
        if (action.getType() == HandleNavuiActionAction.Type.QUIT) {
            Application_.isJourneyStarted = false;

            _eventBus.post(new StopBackgroundServiceAction());
            finish();
            return;
        }

        Fragment fragment = NavuiMenu_.builder().build();
        String item = "";

        switch (action.getType()) {
            case CALL:
                fragment = NavuiCall_.builder().build();
                item = LocalizationHelper.INSTANCE.getString(this, locale, R.string.navui_item_phone);
                break;
            case MESSAGE:
                fragment = NavuiMessage_.builder().build();
                item = LocalizationHelper.INSTANCE.getString(this, locale, R.string.navui_item_sms);
                break;
            case MUSIC:
                fragment = NavuiMusic_.builder().build();
                item = LocalizationHelper.INSTANCE.getString(this, locale, R.string.navui_item_music);
                break;
            case OIL:
                fragment = NavuiOil_.builder().build();
                item = LocalizationHelper.INSTANCE.getString(this, locale, R.string.navui_item_oil);
                break;
            case LANGUAGE:
                fragment = NavuiLanguage_.builder().build();
                item = LocalizationHelper.INSTANCE.getString(this, locale, R.string.navui_item_language);
                break;
            case PARKING:
                item = LocalizationHelper.INSTANCE.getString(this, locale, R.string.navui_item_parking);
                fragment = NavuiParking_.builder().build();
                break;
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();

        VoiceHelper.INSTANCE.speak(UTTERANCE_SPEAK_ITEM, item, NavuiActivity_.locale);

        _eventBus.post(new VibrateAction());

        _onMenu = action.getType() == MENU;

        _setImageResource();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListen(ListenAction action) {
        _listening = true;
        _navIcon.setImageDrawable(_waveDrawable);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListeningDone(ListeningDoneEvent event) {
        _listening = false;
        _setImageResource();
    }

    @Subscribe
    public void onListeningError(ListeningErrorEvent event) {
        _erroredListeningId = event.getListeningId();
        VoiceHelper.INSTANCE.speak(UTTERANCE_SPEECH_ERROR, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_error_repeat), NavuiActivity_.locale);
    }

    @Subscribe
    public void onSpeechDone(SpeechDoneEvent event) {
        if (event.getUtteranceId().equals(UTTERANCE_SPEECH_ERROR)) VoiceHelper.INSTANCE.listen(_erroredListeningId, NavuiActivity_.locale);
    }
}
