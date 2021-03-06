package me.cargoapp.cargo;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.PageSelected;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import me.cargoapp.cargo.helper.IntentHelper;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private String TAG = this.getClass().getSimpleName();

    @Pref
    ParkingStore_ _parkingStore;

    @ViewById(R.id.pager)
    ViewPager _viewPager;

    @ViewById(R.id.tab_layout)
    TabLayout _tabLayout;

    @ViewById(R.id.parking_header)
    View _parkingHeader;

    @AfterViews
    void afterViews() {
        _tabLayout.addTab(_tabLayout.newTab().setText(R.string.tab_journey));
        _tabLayout.addTab(_tabLayout.newTab().setText(R.string.tab_history));
        _tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        _tabLayout.addOnTabSelectedListener(this);

        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), _tabLayout.getTabCount());
        _viewPager.setAdapter(adapter);
    }

    private void _handleParkingHeader() {
        _parkingHeader.setVisibility(_parkingStore.hasPositionSaved().get() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        this._handleParkingHeader();
    }

    @OptionsItem(R.id.action_settings)
    void onActionSettings() {
        startActivity(new Intent(this, SettingsActivity_.class));
    }


    @PageSelected(R.id.pager)
    void onPageSelected(ViewPager view, int position) {
        _tabLayout.getTabAt(position).select();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        _viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

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
                    return new TabHistory_();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Click(R.id.btn_go_to_parking)
    void onGoToParking() {
        double lat = _parkingStore.latitude().get();
        double lon = _parkingStore.longitude().get();
        Intent intent = IntentHelper.INSTANCE.createNavigationIntent(lat, lon, lat + "," + lon + "(Parking)");
        startActivity(intent);
    }

    @Click(R.id.btn_delete_parking)
    void onDeleteParking() {
        _parkingStore.hasPositionSaved().put(false);

        this._handleParkingHeader();
    }
}
