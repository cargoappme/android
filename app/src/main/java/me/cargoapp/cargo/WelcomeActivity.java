package me.cargoapp.cargo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PageSelected;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;

import me.cargoapp.cargo.helper.PermissionHelper;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity
public class WelcomeActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();

    @ViewById(R.id.view_pager)
    ViewPager _viewPager;

    @ViewById(R.id.dots_layout)
    LinearLayout _dotsLayout;

    @ViewById(R.id.btn_next)
    Button _btnNext;

    @Pref
    Preferences_ _preferences;

    private int[] _layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] dangerousAndSpecialPermissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
        };


        // Checking for first time launch or all perms - before calling setContentView()
        if (!_preferences.isFirstRun().get() && PermissionHelper.isPermittedTo(this, dangerousAndSpecialPermissions)) {
            launchHomeScreen();
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        _layouts = new int[]{
                R.layout.welcome_slide_1,
                R.layout.welcome_slide_perms
        };

        // adding bottom _dots
        addBottomDots(0);

        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter();
        _viewPager.setAdapter(viewPagerAdapter);
    }

    @Click(R.id.btn_next)
    void onNextClick() {
        // checking for last page
        // if last page home screen will be launched
        int current = getItem(+1);
        if (current < _layouts.length) {
            // move to next screen
            _viewPager.setCurrentItem(current);
        } else {
            requestPerms();
        }
    }

    private void requestPerms() {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestSpecialPerms();
                } else {
                    requestPerms();
                }
            }
        }
    }

    void requestSpecialPerms() {
        Toast.makeText(this, R.string.enable_perm, Toast.LENGTH_SHORT).show();

        if (!PermissionHelper.isPermittedTo(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        if (!PermissionHelper.isPermittedTo(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0: {
                requestSpecialPerms();
                return;
            }
            case 1: {
                if (!PermissionHelper.isPermittedTo(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    requestSpecialPerms();
                } else {
                    launchHomeScreen();
                }
            }
        }
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[_layouts.length];

        int colorActive = getResources().getColor(R.color.slider_dot_active, null);
        int colorInactive = getResources().getColor(R.color.slider_dot_inactive, null);

        _dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_COMPACT));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorInactive);
            _dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorActive);
    }

    private int getItem(int i) {
        return _viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        _preferences.isFirstRun().put(false);
        MainActivity_.intent(this).start();
        finish();
    }

    @PageSelected(R.id.view_pager)
    void onPageSelected(ViewPager view, int position) {
        addBottomDots(position);

        // changing the next button text 'NEXT' / 'GOT IT'
        if (position == _layouts.length - 1) {
            // last page. make button text to GOT IT
            _btnNext.setText(getString(R.string.slider_start));
        } else {
            // still pages are left
            _btnNext.setText(getString(R.string.slider_next));
        }
    }

    /**
     * View pager adapter
     */
    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(_layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return _layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
