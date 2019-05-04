package com.uberpets.mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.uberpets.model.DataFacebook;

public class UserRegisterActivity extends AppCompatActivity {
    private DataFacebook mDataFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        mDataFacebook = (DataFacebook) getIntent().getSerializableExtra("DATA");
        addName();
    }

    private void addName() {
        EditText editName = findViewById(R.id.name_user_facebook);
        editName.setText(mDataFacebook.getName());
    }

}
