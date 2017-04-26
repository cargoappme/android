package me.cargoapp.cargo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

@EBean
public class OverlayLayer implements View.OnTouchListener {

    private String TAG = this.getClass().getSimpleName();

    @SystemService
    WindowManager _wm;

    @SystemService
    LayoutInflater _layoutInflater;

    @SystemService
    Vibrator _vibrator;

    private Context _context;
    private FrameLayout _layout;

    ImageView _imageView;

    public OverlayLayer(Context context) {
        _context = context;
        _layout = new FrameLayout(context);
    }

    @AfterInject
    public void afterInject() {
        _layoutInflater.inflate(R.layout.overlay, _layout);
        _imageView = (ImageView) _layout.findViewById(R.id.overlay);
        _imageView.setOnTouchListener(this); // cannot use annotation as we use an inflater and not activity
    }

    public void addToScreen() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.LEFT;
        params.alpha = 0.5f;

        _wm.addView(_layout, params);
    }

    public boolean isShown() { return _layout.isShown(); }

    public void removeFromScreen() {
        _wm.removeView(_layout);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int)event.getX();
                int y = (int)event.getY();

                GradientDrawable drawable = (GradientDrawable) _imageView.getDrawable();
                Bitmap bitmap = Bitmap.createBitmap(_imageView.getWidth(), _imageView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);


                int pixel = bitmap.getPixel(x, y);
                if (Color.alpha(pixel) != 0) {
                    Toast.makeText(_context, "Touché", Toast.LENGTH_SHORT).show();
                    _vibrator.vibrate(100);
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) _layout.getLayoutParams();
                    params.gravity = params.gravity == Gravity.LEFT ? Gravity.RIGHT : Gravity.LEFT;
                    _wm.updateViewLayout(_layout, params);
                    return true;
                }
                break;
        }

        return false;
    }
}
