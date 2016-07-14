package com.example.android.splash;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {


    public BlankFragment() {
        // Required empty public constructor
    }
    ImageView iv;
    TextView et;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_main, container, false);
        super.onCreate(savedInstanceState);
        String data=getActivity().getIntent().getExtras().getString("text");
        String image=getActivity().getIntent().getExtras().getString("image");
        Bitmap bmp=base64ToBitmap(image);
        Log.e("ddd", (data));
        iv=(ImageView)getView().findViewById(R.id.captured_image2);
        if(iv!=null)
            iv.setImageBitmap(bmp);



        //mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
//        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);


        et = (TextView) getView().findViewById(R.id.edit2);
        if(et!=null)
            et.setText(data);
        // Set defaults, then update using values stored in the Bundle.



        TextView report_header = (TextView) getView().findViewById(R.id.reporting_text2);
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-BlackItalic.ttf");
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


        return rootView;
    }


    /**
     * Updates fields based on data stored in the bundle.
     */


    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */


    @Override
    public void onStart() {
        super.onStart();
        //  mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        //  if (mGoogleApiClient.isConnected()) {
        //        mGoogleApiClient.disconnect();

    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

}
