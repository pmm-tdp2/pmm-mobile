package com.uberpets.mobile.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.uberpets.mobile.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private static final int ID_USER_TAB = 1;
    private static final int ID_DRIVER_TAB = 1;
    private int mIdTab;
    private int idWhoHasLogged;
    private LoginButton mLoginButton;
    private TextView dataDisplayed;
    private CallbackManager mCallbackManager;
    private final String TAG_LOGIN = "LOGIN_FACEBOOK";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            mIdTab =  index;
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab_login, container, false);

        if (mIdTab == ID_USER_TAB) {
            root.setBackgroundColor(getResources().getColor(R.color.loginUser));
        }else{
            root.setBackgroundColor(getResources().getColor(R.color.loginDriver));
        }

        mLoginButton = root.findViewById(R.id.login_button_facebook);
        dataDisplayed = root.findViewById(R.id.data_displayed_login);
        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton.setReadPermissions("email");
        registerCallback();
        setListenerButtonLogin();


        /*final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }


    public void setListenerButtonLogin() {
        mLoginButton.setOnClickListener( view -> {
            idWhoHasLogged = mIdTab;
            //mLoginButton.setOnClickListener(null);
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(idWhoHasLogged == mIdTab)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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