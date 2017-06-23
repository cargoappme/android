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
import me.cargoapp.cargo.helper.LocalizationHelper;
import me.cargoapp.cargo.helper.VoiceHelper;
import me.cargoapp.cargo.messaging.MessagingNotificationInteracter;
import me.cargoapp.cargo.messaging.MessagingNotificationParser;

@WindowFeature({Window.FEATURE_NO_TITLE})
@EActivity(R.layout.activity_received_message)
public class ReceivedMessageActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    public static boolean active = false;

    private final String UTTERANCE_MESSAGE_READ_CONFIRMATION = "NAVUI_MESSAGE_RECEIVED_MESSAGE_READ_CONFIRMATION";
    private final String UTTERANCE_MESSAGE_READING = "NAVUI_MESSAGE_RECEIVED_MESSAGE_READING";
    private final String UTTERANCE_MESSAGE_IGNORE = "NAVUI_MESSAGE_RECEIVED_MESSAGE_IGNORE";
    private final String UTTERANCE_MESSAGE_REPLY_CONTENT = "NAVUI_MESSAGE_RECEIVED_MESSAGE_REPLY_CONTENT";
    private final String UTTERANCE_MESSAGE_REPLY_VALIDATION = "NAVUI_MESSAGE_RECEIVED_MESSAGE_REPLY_VALIDATION";
    private final String UTTERANCE_MESSAGE_REPLY_SENT = "NAVUI_MESSAGE_RECEIVED_MESSAGE_REPLY_SENT";

    private final String LISTENING_READ_INTENTION = "NAVUI_MESSAGE_RECEIVED_READ_INTENTION";
    private final String LISTENING_REPLY_INTENTION = "NAVUI_MESSAGE_RECEIVED_REPLY_INTENTION";
    private final String LISTENING_REPLY_MESSAGE = "NAVUI_MESSAGE_RECEIVED_REPLY_MESSAGE";
    private final String LISTENING_REPLY_VALIDATION = "NAVUI_MESSAGE_RECEIVED_MESSAGE_REPLY_VALIDATION";

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.application_image)
    ImageView _applicationImage;

    @ViewById(R.id.contact_image)
    ImageView _contactImage;

    @ViewById(R.id.contact_text)
    TextView _contactText;

    private String _message;
    private String _reply;
    private MessagingNotificationParser.NotificationParserResult _notificationResult;

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
        _notificationResult = event.getResult();

        VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_READ_CONFIRMATION, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_received_message_confirmation, event.getResult().getAuthor()), NavuiActivity_.locale);
    }

    @Subscribe
    public void onSpeechDone(SpeechDoneEvent event) {
        switch (event.getUtteranceId()) {
            case UTTERANCE_MESSAGE_READ_CONFIRMATION:
                VoiceHelper.INSTANCE.listen(LISTENING_READ_INTENTION, NavuiActivity_.locale);
                break;
            case UTTERANCE_MESSAGE_IGNORE:
                _eventBus.post(new HandleMessageQueueAction(HandleMessageQueueAction.Type.DONE));
                finish();
                break;
            case UTTERANCE_MESSAGE_READING:
                VoiceHelper.INSTANCE.listen(LISTENING_REPLY_INTENTION, NavuiActivity_.locale);
                break;
            case UTTERANCE_MESSAGE_REPLY_CONTENT:
                VoiceHelper.INSTANCE.listen(LISTENING_REPLY_MESSAGE, NavuiActivity_.locale);
                break;
            case UTTERANCE_MESSAGE_REPLY_VALIDATION:
                VoiceHelper.INSTANCE.listen(LISTENING_REPLY_VALIDATION, NavuiActivity_.locale);
                break;
            case UTTERANCE_MESSAGE_REPLY_SENT:
                _eventBus.post(new HandleMessageQueueAction(HandleMessageQueueAction.Type.DONE));
                finish();
                break;
        }
    }

    @Subscribe
    public void onListeningDone(ListeningDoneEvent event) {
        String text;

        switch (event.getListeningId()) {
            case LISTENING_READ_INTENTION:
                text = event.getText().toLowerCase().trim();

                if (text.contains(LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.stt_yes))) {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_READING, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_received_message_reading, ReceivedMessageActivity.this._message), NavuiActivity_.locale);
                } else if (text.contains(LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.stt_no))) {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_IGNORE, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_received_message_ignore), NavuiActivity_.locale);
                } else {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_READ_CONFIRMATION, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_received_message_confirmation_repeat), NavuiActivity_.locale);
                }
                break;
            case LISTENING_REPLY_INTENTION:
                text = event.getText().toLowerCase().trim();

                if (text.contains(LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.stt_yes))) {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_REPLY_CONTENT, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_received_message_prompt), NavuiActivity_.locale);
                } else if (text.contains(LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.stt_no))) {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_IGNORE, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_message_cancel), NavuiActivity_.locale);
                } else {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_READING, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_received_message_reply_repeat), NavuiActivity_.locale);
                }
                break;
            case LISTENING_REPLY_MESSAGE:
                _reply = event.getText();

                VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_REPLY_VALIDATION, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_message_validation, _reply), NavuiActivity_.locale);

                break;
            case LISTENING_REPLY_VALIDATION:
                text = event.getText().toLowerCase().trim();

                if (text.contains(LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.stt_yes))) {
                    MessagingNotificationInteracter.INSTANCE.reply(this, _notificationResult, _reply);
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_REPLY_SENT, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_message_sent), NavuiActivity_.locale);
                } else if (text.contains(LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.stt_no))) {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_IGNORE, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_message_cancel), NavuiActivity_.locale);
                } else {
                    VoiceHelper.INSTANCE.speak(UTTERANCE_MESSAGE_REPLY_VALIDATION, LocalizationHelper.INSTANCE.getString(this, NavuiActivity_.locale, R.string.tts_message_validation_repeat), NavuiActivity_.locale);
                }
                break;
        }
    }
}
