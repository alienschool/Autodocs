package com.example.news.autodocs;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MechanicActivity extends AppCompatActivity {

    LocalService mService;
    boolean mBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);
        //service
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRequestReceiver, new IntentFilter("RequestToMechanic"));
    }
    private BroadcastReceiver mRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("key");
            if(message.equalsIgnoreCase("success")){
                String name = intent.getStringExtra("name");
                Toast.makeText(context, name, Toast.LENGTH_LONG).show();
            }
            //UserWithRequest userWithRequest= (UserWithRequest) intent.getSerializableExtra("UserWithRequest");
            //TextView tv=(TextView)findViewById(R.id.mytextview);
            //tv.setText(message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.CheckForRequest("1");
            //Toast.makeText(MechanicActivity.this, "service working", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
