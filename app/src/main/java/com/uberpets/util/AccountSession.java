package com.uberpets.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.uberpets.Constants;
import com.uberpets.mobile.DriverHome;
import com.uberpets.mobile.TabLoginActivity;
import com.uberpets.mobile.UserHome;

public class AccountSession {
    private static final AccountSession ourInstance = new AccountSession();
    private static SharedPreferences sp;

    public static AccountSession getInstance() {
        return ourInstance;
    }

    private AccountSession() {
    }


    /**
     * storage information of the previous session
     */
    /*public void saveSessionAccount(String rolId, Context context) {
        try{
            final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            sp = context.getSharedPreferences("Session Data",MODE_PRIVATE);
            sp.edit().putBoolean("logged",true).apply();
            sp.edit().putString("user",rolId).apply();
            Log.i(this.getClass().getName(),"Session is storage");
        }catch (NullPointerException e){
            Log.e(this.getClass().getName(),e.toString());
        }

    }*/


    /**
     * delete information of the previous session
     */
    /*public void finalizeSessionAccount() {
        //sp = getSharedPreferences("login",MODE_PRIVATE);
        sp = mContext.getSharedPreferences("Session Data",MODE_PRIVATE);
        Intent intent = new Intent(this, TabLoginActivity.class);
        startActivity(intent);
        LoginManager.getInstance().logOut();
        try{
            SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
            sp.edit().putBoolean("logged",false).apply();
            sp.edit().putString("user","").apply();
        }catch (NullPointerException e){
            Log.e(this.getClass().getName(),e.toString());
        }
    }*/


    /*public void evaluateSessionAccount(Context context) {
        //Log.i(this.getClass().getName(),"validating previous session");
        if(sp == null)
            sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        //sp = mContext.getSharedPreferences("Session Data",MODE_PRIVATE);
        if(sp == null){
            Log.e(this.getClass().getName(),"error to evaluate session");
        }
        if(sp != null && sp.getBoolean("logged",false)){
            try{
                String idLogged = sp.getString("user","");
                if(idLogged.equals(Constants.getInstance().getID_USERS())){
                    Intent intent = new Intent(this, UserHome.class);
                    Activity.getstartActivity(intent);

                }else if(idLogged.equals(Constants.getInstance().getID_DRIVERS())){
                    Intent intent = new Intent(this, DriverHome.class);
                    Activity.startActivity(intent);
                }
            }catch (NullPointerException e){
                Log.e(this.getClass().getName(),e.toString());
            }
        }

    }*/
    private static final String APP_SETTINGS = "APP_SETTINGS";
    private static final String SOME_STRING_VALUE = "SOME_STRING_VALUE";


    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getSomeStringValue(Context context) {
        return getSharedPreferences(context).getString(SOME_STRING_VALUE , null);
    }

    public static void setSomeStringValue(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SOME_STRING_VALUE , newValue);
        editor.apply();
    }
}
