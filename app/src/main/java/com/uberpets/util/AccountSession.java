package com.uberpets.util;

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

public class AccountSession extends AppCompatActivity {
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
    public void saveSessionAccount(String rolId) {
        try{
            //sp = getSharedPreferences("login",MODE_PRIVATE);
            sp.edit().putBoolean("logged",true).apply();
            sp.edit().putString("user",rolId).apply();
        }catch (NullPointerException e){
            Log.e(this.getClass().getName(),e.toString());
        }

    }


    /**
     * delete information of the previous session
     */
    public void finalizeSessionAccount() {
        //sp = getSharedPreferences("login",MODE_PRIVATE);
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
    }


    public void evaluateSessionAccount(Context context) {
        sp = context.getSharedPreferences("login",MODE_PRIVATE);
        if(sp != null && sp.getBoolean("logged",false)){
            try{
                String idLogged = sp.getString("user","");
                if(idLogged.equals(Constants.getInstance().getID_USERS())){
                    Intent intent = new Intent(this, UserHome.class);
                    startActivity(intent);

                }else if(idLogged.equals(Constants.getInstance().getID_DRIVERS())){
                    Intent intent = new Intent(this, DriverHome.class);
                    startActivity(intent);
                }
            }catch (NullPointerException e){
                Log.e(this.getClass().getName(),e.toString());
            }
        }
    }
}
