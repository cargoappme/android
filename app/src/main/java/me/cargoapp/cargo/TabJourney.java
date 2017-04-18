package me.cargoapp.cargo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@EFragment(R.layout.tab_journey)
public class TabJourney extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @ViewById(R.id.go)
    Button _btn;

    GoogleApiClient _googleClient;
    Location _currentLocation;

    @AfterViews
    void afterViews() {
        _googleClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void onStart() {
        super.onStart();

        _googleClient.connect();
    }

    public void onStop() {
        super.onStop();

        _googleClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        _currentLocation = LocationServices.FusedLocationApi.getLastLocation(_googleClient);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {}

    @Override
    public void onConnectionSuspended(int code) {}

    @Click(R.id.go)
    void onGo() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @OnActivityResult(AUTOCOMPLETE_REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(getActivity(), data);
            Location targetLocation = new Location("");
            targetLocation.setLatitude(place.getLatLng().latitude);
            targetLocation.setLongitude(place.getLatLng().longitude);
            float distanceInMeters = targetLocation.distanceTo(_currentLocation);

            Toast.makeText(getActivity(), String.valueOf(distanceInMeters), Toast.LENGTH_SHORT).show();
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(getActivity(), data);
            // TODO: Handle the error.
            Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }
}