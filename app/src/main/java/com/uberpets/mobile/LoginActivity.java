package com.uberpets.mobile;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    private LoginButton mLoginButton;
    private TextView dataDisplayed;
    private CallbackManager mCallbackManager;

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
        String text = "ID USER: "+
                loginResult.getAccessToken().getUserId() + "\n" +
                "TOKEN: "+loginResult.getAccessToken().getToken();
        dataDisplayed.setText(text);

        //handle if redirect to register or home of user
    }

    private void handleCancelEvent() {
        dataDisplayed.setText(R.string.message_login_canceled);
    }

    private void handleErrorEvent(FacebookException error) {
        dataDisplayed.setText(R.string.message_login_error);
        Log.e(this.getClass().getName(),error.getMessage());
    }

}
