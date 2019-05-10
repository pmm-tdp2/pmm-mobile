package com.uberpets.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.login.LoginManager;

public class AccountSession {
    private static final String APP_SETTINGS = "APP_SETTINGS";
    private static final String LOGIN_STATUS_VALUE = "Login";
    private static final String ROL_LOGGED_VALUE = "rol";


    private static SharedPreferences getSharedPreferences(Context context) {
        Log.d(AccountSession.class.getName(),"init to sharePreferences");
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static boolean getLoginStatusValue(Context context) {
        Log.d(AccountSession.class.getName(),"get status login");
        return getSharedPreferences(context).getBoolean(LOGIN_STATUS_VALUE , false);
    }

    public static void setLoginStatusValue(Context context, boolean value) {
        Log.d(AccountSession.class.getName(),"set status login in: "+ value);
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(LOGIN_STATUS_VALUE , value);
        editor.apply();
    }

    public static String getRolLoggedValue(Context context) {
        String rol = getSharedPreferences(context).getString(ROL_LOGGED_VALUE , "");
        Log.d(AccountSession.class.getName(),"get rol login:" +  rol);
        return rol;
    }

    public static void setRolLoggedValue(Context context, String value) {
        Log.d(AccountSession.class.getName(),"set rol login in: "+ value);
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(ROL_LOGGED_VALUE , value);
        editor.apply();
    }


    public static void finalizeSession(Context context) {
        Log.d(AccountSession.class.getName(),"finalize session");
        LoginManager.getInstance().logOut();
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(ROL_LOGGED_VALUE , "");
        editor.putBoolean(LOGIN_STATUS_VALUE,false);
        editor.apply();
    }

   /* public static boolean isThereSomeoneLogged(Context context) {
        Log.d(AccountSession.class.getName(),"question if there someone logged ");
        return (getRolLoggedValue(context).length() > 0 &&
                getLoginStatusValue(context));
    }*/


}
