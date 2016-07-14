package com.example.android.splash;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.splash.data.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Kartik Sethi on 02-Jun-16.
 */
public class BackgroundTask extends AsyncTask<String, Void, String> {
    // first argument tell us that parameter type is String
    Context contx;
    public static boolean network=false;
    public static int status=0;
    Activity activity;
    //    public static JSONArray supervisor;
    public static String[] sString;
    //String imei="";
    String register_url =  "https://cb47fd8d.ngrok.io/GPSAttendance/welcome/register";//"http://61.246.165.5/GPSAttendance/welcome/register";//  (name of the site) "http://192.168.X.X(ip of my comp or any other site)/directory name/php script
    String login_url ="https://cb47fd8d.ngrok.io/GPSAttendance/welcome/login";// "http://61.246.165.5/GPSAttendance/welcome/login";
    String gps_url = "https://cb47fd8d.ngrok.io/GPSAttendance/welcome/report"; //"http://61.246.165.5/GPSAttendance/welcome/report"
    String forget_password_url = "https://cb47fd8d.ngrok.io/GPSAttendance/welcome/Task_ResetPassword";// "http://61.246.165.5/GPSAttendance/welcome/Task_ResetPassword"; "http://61.246.165.5/GPSAttendance/welcome/Task_ResetPassword";
    String ping_url = "https://cb47fd8d.ngrok.io/GPSAttendance/welcome/ping";
    AlertDialog.Builder builder;  // to alert the user
    ProgressDialog progressDialog;  // to show the progress
    //SERVER IP: http://61.246.165.5
    String lat,lon,imeigps,captImage,message,timestamp;
    String boo;

    public BackgroundTask(Context contx) {
        this.contx = contx;
        activity = (Activity) contx;
    }

