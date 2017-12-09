package com.example.news.autodocs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {

    private RadioGroup mRadioGroup;
    private RadioButton radioButton;
    String userType="";
    Button signup;
    Context mContext;
    EditText name,Emai,Address,BilingAdddress,Password,contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext=Signup.this;
        signup=(Button)findViewById(R.id.sbmt);
        name=(EditText)findViewById(R.id.name);
        Emai=(EditText)findViewById(R.id.email);
        Address=(EditText)findViewById(R.id.Address);
        BilingAdddress=(EditText)findViewById(R.id.BAddress);
        Password=(EditText)findViewById(R.id.password);
        contact=(EditText)findViewById(R.id.Cntctnumber);
        mRadioGroup = (RadioGroup) findViewById(R.id.signup_radio);
        userType= "user";
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                if (i == R.id.signup_radioButtonUser) {
                    radioButton = (RadioButton) findViewById(R.id.signup_radioButtonUser);
                } else if (i == R.id.signup_radioButtonMechanic) {
                    radioButton = (RadioButton) findViewById(R.id.signup_radioButtonMechanic);
                }
                userType=radioButton.getTag().toString();
                Toast.makeText(mContext,radioButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equalsIgnoreCase("user")){

                    //userType is user
                    if(!(name.getText().toString().equals("")||Password.getText().toString().equals("")||Emai.getText().toString().equals("")
                            ||BilingAdddress.getText().toString().equals("")||Address.getText().toString().equals("")))
                    {
                        APIMyInterface apiMyInterface=APIClient.getApiClient().create(APIMyInterface.class);
                        Call<User> call=apiMyInterface.SignUpUser(name.getText().toString(),contact.getText().toString(),
                                Address.getText().toString(),BilingAdddress.getText().toString(),"monthly",Emai.getText().toString(),
                                Password.getText().toString());
                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                String res = response.body().response;
                                if (res.equalsIgnoreCase("success")) {
                                    Intent intnt=new Intent(Signup.this,Signin.class);
                                    startActivity(intnt);
                                    finish();
                                }
                                else if(res.equalsIgnoreCase("already registered")){
                                    Emai.setError("Email already registered");
                                }
                                else if(res.equalsIgnoreCase("fail")){
                                    Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(mContext, "Exception: "+t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        Toast.makeText(mContext, "Complete all the field first", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    //userType is mechanic
                    if(!(name.getText().toString().equals("")||Password.getText().toString().equals("")||Emai.getText().toString().equals("")
                            ||BilingAdddress.getText().toString().equals("")||Address.getText().toString().equals("")))
                    {
                        APIMyInterface apiMyInterface=APIClient.getApiClient().create(APIMyInterface.class);
                        Call<User> call=apiMyInterface.SignUpMechanic(name.getText().toString(),contact.getText().toString(),
                                "lat","lng",
                                Emai.getText().toString(),Password.getText().toString());
                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                String res = response.body().response;
                                if (res.equalsIgnoreCase("success")) {
                                    Intent intnt=new Intent(Signup.this,Signin.class);
                                    startActivity(intnt);
                                    finish();
                                }
                                else if(res.equalsIgnoreCase("already registered")){
                                    Emai.setError("Email already registered");
                                }
                                else if(res.equalsIgnoreCase("fail")){
                                    Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(mContext, "Exception: "+t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        Toast.makeText(mContext, "Complete all the field first", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
}
