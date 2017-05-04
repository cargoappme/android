package me.cargoapp.cargo.navui;

/**
 * Created by Mathieu on 04/05/2017.
 */

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.NavuiLaunchEvent;

/**
 * Created by Mathieu on 04/05/2017.
 */
@EFragment(R.layout.fragment_navui_parking)
public class NavuiParking extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient _googleClient;
    private Location mLastLocation;
    Long latitude;
    Long longitude;

    @AfterViews
    void afterViews() {
        _googleClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onStart() {
        super.onStart();
        _googleClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        _googleClient.disconnect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    _googleClient);
            if (mLastLocation != null) {
                latitude = Double.doubleToLongBits(mLastLocation.getLatitude());
                longitude = Double.doubleToLongBits(mLastLocation.getLongitude());

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                Log.i("TAG", String.valueOf(Double.longBitsToDouble(preferences.getLong("longitude", 0))));
                SharedPreferences.Editor editor = preferences.edit();
                if (preferences.contains("longitude") && preferences.contains("latitude")) {
                    editor.remove("longitude");
                    editor.remove("latitude");
                }
                editor.putLong("longitude", longitude);
                editor.putLong("latitude", latitude);
                editor.commit();
                Toast.makeText(getContext(), "Votre position est bien enregistrée !", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.MENU));


            } else {
                Toast.makeText(getContext(), "Impossible de récupérer votre position :(", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.MENU));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}

