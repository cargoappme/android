package me.cargoapp.cargo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import es.dmoral.toasty.Toasty;
import me.cargoapp.cargo.helper.IntentHelper;
import me.cargoapp.cargo.service.JourneyService_;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@EFragment(R.layout.tab_journey)
public class TabJourney extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final int AUTOCOMPLETE_REQUEST_CODE = 1;
    final String CAR_CHECKS_DIALOG_TAG = "CAR_CHECKS_DIALOG";

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.fab_menu)
    FloatingActionMenu _fabMenu;

    GoogleApiClient _googleClient;
    Location _currentLocation;
    Location _targetLocation;
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

        _googleClient.connect();
    }

    @Override
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
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onConnectionSuspended(int code) {
    }

    @Click(R.id.btn_go)
    void onGo() {
        _fabMenu.close(true);

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Click(R.id.btn_start)
    void onBtnStart() {
        _fabMenu.close(true);

       _eventBus.post(new StartJourneyAction(false));
    }

    @OnActivityResult(AUTOCOMPLETE_REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            _targetPlace = PlaceAutocomplete.getPlace(getActivity(), data);
            _targetLocation = new Location("");
            _targetLocation.setLatitude(_targetPlace.getLatLng().latitude);
            _targetLocation.setLongitude(_targetPlace.getLatLng().longitude);
            float distanceInMeters = _targetLocation.distanceTo(_currentLocation);

            if (distanceInMeters >= 100 * 1000) {
                new CarChecksDialogFragment().show(getFragmentManager(), CAR_CHECKS_DIALOG_TAG);
            } else {
                _eventBus.post(new StartJourneyAction(true));
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(getActivity(), data);
            // TODO: Handle the error.
            Toasty.error(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG, true).show();
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    @Subscribe
    public void onStartJourney(StartJourneyAction action) {
        if (action.withSharing) {
            Intent intent = IntentHelper.createNavigationIntent(_targetPlace.getLatLng().latitude, _targetPlace.getLatLng().longitude, _targetPlace.getAddress().toString());
            startActivity(intent);
        }

        Application_.isJourneyStarted = true;
        Application_.journeyWithSharing = action.withSharing;
        Application_.journeyDestination = _targetLocation;
        Intent overlayServiceIntent = new Intent(getActivity(), JourneyService_.class);
        getActivity().startService(overlayServiceIntent);
        getActivity().finish();
    }

    public static class StartJourneyAction {
        public boolean withSharing;

        public StartJourneyAction(boolean withSharing) {
            this.withSharing = withSharing;
        }
    }

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
                            EventBus.getDefault().post(new StartJourneyAction(true));
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