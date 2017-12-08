package com.example.news.autodocs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PreSignin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_signin);
        Button mechanic=(Button)findViewById(R.id.buttonAsMechanic);
        Button user=(Button)findViewById(R.id.buttonAsUser);
        mechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(PreSignin.this,MechanicActivity.class);
                startActivity(i);
                finish();
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(PreSignin.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button b1=(Button)findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tittle="Notisfication";
                String subject="Its me";
                String body="No it is me";

                NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify=new Notification.Builder
                        (getApplicationContext()).setContentTitle(tittle).setContentText(body).
                        setContentTitle(subject).setSmallIcon(R.drawable.common_google_signin_btn_icon_dark).build();

                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                        notificationIntent, 0);
                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                notif.notify(1, notify);
            }
        });
    }
}
