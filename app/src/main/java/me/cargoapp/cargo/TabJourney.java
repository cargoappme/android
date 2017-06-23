package me.cargoapp.cargo;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
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
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import me.cargoapp.cargo.event.overlay.SetOverlayVisibilityAction;
import me.cargoapp.cargo.helper.IntentHelper;
import me.cargoapp.cargo.helper.PermissionHelper;
import me.cargoapp.cargo.service.BackgroundService_;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@EFragment(R.layout.tab_journey)
public class TabJourney extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    final int AUTOCOMPLETE_REQUEST_CODE = 1;
    final String CAR_CHECKS_DIALOG_TAG = "CAR_CHECKS_DIALOG";
    final static String JOURNEY_SHARING_DIALOG_TAG = "JOURNEY_SHARING_DIALOG";

    final static String JOURNEYS_URL = "https://cargo-api.herokuapp.com/journeys";

    @EventBusGreenRobot
    EventBus _eventBus;

    @ViewById(R.id.fab_menu)
    FloatingActionMenu _fabMenu;

    @ViewById(R.id.btn_go)
    FloatingActionButton _fabWithTracking;

    GoogleApiClient _googleClient;
    Location _currentLocation;
    Location _targetLocation;
    Place _targetPlace;

    Intent _backgroundServiceIntent;

    RequestQueue _requestQueue;

    String _journeyToken;
    String _journeySecret;
    boolean _tokenValid = false;

    static Dialog _trackingDialog;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        _googleClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        _backgroundServiceIntent = new Intent(getActivity(), BackgroundService_.class);

        _requestQueue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        _googleClient.connect();

        getActivity().startService(_backgroundServiceIntent);
    }

    @Override
    public void onStop() {
        super.onStop();

        _googleClient.disconnect();

        if (!Application_.isJourneyStarted) getActivity().stopService(_backgroundServiceIntent);
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

    @AfterViews
    void afterViews() {
        if (!PermissionHelper.INSTANCE.isPermittedTo(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            _fabWithTracking.setEnabled(false);
            Toasty.warning(getActivity(), getString(R.string.journey_tracking_disabled_no_permissions), Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.btn_start)
    void onBtnStart() {
        _fabMenu.close(true);

        _eventBus.post(new StartJourneyAction(false));
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
            Intent intent = IntentHelper.INSTANCE.createNavigationIntent(_targetPlace.getLatLng().latitude, _targetPlace.getLatLng().longitude, _targetPlace.getAddress().toString());
            startActivity(intent);
        }

        Application_.isJourneyStarted = true;
        Application_.journeyWithSharing = action.withSharing;
        Application_.journeyDestination = _targetLocation;
        Application_.journeyToken = _journeyToken;
        Application_.journeySecret = _journeySecret;
        Application_.tokenValid = _tokenValid;

        _eventBus.post(new SetOverlayVisibilityAction(true));

        getActivity().finish();
    }

    public static class StartJourneyAction {
        public boolean withSharing;

        public StartJourneyAction(boolean withSharing) {
            this.withSharing = withSharing;
        }
    }

    @Subscribe
    public void onGetTrackingUrl(GetTrackingUrlAction action) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject startObject = new JSONObject();
            startObject.put("latitude", _currentLocation.getLatitude());
            startObject.put("longitude", _currentLocation.getLongitude());

            JSONObject endObject = new JSONObject();
            endObject.put("latitude", _targetLocation.getLatitude());
            endObject.put("longitude", _targetLocation.getLongitude());
            jsonObject.put("start", startObject);
            jsonObject.put("end", endObject);
        } catch (JSONException e) {
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, JOURNEYS_URL, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String token = "";
                        String secret = "";
                        try {
                            token = response.getString("token");
                            secret = response.getString("secret");
                        } catch (JSONException e) {

                        }

                        _journeyToken = token;
                        _journeySecret = secret;
                        _tokenValid = true;

                       TextView urlView = (TextView) _trackingDialog.findViewById(R.id.url);
                        urlView.setText("https://cargoapp.me/track?id=" + token);

                        ImageButton button = (ImageButton) _trackingDialog.findViewById(R.id.share);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_text, "https://cargoapp.me/track?id=" + _journeyToken));
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.sharing_send_to)));
                            }
                        });
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TextView urlView = (TextView) _trackingDialog.findViewById(R.id.url);
                        urlView.setText("-");
                    }
                });

        _requestQueue.add(jsObjRequest);
    }

    public static class GetTrackingUrlAction {

        public GetTrackingUrlAction() {
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
                            DialogFragment dialogFragment = new TrackingUrlDialogFragment();

                            dialogFragment.show(getFragmentManager(), JOURNEY_SHARING_DIALOG_TAG);
                            getFragmentManager().executePendingTransactions();
                            _trackingDialog = dialogFragment.getDialog();

                            EventBus.getDefault().post(new GetTrackingUrlAction());
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

    public static class TrackingUrlDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setTitle(R.string.dialog_share_journey_title)
                    .setView(R.layout.dialog_share_journey)
                    .setPositiveButton(R.string.dialog_share_journey_action_go, new DialogInterface.OnClickListener() {
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