package com.uberpets.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.uberpets.Constants;
import com.uberpets.mobile.R;
import com.uberpets.mobile.WelcomeToAppActivity;
import com.uberpets.model.DataFacebook;
import com.uberpets.model.SimpleResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private String mIdTab;
    private String idWhoHasLogged = "";
    private LoginButton mLoginButton;
    private TextView dataDisplayed;
    private CallbackManager mCallbackManager;
    private Constants mConstant = Constants.getInstance();
    private CardView mCardMessage;
    private TextView mTextCard;

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
        PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            if(index == 1)
                mIdTab = mConstant.getID_USERS();
            else
                mIdTab = mConstant.getID_DRIVERS();
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab_login, container, false);

        root.setBackgroundColor(getResources().getColor(
                mIdTab.equals( mConstant.getID_USERS() ) ?
                        R.color.loginUser : R.color.loginDriver));

        mLoginButton = root.findViewById(R.id.login_button_facebook);
        dataDisplayed = root.findViewById(R.id.data_displayed_login);
        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton.setReadPermissions("email");
        mLoginButton.setReadPermissions("user_friends");
        mLoginButton.setReadPermissions("user_photos");

        mCardMessage = root.findViewById(R.id.card_showMessage_register);
        mCardMessage.setVisibility(View.INVISIBLE);
        mTextCard = root.findViewById(R.id.text_message_register);
        registerCallback();
        setListenerButtonLogin();

        return root;
    }


    public void setListenerButtonLogin() {
        mLoginButton.setOnClickListener( view ->{
            idWhoHasLogged = mIdTab;
            mCardMessage.setVisibility(View.INVISIBLE);
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(idWhoHasLogged.equals(mIdTab)){
            idWhoHasLogged = "";
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void registerCallback() {
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) { handleSuccessEvent(loginResult); }

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
        /*dataDisplayed.setText("ID USER: "+
                loginResult.getAccessToken().getUserId() + "\n" +
                "TOKEN: "+loginResult.getAccessToken().getToken());*/

        //pegarle al server con el id del usuario para ver si está registrado
        //si no está registrado registrar
        //si está registrado go to home

        //App.nodeServer.get()
        Log.d(this.getClass().getName(),"Success event: "+loginResult.toString());
        validateAmountFriends(loginResult);
    }

    private void handleCancelEvent() {
        dataDisplayed.setText("El login fue cancelado");
        Log.d(this.getClass().getName(),"Cancel event: ");

    }

    private void handleErrorEvent(FacebookException error) {
        dataDisplayed.setText("Error en el Login");
        Log.e(this.getClass().getName(),error.getMessage());
    }

/*
    private void handleGoodResponse(SimpleResponse response) {

    }

    private void handleErrorResponse(Exception e) {

    }
*/

    public void validateAmountFriends(@NonNull LoginResult loginResult) {
        int minimumNumberFriends = mConstant.getMINIMUM_FRIENDS_ACCOUNT();
        GraphRequest requestFriends = GraphRequest.newMyFriendsRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {

                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                        try{
                            Log.v(this.getClass().getName(), response.toString());
                            String value = response.getJSONObject().
                                    getJSONObject("summary").getString("total_count");
                            int amountFriends = Integer.parseInt(value);

                            if(amountFriends >= minimumNumberFriends) {
                                Log.d(this.getClass().getName(),
                                        "Amount Friends validated successfully");
                                getAllDateAlbumsToValidate(loginResult);
                            }else
                                showErrorInRegister("");

                        }catch (Exception ex){
                            Log.e(this.getClass().getName(), ex.toString());
                        }

                    }
                });

        Bundle parametersFriend = new Bundle();
        parametersFriend.putString("fields", "summary");
        requestFriends.setParameters(parametersFriend);
        requestFriends.executeAsync();
    }


    public void getAllDateAlbumsToValidate(@NonNull LoginResult loginResult) {

        final Boolean[] isValid = {false};
        final Boolean[] hasNextPage = {true};
        final String[] afterPage = {""};
        int maxIteration = 20;
        int iterations =0;
        do{
            GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try{
                            Log.v(this.getClass().getName(), response.toString());
                            isValid[0] = validateAlbumDate(object);

                            String after = object.getJSONObject("albums")
                                    .getJSONObject("paging").getJSONObject("cursors")
                                    .getString("after");

                            String nextPage = object.getJSONObject("albums")
                                    .getString("next");

                            if (nextPage == null)
                                hasNextPage[0]=false;
                            else
                                afterPage[0]=after;

                        }catch(Exception ex){
                            Log.d(this.getClass().getName(),ex.toString());
                            hasNextPage[0]=false;
                        }
                    }
                });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "albums");
            parameters.putString("next", afterPage[0]);
            request.setParameters(parameters);

            Thread t = new Thread(request::executeAndWait);
            t.start();
            try {
                t.join();
            }catch (Exception e){
                Log.e(this.getClass().getName(),e.toString());
            }

            iterations++;

        }while(!isValid[0] && hasNextPage[0] && iterations < maxIteration);

        if(isValid[0]) {
            Log.d(this.getClass().getName(),"Account has been validated successfully");
            Log.d(this.getClass().getName(),"Iterations in almbus: "+iterations);
            getDataLoginFacebook(loginResult);
        }else{
            showErrorInRegister("");
        }

    }


    public void showErrorInRegister(String text) {
        if (text.length() > 0){
            mTextCard.setText(text);
        }
        mCardMessage.setVisibility(View.VISIBLE);
    }


    public boolean validateAlbumDate( @NonNull JSONObject object){
        long minimumTimeAccount = mConstant.getMINIMUM_TIME_ACCOUNT();

        try {
            JSONArray albums = object.getJSONObject("albums")
                    .getJSONArray("data");

              for (int i = 0, len = albums.length(); i < len; i++) {
                  JSONObject item = albums.getJSONObject(i);

                  Date dateNow = new Date();

                  //String dateCreationAlbum = "2015-02-28T05:29:36+0000";
                  String dateCreationAlbum = item.getString("created_time");
                  DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                  Date result1 = df1.parse(dateCreationAlbum);
                  long difference = dateNow.getTime()-result1.getTime();
                  if (difference >= minimumTimeAccount)
                      return true;
              }

        }catch (Exception ex){
            Log.e(this.getClass().getName(), ex.toString());
        }
        return false;
    }



    public void getDataLoginFacebook(@NonNull LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.v(this.getClass().getName(), response.toString());
                    try {
                        DataFacebook dataFacebook =  new DataFacebook.DataFacebookBuilder()
                                .setName(object.getString("name"))
                                .setPictureUrl("https://graph.facebook.com/" +
                                        loginResult.getAccessToken().getUserId()
                                        + "/picture?type=large")
                                .build();

                        Log.d(this.getClass().getName(),dataFacebook.getPictureUrl());

                        Intent intent = new Intent(getActivity(), WelcomeToAppActivity.class);
                        intent.putExtra(mConstant.getID_ROL(),mIdTab);
                        intent.putExtra("DATA",dataFacebook);
                        startActivity(intent);

                    }catch (Exception ex){
                        Log.e(this.getClass().getName(),ex.toString());
                    }

                }
            });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, picture");
        request.setParameters(parameters);
        request.executeAsync();

    }


}