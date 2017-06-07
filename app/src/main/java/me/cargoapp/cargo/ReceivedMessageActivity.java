package me.cargoapp.cargo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.event.message.HandleMessageQueueAction;
import me.cargoapp.cargo.event.message.MessageReceivedEvent;
import me.cargoapp.cargo.event.overlay.SetOverlayVisibilityAction;
import me.cargoapp.cargo.event.voice.ListeningDoneEvent;
import me.cargoapp.cargo.event.voice.SpeechDoneEvent;
import me.cargoapp.cargo.helper.VoiceHelper;

@WindowFeature({Window.FEATURE_NO_TITLE})
@EActivity(R.layout.activity_received_message)
public class ReceivedMessageActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    public static boolean active = false;

    private final String UTTERANCE_MESSAGE_ASKING = "NAVUI_MESSAGE_RECEIVED_MESSAGE_ASKING";
    private final String UTTERANCE_MESSAGE_READING = "NAVUI_MESSAGE_RECEIVED_MESSAGE_READING";

    private final String LISTENING_VALIDATION = "NAVUI_MESSAGE_RECEIVED_VALIDATION";

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.application_image)
    ImageView _applicationImage;

    @ViewById(R.id.contact_image)
    ImageView _contactImage;

    @ViewById(R.id.contact_text)
    TextView _contactText;

    private String _message;

    @Override
    public void onCreate(Bundle savedInstanceParams) {
        super.onCreate(savedInstanceParams);

        Logger.init(TAG);
    }

    @Override
    public void onStart() {
        super.onStart();

        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();

        active = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        _eventBus.post(new SetOverlayVisibilityAction(true));
    }

    @Subscribe(sticky = true)
    public void onMessageReceived(final MessageReceivedEvent event) {
        _eventBus.post(new SetOverlayVisibilityAction(false));

        int applicationResId;
        switch (event.getResult().getApplication()) {
            case MESSENGER:
                applicationResId = R.drawable.messenger;
                break;
            default:
                applicationResId = R.drawable.sms;
        }
        _applicationImage.setImageResource(applicationResId);

        _contactImage.setImageBitmap(event.getResult().getPicture());
        _contactText.setText(event.getResult().getAuthor());

        _message = event.getResult().getMessage();

        VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_ASKING, getString(R.string.tts_received_message_confirmation, event.getResult().getAuthor()));
    }

    @Subscribe
    public void onSpeechDone(SpeechDoneEvent event) {
        switch (event.getUtteranceId()) {
            case UTTERANCE_MESSAGE_ASKING:
                VoiceHelper.INSTANCE.listen(LISTENING_VALIDATION);
                break;
            case UTTERANCE_MESSAGE_READING:
                _eventBus.post(new HandleMessageQueueAction(HandleMessageQueueAction.Type.DONE));
                finish();
                break;
        }
    }

    @Subscribe
    public void onListeningDone(ListeningDoneEvent event) {
        switch (event.getListeningId()) {
            case LISTENING_VALIDATION:
                String text = event.getText().toLowerCase().trim();

                if (text.contains(getString(R.string.stt_yes))) {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_READING, getString(R.string.tts_received_message_reading, ReceivedMessageActivity.this._message));
                } else if (text.contains(getString(R.string.stt_no))) {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_READING, getString(R.string.tts_received_message_ignore));
                } else {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_ASKING, getString(R.string.tts_received_message_confirmation_repeat));
                }
                break;
        }
    }
}
