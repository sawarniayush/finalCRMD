/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.splash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;

/**
 * Getting the Location Address.
 * <p/>
 * Demonstrates how to use the {@link Geocoder} API and reverse geocoding to
 * display a device's location as an address. Uses an IntentService to fetch the location address,
 * and a ResultReceiver to process results sent by the IntentService.
 * <p/>
 * Android has two location request settings:
 * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
 * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
 * the AndroidManifest.xml.
 * <p/>
 * For a starter example that displays the last known location of a device using a longitude and latitude,
 * see https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 * <p/>
 * For an example that shows location updates using the Fused Location Provider API, see
 * https://github.com/googlesamples/android-play-location/tree/master/LocationUpdates.
 * <p/>
 * This sample uses Google Play services (GoogleApiClient) but does not need to authenticate a user.
 * For an example that uses authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */
public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "main-activity";

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
//    EditText et = (EditText) findViewById(R.id.edit);


    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    private EditText et;
    //    private String loc;
    private String capt_image = "empty";
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * Displays the location address.
     */
    protected TextView mLocationAddressTextView;

    /**
     * Visible while the address is being fetched.
     */
    ProgressBar mProgressBar;
    String latlong;
    /**
     * Kicks off the request to fetch an address when pressed.
     */
    Button mFetchAddressButton;
    long time1, time2;
    boolean isGPS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mResultReceiver = new AddressResultReceiver(new Handler());

        //mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
//        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mFetchAddressButton = (Button) findViewById(R.id.fetch_address_button);
        et = (EditText) findViewById(R.id.edit);
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        isGPS = false;
        updateValuesFromBundle(savedInstanceState);

        updateUIWidgets();
        buildGoogleApiClient();


        TextView report_header = (TextView) findViewById(R.id.reporting_text);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Roboto-BlackItalic.ttf");
        report_header.setTypeface(typeFace);

        et.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.edit) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        ImageButton Camera_button = (ImageButton) findViewById(R.id.imageButton);
        assert Camera_button != null;
        Camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
//                Double lat = location.getLatitude();
//                Double lon = location.getLongitude();
//                latlong = Double.toString(lat) + " , " + Double.toString(lon);
//
//
//                Log.v("Location",Double.toString(lat));
//                Log.v("Location",Double.toString(lon));
//                Toast.makeText(getApplicationContext(), Double.toString(lat) + " , " + Double.toString(lon), Toast.LENGTH_LONG).show();
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//
//            public void onProviderEnabled(String provider) {
//            }
//
//            public void onProviderDisabled(String provider) {
//            }
//        };

// Register the listener with the Location Manager to receive location updates
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            ImageView iv = (ImageView) findViewById(R.id.captured_image);
            capt_image = getStringImage(bp);
            iv.setImageBitmap(bp);
        } else
            return;


    }


    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                //displayAddressOutput();
            }
        }
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    public void fetchAddressButtonHandler(View view) {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        //time1 = System.currentTimeMillis();
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        else
            NWloc();
        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            if (mAddressRequested) {
                startIntentService();
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        //Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
       // intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        //intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.

        //startService(intent);
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        BackgroundTask bktask = new BackgroundTask(MainActivity.this);
        String loc = Double.toString(latitude) + " , " + Double.toString(longitude);
        TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String msg = et.getText().toString();
//        mProgressBar.setVisibility(ProgressBar.GONE);
        Toast.makeText(MainActivity.this, "Location retrieved form GPS", Toast.LENGTH_SHORT).show();
        if (capt_image.equals("empty"))
            bktask.execute("gps", imei, msg, ""+latitude,""+longitude, "false", "", "false");
        else
            bktask.execute("gps", imei, msg,""+latitude,""+longitude, "true", capt_image, "false");



    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result);

    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Updates the address in the UI.
     */


    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested) {
//            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mFetchAddressButton.setEnabled(false);
            et.setEnabled(false);
            et.setFocusable(false);

        } else {
//            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
            et.setEnabled(true);
            et.setFocusable(true);


        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void GPSloc(int resultCode, Bundle resultData) {
        // Display the address string or an error message sent from the intent service.
        mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
        //displayAddressOutput();
        Log.v("Location", "abc");

        // Show a toast message if an address was found.
        if (resultCode == Constants.SUCCESS_RESULT) {
            showToast(getString(R.string.address_found));
//                EditText et = (EditText) findViewById(R.id.edit);
            et.setFocusable(false);
            View.OnTouchListener touchListener = new View.OnTouchListener() {
                public boolean onTouch(final View v, final MotionEvent motionEvent) {
                    if (v.getId() == 1) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_UP:
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }
                    }
                    return false;
                }
            };
//                et.setLayoutParams(new TableLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT,0f));
            et.setOnTouchListener(touchListener);
            String msg = et.getText().toString();
//                 loc = "loc_empty";
            String loc = mAddressOutput;
//                if(!loc.equals("loc_empty")){
            BackgroundTask bktask = new BackgroundTask(MainActivity.this);
            TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            //Log.v("MainActivity",imei+msg+loc);
            if (capt_image.equals("empty"))
                bktask.execute("gps", imei, msg, loc, "false", "", "true");
            else
                bktask.execute("gps", imei, msg, loc, "true", capt_image, "true");
            isGPS = true;
        }
        isGPS = false;
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Log.v("GPS",GPSloc(resultCode,resultData));
           /* while (!GPSloc(resultCode, resultData)) {
                time2 = System.currentTimeMillis();
               Log.v("Time",Long.toString(time1)+" "+Long.toString(time2));
                if (time2 - time1 >= 5000) {
                    flag = 0;
                    Log.v("Flag",Integer.toString(flag));
                    break;
                }
            }*/
            GPSloc(resultCode, resultData);
        }
    }

    protected void NWloc() {
        showToast("Sorry, your GPS is not working properly.Location coordinates will be sent using network provider.");
                et.setFocusable(false);
                View.OnTouchListener touchListener = new View.OnTouchListener() {
                    public boolean onTouch(final View v, final MotionEvent motionEvent) {
                        if (v.getId() == 1) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_UP:
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }
                        }
                        return false;
                    }
                };
//                et.setLayoutParams(new TableLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT,0f));
                et.setOnTouchListener(touchListener);
                String msg = et.getText().toString();
//                 loc = "loc_empty";
//                if(!loc.equals("loc_empty")){
                BackgroundTask bktask = new BackgroundTask(MainActivity.this);
                TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = tm.getDeviceId();
        AppLocationService appLocationService = new AppLocationService(MainActivity.this);
                //Log.v("MainActivity",imei+msg+loc);
        Location nwLocation = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);
        if (nwLocation != null) {
            double latitude = nwLocation.getLatitude();
            double longitude = nwLocation.getLongitude();
            bktask = new BackgroundTask(MainActivity.this);
            String loc = Double.toString(latitude) + " , " + Double.toString(longitude);
            Toast.makeText(MainActivity.this, "Location retrieved form Network", Toast.LENGTH_SHORT).show();
//            mProgressBar.setVisibility(ProgressBar.GONE);
            if (capt_image.equals("empty"))
                bktask.execute("gps", imei, msg, Double.toString(latitude),Double.toString(longitude), "false", "", "false");
            else
                bktask.execute("gps", imei, msg, Double.toString(latitude),Double.toString(longitude), "true", capt_image, "false");
        }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.

            mAddressRequested = false;

        updateUIWidgets();
        }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
