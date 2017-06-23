package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.yoga.android.YogaLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import es.dmoral.toasty.Toasty;
import me.cargoapp.cargo.R;

@EFragment(R.layout.fragment_navui_music)
public class NavuiMusic extends Fragment {
    @SystemService
    AudioManager _audioManager;
    @ViewById(R.id.playPause_icon)
    ImageView playPause;

    @AfterViews
    void afterViews() {
        if(_audioManager.isMusicActive()){
            playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp, null));
        }
        else{
            playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp, null));
        }
        Toasty.info(getContext(), getString(R.string.music_info), Toast.LENGTH_LONG, true).show();
    }
     @Touch(R.id.prev)
     public boolean onPrevTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.prev);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;

            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
     }

    @Click(R.id.prev)
    void previous() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        _audioManager.dispatchMediaKeyEvent(event);
        KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        _audioManager.dispatchMediaKeyEvent(event2);
    }

    @Touch(R.id.next)
    public boolean onNextTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.next);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ll.setAlpha(1f);
                return false;
            case MotionEvent.ACTION_UP:
                ll.setAlpha(0.7f);
                return false;
        }
        return false;
    }

     @Click(R.id.next)
     void next() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT);
        _audioManager.dispatchMediaKeyEvent(event);
        KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT);
        _audioManager.dispatchMediaKeyEvent(event2);
     }

     @Touch(R.id.playPause)
     public boolean onplayPauseTouch(MotionEvent event) {
        YogaLayout ll = (YogaLayout) getActivity().findViewById(R.id.playPause);
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            ll.setAlpha(1f);
            return false;
        case MotionEvent.ACTION_UP:
            ll.setAlpha(0.7f);
            return false;
        }
        return false;
     }

     @Click(R.id.playPause)
     void playPause() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        _audioManager.dispatchMediaKeyEvent(event);
        KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        _audioManager.dispatchMediaKeyEvent(event2);
         if(_audioManager.isMusicActive()){
             playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp, null));
         }
         else{
             playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp, null));
         }
     }
 }