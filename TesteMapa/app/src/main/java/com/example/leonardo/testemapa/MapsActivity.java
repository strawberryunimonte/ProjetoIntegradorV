package com.example.leonardo.testemapa;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleMap mMap;
    protected GoogleApiClient googleApiClient;
    protected Location location;
    protected LocationRequest locationRequest;
    private TextView latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        criarUserGoogleApi();

        latitude = (TextView)findViewById(R.id.latitude);
        longitude = (TextView)findViewById(R.id.longitude);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    protected synchronized void criarUserGoogleApi() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient != null && googleApiClient.isConnected()){
            comecarLocationUpdate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()){
            pararLocationUpdate();
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getBaseContext(),"Passou pela permissão !",Toast.LENGTH_SHORT).show();
        mMap.setMyLocationEnabled(true);
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (location != null){
                Toast.makeText(getBaseContext(),"Não está vazio",Toast.LENGTH_SHORT).show();
                System.out.println(location.getLatitude());
                System.out.println(location.getLongitude());
            }else {
                Toast.makeText(getBaseContext(), "Está vazio!", Toast.LENGTH_SHORT).show();
            }

        comecarLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getBaseContext(),"Conexão suspendida!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getBaseContext(),"Falha na conexão!",Toast.LENGTH_SHORT).show();
    }

    public void requestLocation(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void comecarLocationUpdate(){
        requestLocation();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,MapsActivity.this);
    }

    public void pararLocationUpdate(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,MapsActivity.this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude.setText(String.valueOf(location.getLatitude()));
        longitude.setText(String.valueOf(location.getLongitude()));
        Toast.makeText(getBaseContext(),"Localização Atualizada!",Toast.LENGTH_SHORT).show();
    }
}
