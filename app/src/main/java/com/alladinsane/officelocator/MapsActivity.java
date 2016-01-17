package com.alladinsane.officelocator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap mMap;
    private ArrayList<OfficeLocation> myOfficeLocations;
    public static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private static LatLng userLocation;
    DistanceCalculator distanceCalculator = new DistanceCalculator();

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setupOfficeLocationsArrayList();

        prepareMapView();
    }
    public void setupOfficeLocationsArrayList()
    {
        myOfficeLocations=new ArrayList<OfficeLocation>();
        myOfficeLocations.clear();
        myOfficeLocations=getIntent().getParcelableArrayListExtra("officeLocations");
    }
    public void prepareMapView()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client=new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestLocationPermissions();
    }
    private void setUpUI()
    {
        ArrayList<String> listViewEntries = new ArrayList<String>();
        listViewEntries.clear();
        ListView listview = (ListView) findViewById(R.id.list);
        if(userLocation!=null)
            sortListByDistance();
        OfficeLocation loc;
        for (int i = 0; i < myOfficeLocations.size(); i++) {
            loc = myOfficeLocations.get(i);
            listViewEntries.add(loc.toString());
            LatLng location = new LatLng(loc.latitude, loc.longitude);
            mMap.addMarker(new MarkerOptions().position(location).title(loc.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        }
        centerMap();

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewEntries);
        listview.setAdapter(itemsAdapter);

        prepareMarkerClickListener();
        prepareListViewClickListener(listview);
    }
    private void prepareMarkerClickListener()
    {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                OfficeLocation myLocation = getThisLocation(title);
                startUpOfficeActivity(myLocation);
                return true;
            }
        });
    }
    private void prepareListViewClickListener(ListView listview)
    {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView) view).getText().toString();

                for (int i = 0; i < myOfficeLocations.size(); i++) {
                    if (item.equals(myOfficeLocations.get(i).toString())) {
                        LatLng currentOffice = new LatLng(myOfficeLocations.get(i).latitude,
                                myOfficeLocations.get(i).longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentOffice));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(7.5f));
                    }
                }

            }
        });
    }
    private void sortListByDistance()
    {
        for (int i = 0; i < myOfficeLocations.size(); i++) {
            OfficeLocation loc = myOfficeLocations.get(i);
            LatLng locationCoordinates = new LatLng(loc.latitude, loc.longitude);
            distanceCalculator.setUserLocation(userLocation);
            distanceCalculator.setCurrentOffice(locationCoordinates);
            double distanceToMarker = distanceCalculator.calculateMyDistanceToMarker();
            loc.setDistance(distanceToMarker);
        }
        Collections.sort(myOfficeLocations, new Comparator<OfficeLocation>() {
            @Override
            public int compare(OfficeLocation o1, OfficeLocation o2) {
                return Double.compare(o1.getDistance(), o2.getDistance());
            }
        });
    }
    private void centerMap()
    {
        LatLng centerOfUS = new LatLng(39.8282, -98.5795);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerOfUS));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(3.2f));
    }
    private OfficeLocation getThisLocation(String title)
    {
        OfficeLocation location = new OfficeLocation();
        for (int i = 0; i < myOfficeLocations.size(); i++) {
            if(myOfficeLocations.get(i).name.equals(title))
                location = myOfficeLocations.get(i);
        }
        return location;
    }
    private void startUpOfficeActivity(OfficeLocation myLocation)
    {
        Intent intent = new Intent(this, OfficeActivity.class);
        Bundle args = new Bundle();
        args.putParcelable("myLocation", myLocation);
        args.putParcelable("userLocation", userLocation);
        intent.putExtra("bundle", args);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.alladinsane.officelocator/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.alladinsane.officelocator/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);

            }
        }
        return;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case  MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.

            }
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    mMap.setMyLocationEnabled(true);
                }
                catch(SecurityException e) {
                }
            } else {
                try {
                    mMap.setMyLocationEnabled(false);
                }
                catch(SecurityException e) {
                }
            }
            return;
        }
    }
    public LatLng getUserLocation()
    {
        return userLocation;
    }
    @Override
    public void onConnected(Bundle bundle) {
        Location location = null;
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(client);
        }
        catch(SecurityException e)
        {
        }
        if (location == null) {
            // Blank for a moment...
        }
        else {
            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            setUpUI();
        };
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        client.connect();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (client.isConnected()) {
            client.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}