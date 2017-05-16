package me.cargoapp.cargo;

import com.facebook.soloader.SoLoader;

/**
 * Created by Marvin on 10/05/2017.
 */

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SoLoader.init(this, false);
    }
}
