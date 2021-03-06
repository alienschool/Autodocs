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
import android.widget.CheckBox;
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
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleMap.OnMarkerClickListener {

    //this is a test change
    Context mContext;
    // Session Manager Class
    SessionManager session;
    String SessionId,SessionEmail,SessionPassword;

    GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    ArrayList<LatLng> latlngs;
    LatLng latLng;
    Location mLastLocation;
    List<Marker> friendMarkers = new ArrayList<>();
    Marker mCurrLocationMarker;
    MarkerOptions options;

    double mlatitude;
    double mlongitude;
    String mMechanicId;
    Boolean done;
    Boolean requesr;
    LocalService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        SessionId = user.get(SessionManager.KEY_NAME);

        // email
        SessionEmail = user.get(SessionManager.KEY_EMAIL);

        // password
        SessionPassword = user.get(SessionManager.KEY_PASSWORD);
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
                FindNearestMechanic();
            }
        });
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getExtras().get("key").toString();
            //TextView tv=(TextView)findViewById(R.id.mytextview);
            //tv.setText(message);
            //requesr=true;
             Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            if(message.equalsIgnoreCase("success"))
            {
                if(mydialog.isShowing()){
                    mydialog.dismiss();
                }
                final String requestId = intent.getExtras().get("requestId").toString();
                //requesr=false;
                AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Request Accepted");
                alertDialog.setMessage("Mechanic accepted the request, have a good day :)");
                alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Okay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                UpdateMechanicLocation(requestId);

                            }
                        });
                alertDialog.show();
            }else if(message.equalsIgnoreCase("rejected")){
                if(mydialog.isShowing()){
                    mydialog.dismiss();
                }

                //requesr=false;
                AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Request Rejected");
                alertDialog.setMessage("Mechanic rejected your request. May be He/She is busy");
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
    public void UpdateMechanicLocation(final String requestId){
        APIMyInterface apiInterface= APIClient.getApiClient().create(APIMyInterface.class);
        //calling php file from here. php will return success
        Call<UserWithRequest> call=apiInterface.getMechanicLocation(requestId);
        call.enqueue(new Callback<UserWithRequest>() {
            @Override
            public void onResponse(Call<UserWithRequest> call, Response<UserWithRequest> response) {
                UserWithRequest c=response.body();
                if(c.response.equalsIgnoreCase("success")){
                    Toast.makeText(MainActivity.this, "mechanic location updated ", Toast.LENGTH_LONG).show();
                    Marker mMarker = null;
                    for (Marker marker : friendMarkers) {
                        if (marker.getTag().equals(c.mechanicId)) {
                            mMarker=marker;
                            mMechanicId=c.mechanicId;
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            marker.setPosition(new LatLng(Double.parseDouble(c.mechanicLat),Double.parseDouble(c.mechanicLng)));
                            UpdateMechanicLocation(requestId);
                        }else{
                            marker.remove();
                            //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        }
                    }
                    friendMarkers.clear();
                    friendMarkers.add(mMarker);
                }else if(c.response.equalsIgnoreCase("canceled")){
                    Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_LONG).show();

                    AlertDialog cancelDialog=new AlertDialog.Builder(MainActivity.this).create();
                    cancelDialog.setTitle("Request Canceled");
                    cancelDialog.setMessage("Mechanic canceled your request");
                    cancelDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    unbindService(mConnection);
                                    // Bind to LocalService
                                    Intent intent = new Intent(mContext, LocalService.class);
                                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                                    for (Marker marker : friendMarkers) {
                                        if (marker.getTag().equals(mMechanicId)) {
                                            marker.remove();
                                        }
                                    }
                                    AllMechanics();
                                    dialog.dismiss();

                                }
                            });
                    cancelDialog.show();
                }else if(c.response.equalsIgnoreCase("finished")){
                    Toast.makeText(MainActivity.this, "Finished", Toast.LENGTH_LONG).show();
                    AlertDialog finishDialog=new AlertDialog.Builder(MainActivity.this).create();
                    finishDialog.setTitle("Request Finished");
                    finishDialog.setMessage("Job completed!");
                    finishDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    unbindService(mConnection);
                                    // Bind to LocalService
                                    Intent intent = new Intent(mContext, LocalService.class);
                                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                                    for (Marker marker : friendMarkers) {
                                        if (marker.getTag().equals(mMechanicId)) {
                                            marker.remove();
                                        }
                                    }
                                    dialog.dismiss();

                                }
                            });
                    finishDialog.show();
                }
                else {
                    Toast.makeText(MainActivity.this,"Server response: "+c.response, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<UserWithRequest> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void FindNearestMechanic(){
        APIMyInterface apiInterface= APIClient.getApiClient().create(APIMyInterface.class);
        //calling php file from here. php will return success
        Call<UserWithRequest> call=apiInterface.FindNearestMechanic(String.valueOf(mlatitude), String.valueOf(mlongitude));
        call.enqueue(new Callback<UserWithRequest>() {
            @Override
            public void onResponse(Call<UserWithRequest> call, Response<UserWithRequest> response) {
                UserWithRequest c=response.body();
                if(c.response.equalsIgnoreCase("success")){
                    Toast.makeText(MainActivity.this, "found "+c.mechanicId, Toast.LENGTH_LONG).show();
                    for (Marker marker : friendMarkers) {
                        if (marker.getTag().equals(c.mechanicId)) {
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }else{
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,"Server response: "+c.response, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<UserWithRequest> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
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
                        options = new MarkerOptions();
                        //get all markers and locations
                        builder.include(latLng);
                        options.position(latLng);
                        options.title(item.name);
                        options.snippet(item.phone);
                        Marker friendMarker=mGoogleMap.addMarker(options);
                        friendMarker.setTag(item.id);
                        friendMarkers.add(friendMarker);


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
        if (mBound) {
            if(marker.getTag()!=null){
                dialogShow(marker);
            }
        }else{
            Toast.makeText(MainActivity.this, "error running service", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    Dialog mydialog;
    //final Marker marker
    private void dialogShow(final Marker marker) {
        mydialog=new Dialog(this);

        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mydialog.setTitle("Help Session");

        mydialog.setContentView(R.layout.dialog);
        final RadioButton BreakDown = (RadioButton)mydialog. findViewById(R.id.carBreakdown);
        final RadioButton CarMain = (RadioButton)mydialog.findViewById(R.id.carMaint);
        RadioGroup rg = (RadioGroup)mydialog.findViewById(R.id.radio1);
        final CheckBox flatTyre=(CheckBox)mydialog.findViewById(R.id.chck1);
        final CheckBox HeatIssue=(CheckBox)mydialog.findViewById(R.id.chck2);
        final CheckBox BatteryDown=(CheckBox)mydialog.findViewById(R.id.chck3);
        final CheckBox Other=(CheckBox)mydialog.findViewById(R.id.chck4);
final Boolean c;
        BreakDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flatTyre.setEnabled(true);
                HeatIssue.setEnabled(true);
                BatteryDown.setEnabled(true);
                Other.setEnabled(true);
            }
        });
        CarMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flatTyre.setEnabled(false);
                HeatIssue.setEnabled(false);
                BatteryDown.setEnabled(false);
                Other.setEnabled(false);
            }
        });

        RadioButton selectedButton = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        final Button request=(Button)mydialog.findViewById(R.id.request);
       // Button Back=(Button)mydialog.findViewById(R.id.Back);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CarMain.isChecked()||(BreakDown.isChecked()
                        &&(flatTyre.isChecked()||HeatIssue.isChecked()||BatteryDown.isChecked()||Other.isChecked()))) {
                    mService.RequestMechanic(String.valueOf(mlatitude), String.valueOf(mlongitude), SessionId, marker.getTag().toString());
                    request.setEnabled(false);
                    request.setText("Requesting");
                }
                else{
                    Toast.makeText(mContext, "Please select the Mechanic type", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mydialog.show();
    }
}
