package com.example.news.autodocs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {
    Button signup;
    EditText name,Emai,Address,BilingAdddress,Password,contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup=(Button)findViewById(R.id.sbmt);
        name=(EditText)findViewById(R.id.name);
        Emai=(EditText)findViewById(R.id.email);
        Address=(EditText)findViewById(R.id.Address);
        BilingAdddress=(EditText)findViewById(R.id.BAddress);
        Password=(EditText)findViewById(R.id.password);
        contact=(EditText)findViewById(R.id.Cntctnumber);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(name.getText().toString().equals("")||Password.getText().toString().equals("")||Emai.getText().toString().equals("")
                        ||BilingAdddress.getText().toString().equals("")||Address.getText().toString().equals("")))
                {
                    APIMyInterface apiMyInterface=APIClient.getApiClient().create(APIMyInterface.class);
                    Call<User> call=apiMyInterface.Login(name.getText().toString(),contact.getText().toString(),
                            Address.getText().toString(),BilingAdddress.getText().toString(),"null",Emai.getText().toString(),
                            Password.getText().toString());
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            String res = response.body().response;
                            if (res .equals("Sucess")) {
                                Intent intnt=new Intent(Signup.this,MainActivity.class);
                                startActivity(intnt);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(Signup.this, "Complete all the fields first", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(Signup.this, "Exception: "+t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    Toast.makeText(Signup.this, "Complete all the field first", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
