package me.cargoapp.cargo;

import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceScreen;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {

    @AfterViews
    void afterViews() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsActivity_.SettingsFragment_())
                .commit();
    }

    @PreferenceScreen(R.xml.preferences)
    @EFragment
    public static class SettingsFragment extends PreferenceFragment {

    }
}
