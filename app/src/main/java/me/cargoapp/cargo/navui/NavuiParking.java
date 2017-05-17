package me.cargoapp.cargo.navui;

/**
 * Created by Mathieu on 04/05/2017.
 */

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

import es.dmoral.toasty.Toasty;
import me.cargoapp.cargo.ParkingStore_;
import me.cargoapp.cargo.event.NavuiLaunchEvent;

/**
 * Created by Mathieu on 04/05/2017.
 */
@EFragment
public class NavuiParking extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient _googleClient;
    private Location _lastLocation;

    @Pref
    ParkingStore_ _parkingStore;

    @AfterInject
    void afterInject() {
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
            _lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    _googleClient);
            if (_lastLocation != null) {
                _parkingStore.latitude().put((float) _lastLocation.getLatitude());
                _parkingStore.longitude().put((float) _lastLocation.getLongitude());
                _parkingStore.hasPositionSaved().put(true);
                Toasty.success(getContext(), "Votre position est bien enregistrée !", Toast.LENGTH_LONG, true).show();
            } else {
                Toasty.error(getContext(), "Impossible de récupérer votre position :(", Toast.LENGTH_LONG, true).show();
            }

            EventBus.getDefault().post(new NavuiLaunchEvent(NavuiLaunchEvent.Type.MENU));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}

