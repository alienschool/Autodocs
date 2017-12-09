package com.example.news.autodocs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signin extends AppCompatActivity {
Button signin;
    EditText usernaem,Password;
    TextView signup;
    SharedPreferences ss;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ss=getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        editor=ss.edit();
        signin=(Button)findViewById(R.id.login_login_button);
        usernaem=(EditText) findViewById(R.id.login_username_editText);
        signup=(TextView) findViewById(R.id.login_register_textView);
        Password=(EditText)findViewById(R.id.login_password_editText);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(usernaem.getText().toString().equals("")||Password.getText().toString().equals("")))
                {
                    APIMyInterface apiMyInterface=APIClient.getApiClient().create(APIMyInterface.class);
                    Call<User>call=apiMyInterface.Login(usernaem.getText().toString(),Password.getText().toString());
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            String res = response.body().response;
                            if (res .equals("Sucess")) {
                                AlertDialog alertDialog = new AlertDialog.Builder(Signin.this).create();
                                alertDialog.setTitle("Log in");
                                alertDialog.setMessage("Remember this Account? ");
                                alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                editor.putString("Login", "User");
                                                editor.commit();
                                                dialog.dismiss();
                                                Intent intent = new Intent(Signin.this, MainActivity.class);
                                                startActivity(intent);


                                            }
                                        });
                                alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(Signin.this, MainActivity.class);
                                                startActivity(intent);


                                            }

                                        });

                                alertDialog.show();
                            }
                            else
                            {
                                Toast.makeText(Signin.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Signin.this,Signup.class);
                startActivity(intent);
            }
        });
    }
}
