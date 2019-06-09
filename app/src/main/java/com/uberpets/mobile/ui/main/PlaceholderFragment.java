package com.uberpets.mobile.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.uberpets.Constants;
import com.uberpets.library.rest.Headers;
import com.uberpets.mobile.DriverHome;
import com.uberpets.mobile.R;
import com.uberpets.mobile.UserHome;
import com.uberpets.mobile.WelcomeToAppActivity;
import com.uberpets.model.DataFacebook;
import com.uberpets.model.FileDocumentDTO;
import com.uberpets.model.LoginDTO;
import com.uberpets.model.SimpleResponse;
import com.uberpets.services.App;
import com.uberpets.util.AccountImages;
import com.uberpets.util.AccountSession;
import com.uberpets.util.ConvertImages;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private String mIdTab;
    private String idWhoHasLogged = "";
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private Constants mConstant = Constants.getInstance();
    private CardView mCardMessage;
    private TextView mTextCard;
    private LoginResult mLoginResult;

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
        PageViewModel pageViewModel = ViewModelProviders.of(this)
                .get(PageViewModel.class);

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

        /*
         * before to init the application valid
         * if user has init session previously and avoid login again
         */

        root.setBackgroundColor(getResources().getColor(
                mIdTab.equals( mConstant.getID_USERS() ) ?
                        R.color.loginUser : R.color.loginDriver));

        evaluateSession();

        mLoginButton = root.findViewById(R.id.login_button_facebook);
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

    private void evaluateSession() {
        if(AccountSession.getLoginStatusValue(getContext()) &&
        AccountSession.getRolLoggedValue(getContext()).equals(this.mIdTab)) {
            goLoginServer();
        }
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
            public void onSuccess(LoginResult loginResult) {
                handleSuccessLoginFacebook(loginResult); }

            @Override
            public void onCancel() {

                handleCancelLoginFacebook();
            }

            @Override
            public void onError(FacebookException error) {

                handleErrorLoginFacebook(error);
            }
        });
    }


    private void handleCancelLoginFacebook() {
        showErrorInRegister("El login fue cancelado");
        Log.d(this.getClass().getName(),"Cancel event: ");
    }

    private void handleErrorLoginFacebook(FacebookException error) {
        showErrorInRegister("Hubo un error en el Login, inténtelo ,más tarde");
        Log.e(this.getClass().getName(),error.getMessage());
    }

    private void handleSuccessLoginFacebook(@NonNull LoginResult loginResult) {
        mLoginResult = loginResult;
        goLoginServer();
    }


    private void goLoginServer() {

        String id;
        if(mLoginResult == null) {
            id =  AccountSession.getIdLogin(getActivity());
        }else {
            id =  mLoginResult.getAccessToken().getUserId();
        }

        Log.i(this.getClass().getName(),id);
        LoginDTO loginDTO = new LoginDTO(id, mIdTab.equals(mConstant.getID_USERS())?
                mConstant.getID_USERS() : mConstant.getID_DRIVERS());

        App.nodeServer.post("/api/login",loginDTO,
                SimpleResponse.class, new Headers())
                .run(this::handleSuccessfulServerLoginResponse,this::handleErrorLoginServerResponse);
    }

    private void handleErrorLoginServerResponse(Exception e) {
        Log.e(this.getClass().getName(),"Error en el login");
        Log.e(this.getClass().getName(),e.toString());
        showErrorInRegister("Hubo un problema con el login, inténtelo más tarde");
    }


    private void handleSuccessfulServerLoginResponse(SimpleResponse response) {
        Log.d(this.getClass().getName(),"data response login: "+response.getStatus());
        switch (response.getStatus()){
            case 200:
                loadImages();
                break;

            case 203:
                initRegister();
                break;
        }
    }


    private void loadImages() {
        if(mLoginResult != null) {
            AccountSession.setLoginId(getActivity(),mLoginResult.getAccessToken().getUserId());
            AccountSession.setRolLoggedValue(getActivity(),this.mIdTab);
            AccountSession.setLoginStatusValue(getActivity(),true);
        }

        String rolId = mIdTab.equals(mConstant.getID_USERS())? "userId" : "driverId";
        String path = "/api/fileDocuments/?"+rolId+"="+
                AccountSession.getIdLogin(getActivity())+"&name=profile";
        App.nodeServer.get(path, FileDocumentDTO[].class,new Headers())
                .run(this::handleSuccessLoadImages,this::handleErrorLoadImages);
    }

    private void handleErrorLoadImages(Exception e) {
        Log.e(this.getClass().getName(),"Error to load images from server");
        Log.e(this.getClass().getName(),e.toString());
        Toast toast = Toast.makeText(getActivity(),"Error para obtener las imagenes"
                ,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
    }

    private void handleSuccessLoadImages(FileDocumentDTO[] files) {
        Log.i(this.getClass().getName(),"File obtained successfully");
        AccountImages.getInstance().setPhotoProfile(
                ConvertImages.getBitmapImage(files[0].getData()));
        goToHome();
    }

    private void goToHome() {
        // when user is registered and access successfully
        Intent intent = new Intent(getActivity(),
                mIdTab.equals(mConstant.getID_USERS()) ?
                        UserHome.class : DriverHome.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void initRegister() {
        //TODO show message or progress bar
        validateAmountFriends(mLoginResult);
    }


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

                            try{
                                String after = object.getJSONObject("albums")
                                        .getJSONObject("paging").getJSONObject("cursors")
                                        .getString("after");

                                object.getJSONObject("albums")
                                        .getString("next");

                                afterPage[0]=after;

                            }catch (Exception e){
                                hasNextPage[0]=false;
                            }

                        }catch(Exception ex){
                            Log.d(this.getClass().getName(),ex.toString());
                            hasNextPage[0]=false;
                        }
                    }
                });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "albums");
            parameters.putString("after", afterPage[0]);
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
        LoginManager.getInstance().logOut();
        if (text.length() > 0){
            mTextCard.setText(text);
        }
        mCardMessage.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Do something after 2500ms
            mCardMessage.setVisibility(View.INVISIBLE);
        }, 2500);
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


    /**
     * This method obtain picture profile and name of user/driver
     * if these fields are not obtained the field name is empty
     * and picture is picture by default
     * @param loginResult is object that contain token, id of facebook account
     */
    public void getDataLoginFacebook(@NonNull LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.v(this.getClass().getName(), response.toString());
                    try {
                        DataFacebook dataFacebook =  new DataFacebook
                                .DataFacebookBuilder(loginResult.getAccessToken().getUserId())
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