    @Override
    protected void onPreExecute() {  // to initialise the progress dialog

        //TelephonyManager tm=(TelephonyManager) contx.getSystemService(Context.TELEPHONY_SERVICE);
        //imei=tm.getDeviceId();
        builder = new AlertDialog.Builder(activity); // alert dialog box for the context which has called the BackgroundTask.java (Register/Login)
        progressDialog = new ProgressDialog(contx);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Connecting to server....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        Log.e("FFFFF",activity.toString());
        if(!activity.toString().contains("splash.Splash"))
        {
            progressDialog.show();
            Log.e("Splash","something");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String method = params[0]; // determines whether the task is "register" or "login" as it will be the first parameter of the @param array. See register activity's last lines.
        if (method.equals("register")) // if the command is to register then we will establish connection to the server, i.e params[0] = "register"
        {
            try { // create a url connection using HttpURLconnection and use output stream to send data to the server
                //Log.v("BackgroundTask","IMEI number is: " + imei);
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8")); // last constructor of the documentation
                String name = params[1]; // refer Register Activity if confused
                String email = params[2];
                String username = params[3]; // refer Register Activity if confused
                String password = params[4]; // refer Register Activity if confused
                String imei = params[5];
                /*
                 * URLEncoder is a separate class with encode(String s, String charsetName) -> Encodes s using the Charset named by charsetName.
                 */
                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("imei", "UTF-8") + "=" + URLEncoder.encode(imei, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();   // output stream has been used to send data to the server
                /*
                declare an input stream to get response from the server, whether the insertion is successful or not
                the response will be in the form of a JSON builder object
                 */

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";  // just a variable to read data from each line
                while ((line = bufferedReader.readLine()) != null) {  // the response received will be written in the php code after checking the required conditions
                    stringBuilder.append(line + "\n");
                }
                httpURLConnection.disconnect();
                try {
                    Thread.sleep(500); // to give a pause for the "connecting to  the server" effect
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return stringBuilder.toString().trim(); // appropriate message (Registration successful or failed or already exists is displayed)


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (method.equals("login")) { // if params[0] is equal to "login"
            /*
             *to send the username and password to the server and get the response from the server
             * if the response is positive we will transit to the home activity
             * otherwise we need to display an alertDialog
             */
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String username, password, imei;
                username = params[1];
                password = params[2];
                imei = params[3];
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("imei", "UTF-8") + "=" + URLEncoder.encode(imei, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();   // output stream has been used to send data to the server
                /*
                 * Now the response from the server will be in the form of json and we need to decode it
                 */
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";  // just a variable to read data from each line
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                httpURLConnection.disconnect();
                try {
                    Thread.sleep(500); // to give a pause for the "connecting to  the server" effect
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (method.equals("gps")) { // if params[0] is equal to "login"
            /*
             *to send the username and password to the server and get the response from the server
             * if the response is positive we will transit to the home activity
             * otherwise we need to display an alertDialog
             */
            String imageFlag;
            imeigps = params[1];
            message = params[2];
            lat = params[3];
            lon=params[4];
            imageFlag = params[5];
            captImage = params[6];
            boo=params[7];
            timestamp = params[8];
            try {
                Log.e("time",timestamp);
              //  reportflag=true;
                URL url = new URL(gps_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


                String data = URLEncoder.encode("imei", "UTF-8") + "=" + URLEncoder.encode(imeigps, "UTF-8") + "&" +
                        URLEncoder.encode("Report", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8") + "&" +
                        URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8") + "&" +
                        URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(lon, "UTF-8") + "&" +
                        URLEncoder.encode("imageflag", "UTF-8") + "=" + URLEncoder.encode(imageFlag, "UTF-8") + "&" +
                        URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(captImage, "UTF-8") + "&" +
                        URLEncoder.encode("flag", "UTF-8") + "=" + URLEncoder.encode(boo, "UTF-8") + "&" +
                        URLEncoder.encode("timestamp", "UTF-8") + "=" + URLEncoder.encode(timestamp, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //Log.v("Back",imeigps+message+location);
                // output stream has been used to send data to the server
                /*
                 * Now the response from the server will be in the form of json and we need to decode it
                 */
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";  // just a variable to read data from each line
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                httpURLConnection.disconnect();
                try {
                    Thread.sleep(500); // to give a pause for the "connecting to  the server" effect
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
//                Log.v("mal","HEre");
                e.printStackTrace();
            } catch (ProtocolException e) {
//                Log.v("protocol","HEre");
                e.printStackTrace();
            } catch (IOException e) {
//                Log.v("IO","HEre");
                e.printStackTrace();
            }

        } else if (method.equals("forget_password")) { // if params[0] is equal to "forget_password"

            try {
                URL url = new URL(forget_password_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String imei = params[1];
                String data = URLEncoder.encode("imei", "UTF-8") + "=" + URLEncoder.encode(imei, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //Log.v("Back",imeigps+message+location);
                // output stream has been used to send data to the server
                /*
                 * Now the response from the server will be in the form of json and we need to decode it
                 */
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";  // just a variable to read data from each line
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                httpURLConnection.disconnect();
                try {
                    Thread.sleep(500); // to give a pause for the "connecting to  the server" effect
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
//                Log.v("mal","HEre");
                e.printStackTrace();
            } catch (ProtocolException e) {
//                Log.v("protocol","HEre");
                e.printStackTrace();
            } catch (IOException e) {
//                Log.v("IO","HEre");
                e.printStackTrace();
            }

        }


        return null; // this might be causing the shutdown of app
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    /*
     *@param here the parameter is some jason data
     * this method is used to decode that jason data(object)
     */
    @Override
    protected void onPostExecute(String json) {
        // Log.v("sdfsdf","Back");
        if (json == null) {
            network=false;
            String error_message = "Some Error has occurred. Please check you internet connection.";

            //showDialog("Login failed", error_message, "net_fail");
            Toast.makeText(this.activity, error_message, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            if(!activity.toString().contains("splash.Splash")) {
                {
                    DBHelper db = new DBHelper(contx);
                    db.insertReport_previous(message, lat, lon, imeigps, captImage, 0, timestamp);
                }
               // reportflag=false;
            }

        } else {
            try {
                // first we need to dismiss the
                //String str=json;
                JSONObject jsonObject = new JSONObject(json); // Output String parsed as a JSON object
                JSONArray jsonArray = jsonObject.getJSONArray("server_response"); // see welcome.php for server_response explanation
                // to get each of the json data from the json array. A JSON array can contain multiple JSON objects.

            /*
             * 0 is the index as our final JSON array which is being echoed from the php script
             * It has value either ["server_response" :{"code":"reg_true", "message":"Hurray!"}} or {"server_response" :{"code":"reg_false", "message":"Failed!"}]
             */
                JSONObject JO = jsonArray.getJSONObject(0);
                // this will give us the json object = {"code":"reg_true", "message":"Hurray!,"supervisor":["hello","world"]} or {"code":"reg_false", "message":"Failed!"}
                // now we can read respective values from the key:value pairs for the above jason object
                // there are two data, code and message from the server as defined in our php script

                String code = JO.getString("code");  // code as the key, getString will return the value from the key:value pair

                if (code.equals("reg_true")) // reg_true that means registration success, so corresponding message will be displayed: (here) it is a random string ("Hurray!") (to be changed)
                {
                    String message = JO.getString("message"); // message as the key, getString will return the value from the key:value pair
                    showDialog("Registration Success", message, code);
                    progressDialog.dismiss();
                    activity.finish();
                } else if (code.equals("reg_false"))// reg_false that means registration failure, so corresponding message will be displayed: (here) it is a random string ("Failed!") (to be changed)
                {
                    String message = JO.getString("message"); // message as the key, getString will return the value from the key:value pair
                    showDialog("Registration Failed", message, code);
                    progressDialog.dismiss();
                } else if (code.equals("login_true")) {
                    if (LoginActivity.saveLogin) {
                        SharedPreferences loginPreferences = activity.getSharedPreferences("loginPrefs", 0);
                        SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.commit();
                    }
                    Log.v("kartik:", "asasa");
                    String name = JO.getString("name"); // message as the key, getString will return the value from the key:value pair
                    String dept = JO.getString("Department"); // message as the key, getString will return the value from the key:value pair
                    JSONArray js = JO.getJSONArray("supervisor");
                    SharedPreferences loginPreferences = activity.getSharedPreferences("loginPrefs", 0);
                    SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();
                    loginPrefsEditor.putString("name", name);
                    loginPrefsEditor.putString("dept",dept);
                    loginPrefsEditor.commit();
                    sString = new String[js.length()];
                    DBHelper db=new DBHelper(contx);
                    db.deleteSuperVisorList();
                    for (int i = 0; i < js.length(); i++) {


                        db.insertSupervisor(js.getString(i));
                    }

                    Intent intent = new Intent(activity, drawer.class);
                    // to attach message to the intent from  the server
                    intent.putExtra("name", name);
                    intent.putExtra("dept", dept);

                    activity.startActivity(intent);
                    progressDialog.dismiss();
                    activity.finish();
                } else if (code.equals("login_false")) {
                    String message = JO.getString("message"); // message as the key, getString will return the value from the key:value pair
                    showDialog("Login Error", message, code);
                    progressDialog.dismiss();
                } else if (code.equals("report_true")) {
                    DBHelper db=new DBHelper(contx);
                    if(activity.toString().contains("splash.Splash")) {
                        if (captImage == null)
                            captImage = "";
                        Log.e("asdf","asdf");
                        db.updateStatus(message, lat, lon, imeigps, captImage, 1);
                    }
                    else
                        db.insertReport_previous(message,lat,lon,imeigps,captImage,1,timestamp);
                    if(!activity.toString().contains("splash.Splash")) {
                        progressDialog.dismiss();

                        String message = JO.getString("message"); // message as the key, getString will return the value from the key:value pair
                        if (boo.equals("true"))
                            showDialog("Submission Successful!", message, code);
                        //  reportflag=false;
                        HomeFrag completedFragment= new HomeFrag();

//                        final int commit = activity.getFragmentManager().beginTransaction().replace(R.id.FragmentHolder, completedFragment).commit();
                    }
                } else if (code.equals("report_false")) {
                    String message = JO.getString("message"); // message as the key, getString will return the value from the key:value pair
                    showDialog("Submission error!", message, code);
                    progressDialog.dismiss();
                } else if (code.equals("forget_pass_true")) {
                    String message = JO.getString("message"); // message as the key, getString will return the value from the key:value pair
                    showDialog("", message, code);
                    progressDialog.dismiss();
                }
                else if(code.equals("true"))
                {
                    network=true;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    /*
     * To analyse the message and to display the alert message
     */
    public void showDialog(String title, String message, String code) {
        builder.setTitle(title);

        if (code.equals("reg_true") || code.equals("reg_false")) {

            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    activity.finish();
                }
            });

        } else if (code.equals("login_false")) {
            // if loginn fails then we need to empty the username and password fields
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText username, password;
                    username = (EditText) activity.findViewById(R.id.username_login);
                    password = (EditText) activity.findViewById(R.id.password_login);
                    username.setText("");
                    password.setText("");
                    dialogInterface.dismiss();
                }
            });
        } else if (code.equals("report_false")) {
            // if loginn fails then we need to empty the username and password fields
            builder.setMessage(message);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });
        } else if (code.equals("report_true")) {
            // if loginn fails then we need to empty the username and password fields
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent in=new Intent(activity,drawer.class);
                    String name,dept;
                    SharedPreferences loginPreferences = activity.getSharedPreferences("loginPrefs", 0);
                    name = loginPreferences.getString("name", "");
                    dept= loginPreferences.getString("dept", "");
                    in.putExtra("name",name);
                    in.putExtra("dept",dept);
                    activity.startActivity(in);
                    activity.finish();
                }
            });

        } else if (code.equals("forget_pass_true")) {
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}