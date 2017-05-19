package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;

import me.cargoapp.cargo.R;

/**
 * Created by Mathieu on 19/05/2017.
 */
@EFragment(R.layout.fragment_navui_music)
public class NavuiMusic extends Fragment {
    @SystemService
    AudioManager _audioManager;


    @AfterViews
    void afterViews() {
        Button playPause = (Button)getActivity().findViewById(R.id.playPause);
        if(_audioManager.isMusicActive()) {
            playPause.setText("Pause");
        }
        else{
            playPause.setText("Play");
        }


    }
    @Click(R.id.prev)
    void previous() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        _audioManager.dispatchMediaKeyEvent(event);
        KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        _audioManager.dispatchMediaKeyEvent(event2);
    }
    @Click(R.id.next)
    void next() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT);
        _audioManager.dispatchMediaKeyEvent(event);
        KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT);
        _audioManager.dispatchMediaKeyEvent(event2);
    }
    @Click(R.id.playPause)
    void playPause() {
        Button playPause = (Button)getActivity().findViewById(R.id.playPause);
            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            _audioManager.dispatchMediaKeyEvent(event);
        KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        _audioManager.dispatchMediaKeyEvent(event2);
        if(_audioManager.isMusicActive()) {
            playPause.setText("Play");
        }
        else{
            playPause.setText("Pause");
        }
    }
}


