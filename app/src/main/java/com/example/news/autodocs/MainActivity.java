package com.example.news.autodocs;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleMap.OnMarkerClickListener {

    //this is a test change
    Context mContext;
    GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    ArrayList<LatLng> latlngs;
    LatLng latLng;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    double mlatitude,mlongitude;
    Boolean done;
    Boolean requesr;
    LocalService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=MainActivity.this;
        done=false;
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //service
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("intentKey"));
        Button b=(Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialogShow();
                if (mBound) {
                    // Call a method from the LocalService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    //mService.RequestMechanic(String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude), 1, marker.getTag().toString());
                    //Toast.makeText(MainActivity.this, "number: " + num, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "error running service", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("key");
            //TextView tv=(TextView)findViewById(R.id.mytextview);
            //tv.setText(message);
            //requesr=true;
             Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            if(message.equalsIgnoreCase("success"));
            {
                mydialog.dismiss();
                //requesr=false;
                AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Request Accepted");
                alertDialog.setMessage("Mechanic accepted the request, have a good day :)");
                alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Okay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show();
            }

        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }

    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute) */
    public void onButtonClick(View v) {

        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            //mService.RequestMechanic();
            //Toast.makeText(this, "check log: " + num, Toast.LENGTH_SHORT).show();
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private void AllMechanics(){
        APIMyInterface apiInterface= APIClient.getApiClient().create(APIMyInterface.class);
        Call<List<Mechanic>> call=apiInterface.MechanicsNearBy(String.valueOf(mlatitude),String.valueOf(mlongitude));
        call.enqueue(new Callback<List<Mechanic>>() {
            @Override
            public void onResponse(Call<List<Mechanic>> call, Response<List<Mechanic>> response) {
                List<Mechanic> c=response.body();
                //Toast.makeText(mContext, "Server response: "+ c.get(0).response, Toast.LENGTH_LONG).show();
                if(c.get(0).response.equalsIgnoreCase("success")) {
                    latlngs = new ArrayList<>();
                    for (final Mechanic item:c) {
                        latLng = new LatLng(Double.parseDouble(item.lng),Double.parseDouble(item.lat));
                        //google map markers
                        MarkerOptions options = new MarkerOptions();
                        //get all markers and locations
                        builder.include(latLng);
                        options.position(latLng);
                        options.title(item.name);
                        options.snippet(item.phone);
                        mGoogleMap.addMarker(options).setTag(item.id);

                    }
                    // Set the camera to the greatest possible zoom level that includes the bounds
                    mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
                            LatLngBounds bounds = builder.build();
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                        }
                    });
                    mGoogleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) mContext);

                    //  Toast.makeText(mContext, "Welcome "+c.get(0).name, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, "Server response: "+c.get(0).response, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Mechanic>> call, Throwable t) {
                Toast.makeText(mContext, "Fail "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //google map location code bellow
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        markerOptions.draggable(true);
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);



        mlatitude = (double) (location.getLatitude());
        mlongitude = (double) (location.getLongitude());
        if(!done){
            done=true;
            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
            AllMechanics();
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                //UserWithRequest Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        //if (mGoogleApiClient != null) {
        //  LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //}
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        //Toast.makeText(MainActivity.this, "marker clicked", Toast.LENGTH_SHORT).show();
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            //Toast.makeText(MainActivity.this, "bound", Toast.LENGTH_SHORT).show();
            dialogShow(marker);
            //mService.RequestMechanic(String.valueOf(marker.getPosition().latitude),String.valueOf(marker.getPosition().longitude),"1",marker.getTag().toString());
            //String.valueOf(marker.getPosition().latitude),String.valueOf(marker.getPosition().longitude),"1",marker.getTag().toString()
            //Toast.makeText(MainActivity.this, "number: " + num, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "error running service", Toast.LENGTH_SHORT).show();
        }
        //String name= marker.getTitle();
        //Toast.makeText(MainActivity.this, "marker clicked", Toast.LENGTH_SHORT).show();


        //if ("sda".equalsIgnoreCase("My Spot"))
        //{
            //write your code here
        //}
        return true;
    }

    Dialog mydialog;
    //final Marker marker
    private void dialogShow(final Marker marker) {
        mydialog=new Dialog(this);

        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mydialog.setTitle("Help Session");

        mydialog.setContentView(R.layout.dialog);
        RadioButton BreakDown = (RadioButton)mydialog. findViewById(R.id.carBreakdown);
        RadioButton CarMain = (RadioButton)mydialog.findViewById(R.id.carMaint);
        RadioGroup rg = (RadioGroup)mydialog.findViewById(R.id.radio1);
        RadioButton selectedButton = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        final Button request=(Button)mydialog.findViewById(R.id.request);
       // Button Back=(Button)mydialog.findViewById(R.id.Back);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mService.RequestMechanic(String.valueOf(marker.getPosition().latitude),String.valueOf(marker.getPosition().longitude),"1",marker.getTag().toString());
                request.setEnabled(false);
                request.setText("Requesting");
            }
        });
        mydialog.show();
    }
}
