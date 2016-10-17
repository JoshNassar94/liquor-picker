package srdesign.liquorpicker;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private JSONArray mBars;
    private HashMap<String, String> barMap;
    private Marker myMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_map);
        Intent intent = getIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        barMap = new HashMap<>();
        getBarsJSON(intent.getStringExtra("BARS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else {
                showBars();
                handleNewLocation(location);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void handleNewLocation(Location location) {
        if(myMarker != null)
            myMarker.remove();
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        myMarker = mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void getBarsJSON(String barString){
        try {
            mBars = new JSONArray(barString);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private void showBars(){
        try {
            for (int i = 0; i < mBars.length(); ++i) {
                JSONObject jObject = mBars.getJSONObject(i);
                Double lat = Double.parseDouble(jObject.getString("Lat"));
                Double lon = Double.parseDouble(jObject.getString("Lon"));
                LatLng latLng = new LatLng(lat, lon);
                String website = jObject.getString("Website");
                String idBars = jObject.getString("idBars");
                String name = jObject.getString("Name");
                if(!website.equals(""))
                    barMap.put(website, idBars);
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .snippet(website)
                        .title(name);
                mMap.addMarker(options);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if(!marker.getTitle().equals("I am here!"))
                            MapsActivity.this.sendMessage(marker);
                    }
                });
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(Marker marker) {
        Intent intent = new Intent(this, DealsList.class);
        String id = barMap.get(marker.getSnippet());
        String query = null;

        //Get all of the deals for this bar
        BasicQuery dealQuery = new BasicQuery();
        query = "http://cise.ufl.edu/~jnassar/liquor-picker/getDeals.php?id=" + id + "&valid=0";
        dealQuery.execute(query);

        String deals = null;
        do {
            deals = dealQuery.getContent();
        }while(deals == null);

        intent.putExtra("Deals", deals);
        intent.putExtra("Title", marker.getTitle());
        startActivity(intent);
    }
}
