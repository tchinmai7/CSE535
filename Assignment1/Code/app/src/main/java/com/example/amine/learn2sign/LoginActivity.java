package com.example.amine.learn2sign;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.stetho.Stetho;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    public static String INTENT_ID = "INTENT_ID";
    public static String INTENT_EMAIL = "INTENT_EMAIL";
    public static String INTENT_WORD = "INTENT_WORD";
    public static String INTENT_TIME_WATCHED = "INTENT_TIME_WATCHED";
    public static String INTENT_TIME_WATCHED_VIDEO = "INTENT_TIME_WATCHED_VIDEO";
    public static String INTENT_URI = "INTENT_URI";
    public static String INTENT_SERVER_ADDRESS = "INTENT_SERVER_ADDRESS";

    @BindView(R.id.et_email)
    EditText et_email;

    @BindView(R.id.et_id)
    EditText et_id;

    @BindView(R.id.bt_login)
    Button bt_login;

    String email;
    String id;
    SharedPreferences sharedPreferences;
    long time_to_login;
    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Stetho.initializeWithDefaults(this);

        time_to_login = System.currentTimeMillis();
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        if(sharedPreferences.contains(INTENT_ID) && sharedPreferences.contains(INTENT_EMAIL)) {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();

        }
    }

    @OnClick(R.id.bt_login)
    public void login() {

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            if (et_email.getText().toString().isEmpty() || et_id.getText().toString().isEmpty()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("ALERT");
                alertDialog.setMessage("Please Enter Login Information!");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                email = et_email.getText().toString();
                id = et_id.getText().toString();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(INTENT_EMAIL, email);
                intent.putExtra(INTENT_ID, id);

                if (sharedPreferences.edit().putString(INTENT_EMAIL, email).commit() &&
                        sharedPreferences.edit().putString(INTENT_ID, id).commit()) {

                    time_to_login = System.currentTimeMillis() - time_to_login;

                    sharedPreferences.edit().putInt(getString(R.string.login), sharedPreferences.getInt(getString(R.string.login), 0) + 1).apply();
                    HashSet<String> hashset = (HashSet<String>) sharedPreferences.getStringSet("LOGIN_TIME", new HashSet<String>());
                    hashset.add("LOGIN_ATTEMPT_" + sharedPreferences.getInt(getString(R.string.login), 0) + "_" + id + "_" + email + "_" + time_to_login);
                    sharedPreferences.edit().putStringSet("LOGIN_TIME", hashset).apply();
                    startActivity(intent);
                    this.finish();

                }
            }
        }
    }

}
