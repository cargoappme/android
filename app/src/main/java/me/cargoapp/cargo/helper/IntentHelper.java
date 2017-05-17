package me.cargoapp.cargo.helper;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by Marvin on 16/05/2017.
 */

public class IntentHelper {

    public static Intent createNavigationIntent(double lat, double lon, String query) {
        Uri intentUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + query);
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, intentUri);

        return navigationIntent;
    }
}
