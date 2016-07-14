package com.example.android.splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private TextView signup_text, forget_pass_text;
    private EditText Username, Pass;
    private Button login_button;
    private AlertDialog.Builder builder;
    private String imei = "";
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    public  static Boolean saveLogin;
    public static Boolean keepLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        if (mayRequestContacts()) {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();

            signup_text = (TextView) findViewById(R.id.sign_up);
            signup_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
            });

            forget_pass_text = (TextView) findViewById(R.id.forget_pass);
            forget_pass_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BackgroundTask backgroundTask = new BackgroundTask(LoginActivity.this);
                    backgroundTask.execute("forget_password", imei);
                }
            });

            Username = (EditText) findViewById(R.id.username_login);
            Pass = (EditText) findViewById(R.id.password_login);
            login_button = (Button) findViewById(R.id.login_button);
            saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
            loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            loginPrefsEditor = loginPreferences.edit();
            saveLogin = loginPreferences.getBoolean("saveLogin", false);

            /*
             * code to set up font for text views
             */



            if (saveLogin == true) {
                Username.setText(loginPreferences.getString("username", ""));
                Pass.setText(loginPreferences.getString("password", ""));
                saveLoginCheckBox.setChecked(true);
            }
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Username.getText().toString().equals("") || Pass.getText().toString().equals("")) {
                        builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Something went wrong");
                        builder.setMessage("Please fill both the fields");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        BackgroundTask backgroundTask = new BackgroundTask(LoginActivity.this);


                        String username = Username.getText().toString();
                        String password = Pass.getText().toString();
                        if (saveLoginCheckBox.isChecked()) {
                            saveLogin=true;
                            loginPrefsEditor.putString("username", username);
                            loginPrefsEditor.putString("password", password);
                            loginPrefsEditor.commit();
                        } else {
                            loginPrefsEditor.clear();
                            loginPrefsEditor.commit();
                        }

                        /*
                         * AsyncTask has the form AsyncTask<Param, Progress, Return> with execute using the Param type.
                         * so here we are sending the @Params to the Background task with all the parameters as strings
                         */

                        backgroundTask.execute("login", Username.getText().toString(), Pass.getText().toString(), imei);
                        /*
                         * here login is arg[0], Username.getText().toString() is arg[1] and so on.
                         */

                    }

                }
            });




        }
//        }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//
//}
    }

    private boolean mayRequestContacts() {
        int permissionInternet = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int phone_state_permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        int cameraperm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int flashlightperm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.FLASHLIGHT);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionInternet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (phone_state_permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (cameraperm != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (flashlightperm != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.FLASHLIGHT);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_DENIED);
                perms.put(android.Manifest.permission.FLASHLIGHT, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if ((perms.get(android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                            && (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            && (perms.get(android.Manifest.permission.FLASHLIGHT) == PackageManager.PERMISSION_GRANTED)
                            && (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                            && (perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                        Log.d("TAG", "everypermission granted");
                        //else any one or both the permissions are not granted
                    } else {
                        //Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.INTERNET) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.FLASHLIGHT)) {

                            showDialogOK("Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    mayRequestContacts();
                                                    break;

                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            return;
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }
}

