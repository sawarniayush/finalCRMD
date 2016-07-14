package com.example.android.splash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    String name, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = this.getIntent();
        if (intent != null & intent.hasExtra("text"))
            name = intent.getStringExtra("text");
        if (intent != null & intent.hasExtra("image"))
            image = intent.getStringExtra("image");
        Bitmap bmp = base64ToBitmap(image);
       // Log.e("ddd", name);
        ImageView iv = (ImageView)findViewById(R.id.captured_image2);
        if (bmp != null)
            iv.setImageBitmap(bmp);


        //mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
//        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Log.e("name", name);
       TextView et = (TextView) findViewById(R.id.edit2);
        et.setText(name);
        // Set defaults, then update using values stored in the Bundle.



    //    report_header.setTypeface(typeFace);

//        et.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View view, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (view.getId() == R.id.edit) {
//                    view.getParent().requestDisallowInterceptTouchEvent(true);
//                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                        case MotionEvent.ACTION_UP:
//                            view.getParent().requestDisallowInterceptTouchEvent(false);
//                            break;
//                    }
//                }
//                return false;
//            }
//        });
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

}
