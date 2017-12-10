package com.example.news.autodocs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signin extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;
    String SessionId,SessionEmail,SessionPassword;

    private RadioGroup mRadioGroup;
    private RadioButton radioButton;
    EditText mEmailInput,mPasswordInput;
    Button mSignInButton;
    Context mContext;
    User user;
    String userType="";
    Mechanic mechanic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mContext=Signin.this;
        mEmailInput = (EditText) findViewById(R.id.login_email_editText);
        mPasswordInput = (EditText) findViewById(R.id.login_password_editText);

        mRadioGroup = (RadioGroup) findViewById(R.id.login_radio);
         userType= "user";

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                if (i == R.id.login_radioButtonUser) {
                    radioButton = (RadioButton) findViewById(R.id.login_radioButtonUser);
                } else if (i == R.id.login_radioButtonMechanic) {
                    radioButton = (RadioButton) findViewById(R.id.login_radioButtonMechanic);
                }
                userType=radioButton.getTag().toString();
                Toast.makeText(mContext,radioButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        TextView mRegisterLink = (TextView) findViewById(R.id.loginPage_register_textView);
        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,Signup.class);
                mContext.startActivity(intent);
            }
        });
        mSignInButton = (Button) findViewById(R.id.loginPage_login_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });
    }
    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        String email,password;
        // Reset errors.
        mEmailInput.setError(null);
        mPasswordInput.setError(null);
        user=new User();
        user.email = mEmailInput.getText().toString();
        user.password = mPasswordInput.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(user.password) && !isPasswordValid(user.password)) {
            mPasswordInput.setError("error_invalid_password");
            focusView = mPasswordInput;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(user.email)) {
            mEmailInput.setError("error_field_required");
            focusView = mEmailInput;
            cancel = true;
        } else if (!isEmailValid(user.email)) {
            mEmailInput.setError("error_invalid_email");
            focusView = mEmailInput;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //ProgressBar progressBar=(ProgressBar)findViewById(R.id.login_progressBar);

            // perform the user login attempt.
            APIMyInterface apiInterface= APIClient.getApiClient().create(APIMyInterface.class);
            Call<User> call=apiInterface.SignIn(user.email,user.password,userType);
            call.enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User u=response.body();
                    if(u.response.equalsIgnoreCase("success")) {
                        Toast.makeText(mContext,"Welcome " +u.name, Toast.LENGTH_SHORT).show();
                        // Session Manager
                        session = new SessionManager(getApplicationContext());
                        session.createLoginSession(u.id, u.email, user.password);
                        NextActivity();
                    }else if(u.response.equalsIgnoreCase("unregistered")){
                        mEmailInput.setError("Not registered");
                    }else if(u.response.equalsIgnoreCase("wrong password")){
                        mPasswordInput.setError("Wrong password");
                    }else{
                        Toast.makeText(mContext, "Server response: "+u.response, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(mContext, "Fail"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void NextActivity(){
        if(userType.equalsIgnoreCase("user")){
            Intent intent1 = new Intent(mContext,MainActivity.class);
            mContext.startActivity(intent1);
            finish();
    }else{
            Intent intent = new Intent(mContext,MechanicActivity.class);
            mContext.startActivity(intent);
            finish();
        }

    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }
}
