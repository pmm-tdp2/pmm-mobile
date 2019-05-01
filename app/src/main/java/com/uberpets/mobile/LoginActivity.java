package com.uberpets.mobile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookButtonBase;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    private LoginButton mLoginButton;
    private TextView dataDisplayed;
    private CallbackManager mCallbackManager;
    private final String TAG_LOGIN = "LOGIN_FACEBOOK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = findViewById(R.id.login_button_facebook);
        dataDisplayed = findViewById(R.id.data_displayed_login);
        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton.setReadPermissions("email");
        registerCallback();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void registerCallback() {
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleSuccessEvent(loginResult);
            }

            @Override
            public void onCancel() {
                handleCancelEvent();
            }

            @Override
            public void onError(FacebookException error) {
                handleErrorEvent(error);
            }
        });
    }

    private void handleSuccessEvent(@NonNull LoginResult loginResult) {
        dataDisplayed.setText("ID USER: "+
                loginResult.getAccessToken().getUserId() + "\n" +
                "TOKEN: "+loginResult.getAccessToken().getToken());

        //handle if redirect to register or home of user
    }

    private void handleCancelEvent() {
        dataDisplayed.setText("El login fue cancelado");
    }

    private void handleErrorEvent(FacebookException error) {
        dataDisplayed.setText("Error en el Login");
        Log.e(TAG_LOGIN,error.getMessage());
    }

}
