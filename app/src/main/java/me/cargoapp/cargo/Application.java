package me.cargoapp.cargo;

import com.facebook.soloader.SoLoader;

import org.androidannotations.annotations.EApplication;

/**
 * Created by Marvin on 10/05/2017.
 */

@EApplication
public class Application extends android.app.Application {
    public static boolean isJourneyStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();

        SoLoader.init(this, false);
    }
}
