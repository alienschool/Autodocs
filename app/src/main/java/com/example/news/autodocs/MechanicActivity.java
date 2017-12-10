package com.example.news.autodocs;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MechanicActivity extends AppCompatActivity {

    LocalService mService;
    boolean mBound = false;
    //this is new
    UserWithRequest userWithRequest;
    // Session Manager Class
    SessionManager session;
    String SessionId,SessionEmail,SessionPassword;
Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);
        mContext=MechanicActivity.this;
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
                Notification();
                dialogShow();
            }else if(message.equalsIgnoreCase("wait")){
                Toast.makeText(context, "Searching for customer requests in the background.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
            //UserWithRequest userWithRequest= (UserWithRequest) intent.getSerializableExtra("UserWithRequest");
            //TextView tv=(TextView)findViewById(R.id.mytextview);
            //tv.setText(message);

        }
    };
    private void Notification()
    {

        Intent intent= new Intent(MechanicActivity.this, MechanicActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(MechanicActivity.this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("AutoDocs")
                .setContentText("You have new request")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
    private void dialogShow() {
        final Dialog mydialog=new Dialog(this);

        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mydialog.setContentView(R.layout.mechanic_dialog);
        TextView Name=(TextView)mydialog.findViewById(R.id.Name);
        TextView contactno=(TextView)mydialog.findViewById(R.id.Name);
        Name.setText(userWithRequest.name);
        contactno.setText(userWithRequest.name);
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
                mydialog.dismiss();
                RejectRequest(userWithRequest.id);

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
                    Intent i=new Intent(MechanicActivity.this,TheMapActivity.class);
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
    public void RejectRequest( final String requestId){
        APIMyInterface apiInterface= APIClient.getApiClient().create(APIMyInterface.class);
        //calling php file from here. php will return success
        Call<UserWithRequest> call=apiInterface.RejectRequest(requestId);
        call.enqueue(new Callback<UserWithRequest>() {
            @Override
            public void onResponse(Call<UserWithRequest> call, Response<UserWithRequest> response) {
                UserWithRequest c=response.body();
                if(c.response.equalsIgnoreCase("success")){
                    Toast.makeText(MechanicActivity.this, "rejected ", Toast.LENGTH_LONG).show();
                    mService.CheckForRequest(SessionId);
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
            mService.CheckForRequest(SessionId);
            //Toast.makeText(MechanicActivity.this, "service working", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
