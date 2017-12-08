package com.example.news.autodocs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }
}
