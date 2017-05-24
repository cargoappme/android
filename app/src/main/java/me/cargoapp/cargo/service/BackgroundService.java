package me.cargoapp.cargo.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import es.dmoral.toasty.Toasty;
import me.cargoapp.cargo.NavuiActivity_;
import me.cargoapp.cargo.OverlayLayer;
import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.overlay.OverlayClickedEvent;
import me.cargoapp.cargo.event.overlay.SetOverlayVisibilityAction;
import me.cargoapp.cargo.event.service.ServiceReadyEvent;
import me.cargoapp.cargo.event.service.StopBackgroundServiceAction;
import me.cargoapp.cargo.event.vibrator.VibrateAction;
import me.cargoapp.cargo.event.voice.ListenAction;
import me.cargoapp.cargo.event.voice.SpeakAction;
import me.cargoapp.cargo.event.voice.SpeechDoneEvent;
import me.cargoapp.cargo.lib.SpeechRecognizer;

@EService
public class BackgroundService extends Service implements TextToSpeech.OnInitListener {

    private String TAG = this.getClass().getSimpleName();
    public static boolean active = false;
    public static boolean ready = false;

    private final static int FOREGROUND_ID = 999;
    private final String UTTERANCE_INIT = "BACKGROUND_INIT";

    @EventBusGreenRobot
    EventBus _eventBus;

    @SystemService
    Vibrator _vibrator;

    @Bean
    OverlayLayer _overlayLayer;

    TextToSpeech _tts;
    SpeechRecognizer _stt;

    /**
     * Service
     */

    @Override
    public void onCreate() {
        Logger.init(TAG);

        _tts = new TextToSpeech(getApplicationContext(), this);
        _stt = new SpeechRecognizer(getApplicationContext());

        _overlayLayer.setLoading(true);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Notification notification = _createNotification();
        startForeground(FOREGROUND_ID, notification);

        active = true;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_overlayLayer.isShown()) {
            _overlayLayer.removeFromScreen();
            _overlayLayer = null;
        }

        if (_tts != null) {
            _tts.stop();
            _tts.shutdown();
        }

        if (_stt != null) {
            _stt.stop();
            _stt.shutdown();
        }

        active = false;

        stopForeground(true);
    }

    @Subscribe
    public void onStopBackgroundService(StopBackgroundServiceAction event) {
        stopSelf();
    }

    /**
     * Voice
     */

    // TTS

    @Override
    @Background
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            _tts.setLanguage(Locale.getDefault());
            _tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {}

                @Override
                public void onDone(String utteranceId) {
                    if (utteranceId.equals(UTTERANCE_INIT)) {
                        ready = true;
                        _eventBus.post(new ServiceReadyEvent());
                    } else _eventBus.post(new SpeechDoneEvent(utteranceId));
                }

                @Override
                public void onError(String utteranceId) {}
            });

            _tts.speak("", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_INIT);
        }
    }

    @Subscribe
    void onSpeak(SpeakAction action) {
        _tts.speak(action.getText(), TextToSpeech.QUEUE_ADD, null, action.getUtteranceId());
    }

    // STT

    @Subscribe
    void onListen(ListenAction action) {
        _stt.listen(action.getListeningId());
    }

    /**
     * Vibrations
     */

    @Subscribe
    void onVibrate(VibrateAction action) {
        _vibrator.vibrate(200);
    }

    /**
     * Overlay
     */

    @Subscribe
    public void onSetOverlayVisibility(SetOverlayVisibilityAction action) {
        if (action.getVisible()) {
            _overlayLayer.addToScreen();
        } else {
            _overlayLayer.removeFromScreen();
        }
    }

    @Subscribe
    public void onOverlayClicked(OverlayClickedEvent event) {
        if (!ready) {
            Toasty.warning(getApplicationContext(), getString(R.string.navui_overlay_toast_wait), Toast.LENGTH_SHORT).show();
            // return;
        }

        NavuiActivity_.intent(getApplicationContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceReady(ServiceReadyEvent event) {
        _overlayLayer.setLoading(false);
    }

    private Notification _createNotification() {
        return new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.primary, null))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle(getResources().getString(R.string.notification_title))
                .setContentText(getResources().getString(R.string.notification_text))
                .build();
    }
}
