package com.example.news.autodocs;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MechanicActivity extends AppCompatActivity {

    LocalService mService;
    boolean mBound = false;
    //this is new
    UserWithRequest userWithRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);
        userWithRequest=new UserWithRequest();
        //service
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRequestReceiver, new IntentFilter("RequestToMechanic"));
    }
    private BroadcastReceiver mRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getExtras().get("key").toString();
            if(message.equalsIgnoreCase("success")){
                userWithRequest.name = intent.getExtras().get("name").toString();
                userWithRequest.userLat = intent.getExtras().get("userLat").toString();
                userWithRequest.userLng = intent.getExtras().get("userLng").toString();
                userWithRequest.helpType = intent.getExtras().get("helpType").toString();
                userWithRequest.id = intent.getExtras().get("id").toString();
               // Toast.makeText(context, name, Toast.LENGTH_LONG).show();
                dialogShow();
            }
            //UserWithRequest userWithRequest= (UserWithRequest) intent.getSerializableExtra("UserWithRequest");
            //TextView tv=(TextView)findViewById(R.id.mytextview);
            //tv.setText(message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    };
    private void dialogShow() {
        final Dialog mydialog=new Dialog(this);

        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mydialog.setContentView(R.layout.mechanic_dialog);
        TextView tt=(TextView)mydialog.findViewById(R.id.Name);
        tt.setText(userWithRequest.name);
        Button accept=(Button)mydialog.findViewById(R.id.yes);
         Button reject=(Button)mydialog.findViewById(R.id.no);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AcceptRequest(userWithRequest.id);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mydialog.show();
    }
    public void AcceptRequest( final String requestId){
        APIMyInterface apiInterface= APIClient.getApiClient().create(APIMyInterface.class);
        //calling php file from here. php will return success
        Call<UserWithRequest> call=apiInterface.AcceptRequest(requestId);
        call.enqueue(new Callback<UserWithRequest>() {
            @Override
            public void onResponse(Call<UserWithRequest> call, Response<UserWithRequest> response) {
                UserWithRequest c=response.body();
                if(c.response.equalsIgnoreCase("success")){
                    Toast.makeText(MechanicActivity.this, "accepted ", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(MechanicActivity.this,MapActivity.class);
                    i.putExtra("lat",userWithRequest.userLat);
                    i.putExtra("lng",userWithRequest.userLng);
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(MechanicActivity.this,"Server response: "+c.response, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<UserWithRequest> call, Throwable t) {
                Toast.makeText(MechanicActivity.this, "Fail "+t.getMessage(), Toast.LENGTH_LONG).show();
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
