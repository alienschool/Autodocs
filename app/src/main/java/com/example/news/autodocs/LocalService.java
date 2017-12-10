package com.example.news.autodocs;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocalService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    private Handler handler;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients
     * @param lat
     * @param lng
     * @param userId
     * @param mechanicId*/
    /*public int getRandomNumber() {
        handler=new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AllMechanics();
            }
        },1000);
        return mGenerator.nextInt(100);
    }

    public Runnable runnable=new Runnable() {
        @Override
        public void run() {
            mGenerator.nextInt(100);

        }
    };*/
    private int i=0;
    public void RequestMechanic(final String lat, final String lng, final String userId, final String mechanicId){
        APIMyInterface apiInterface= APIClient.getApiClient().create(APIMyInterface.class);
        //calling php file from here. php will return success
        Call<Mechanic> call=apiInterface.RequestAMechanic(lat,lng,userId,mechanicId,"Flat Tyre");
        call.enqueue(new Callback<Mechanic>() {
            @Override
            public void onResponse(Call<Mechanic> call, Response<Mechanic> response) {
                Mechanic c=response.body();
                if(c.response.equalsIgnoreCase("wait")){
                    i=1;
                    if(i==0){
                        Intent intent = new Intent("intentKey");
                        // You can also include some extra data.
                        intent.putExtra("key", c.response+i);
                        LocalBroadcastManager.getInstance(LocalService.this).sendBroadcast(intent);
                    }
                    RequestMechanic(lat,lng,userId,mechanicId);
                }
                else if(c.response.equalsIgnoreCase("success")){
                    Intent intent = new Intent("intentKey");
                    // You can also include some extra data.
                    intent.putExtra("key", c.response);
                    LocalBroadcastManager.getInstance(LocalService.this).sendBroadcast(intent);

                }else{
                    Intent intent = new Intent("intentKey");
                    // You can also include some extra data.
                    intent.putExtra("key", c.response);
                    LocalBroadcastManager.getInstance(LocalService.this).sendBroadcast(intent);
                }
            }
            @Override
            public void onFailure(Call<Mechanic> call, Throwable t) {
                //Toast.makeText(mContext, "Fail "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private int j=0;
    public void CheckForRequest( final String mechanicId){
        APIMyInterface apiInterface= APIClient.getApiClient().create(APIMyInterface.class);
        //calling php file from here. php will return success
        Call<UserWithRequest> call=apiInterface.CheckForRequest(mechanicId);
        call.enqueue(new Callback<UserWithRequest>() {
            @Override
            public void onResponse(Call<UserWithRequest> call, Response<UserWithRequest> response) {
                UserWithRequest c=response.body();
                if(c.response.equalsIgnoreCase("wait")){
                    j=1;
                    if(j==0){
                        Intent intent = new Intent("RequestToMechanic");
                        // You can also include some extra data.
                        intent.putExtra("key", c.response+j);
                        LocalBroadcastManager.getInstance(LocalService.this).sendBroadcast(intent);
                    }
                    CheckForRequest(mechanicId);
                }
                else if(c.response.equalsIgnoreCase("success")){
                    Intent intent = new Intent("RequestToMechanic");
                    // You can also include some extra data.
                    intent.putExtra("key", c.response);
                    intent.putExtra("name", c.name);
                    intent.putExtra("userLat", c.userLat);
                    intent.putExtra("userLng", c.userLng);
                    intent.putExtra("helpType", c.helpType);
                    intent.putExtra("id", c.id);
                    LocalBroadcastManager.getInstance(LocalService.this).sendBroadcast(intent);
                }else {
                    //Intent intent = new Intent("RequestToMechanic");
                    // You can also include some extra data.
                    //intent.putExtra("key", c.response+j);
                    //LocalBroadcastManager.getInstance(LocalService.this).sendBroadcast(intent);
                }
            }
            @Override
            public void onFailure(Call<UserWithRequest> call, Throwable t) {
                //Toast.makeText(mContext, "Fail "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}