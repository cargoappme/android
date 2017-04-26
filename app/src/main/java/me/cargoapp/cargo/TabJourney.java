package me.cargoapp.cargo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.cargoapp.cargo.event.ShowOverlayAction;
import me.cargoapp.cargo.service.OverlayService_;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@EFragment(R.layout.tab_journey)
public class TabJourney extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    final int AUTOCOMPLETE_REQUEST_CODE = 1;
    final String CAR_CHECKS_DIALOG_TAG = "CAR_CHECKS_DIALOG";

    @ViewById(R.id.go)
    Button _btn;

    GoogleApiClient _googleClient;
    Location _currentLocation;
    Place _targetPlace;

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

        EventBus.getDefault().register(this);
        _googleClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
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
            _targetPlace = PlaceAutocomplete.getPlace(getActivity(), data);
            Location targetLocation = new Location("");
            targetLocation.setLatitude(_targetPlace.getLatLng().latitude);
            targetLocation.setLongitude(_targetPlace.getLatLng().longitude);
            float distanceInMeters = targetLocation.distanceTo(_currentLocation);

            if (distanceInMeters >= 100 * 1000) {
                new CarChecksDialogFragment().show(getFragmentManager(), CAR_CHECKS_DIALOG_TAG);
            } else {
                EventBus.getDefault().post(new StartJourneyAction());
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(getActivity(), data);
            // TODO: Handle the error.
            Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    @Subscribe
    public void onStartJourney(StartJourneyAction action) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + _targetPlace.getAddress()));
        startActivity(intent);

        Intent overlayServiceIntent = new Intent(getActivity(), OverlayService_.class);
        getActivity().startService(overlayServiceIntent);
        getActivity().finish();
    }

    public static class StartJourneyAction {}
    public static class CarChecksDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setTitle(R.string.dialog_vehiclechecks_title)
                    .setView(R.layout.dialog_vehicle_checks)
                    .setPositiveButton(R.string.dialog_vehiclechecks_action_go, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EventBus.getDefault().post(new StartJourneyAction());
                        }
                    })
                    .setNegativeButton(R.string.dialog_vehiclechecks_action_back, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}