package me.cargoapp.cargo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private String TAG = this.getClass().getSimpleName();
    Double longi;
    Double lat;

    @ViewById(R.id.pager)
    ViewPager _viewPager;

    @ViewById(R.id.include)
    View _header;

    @ViewById(R.id.findPark)
    Button _findPark;

    @AfterViews
    void afterViews() {
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_journey));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_vehicle));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains("longitude") && preferences.contains("latitude")) {
            _header.setVisibility(View.VISIBLE);
        }
        else {
            _header.setVisibility(View.GONE);
        }
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        _viewPager.setAdapter(adapter);
        _viewPager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        tabLayout.getTabAt(position).select();
                    }
                });
        tabLayout.addOnTabSelectedListener(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains("longitude") && preferences.contains("latitude")) {
            _header.setVisibility(View.VISIBLE);
        }
        else {
            _header.setVisibility(View.GONE);
        }
    }


    @OptionsItem(R.id.action_settings)
    void onActionSettings() {
        startActivity(new Intent(this, SettingsActivity_.class));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        _viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TabJourney_();
                case 1:
                    return new TabVehicle_();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Click(R.id.findPark)
    void onFind() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        longi = Double.longBitsToDouble(preferences.getLong("longitude", 0));
        lat = Double.longBitsToDouble(preferences.getLong("latitude", 0));
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", lat, longi, "Parking");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("longitude");
        editor.remove("latitude");
        editor.commit();
        _header.setVisibility(View.GONE);
    }
}
