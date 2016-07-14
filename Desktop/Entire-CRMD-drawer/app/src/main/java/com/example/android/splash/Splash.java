package com.example.android.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.example.android.splash.data.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ImageView im = (ImageView) findViewById(R.id.imageView);
        im.setImageResource(R.drawable.log2);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
        Log.v("parameters:", Float.toString(dpWidth));
        Log.v("parameters:", Float.toString(dpHeight));
        // final ImageView iv = (ImageView) findViewById(R.id.imageView2);
        // final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_in);
        ImageView imgview = (ImageView) findViewById(R.id.imageView2);
        TranslateAnimation tanim = new TranslateAnimation(0.0f, 1220.0f, 0.0f, 0.0f);
        tanim.setDuration(6000);
        tanim.setRepeatCount(0);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    /*Intent openMainActivity= new Intent("com.coderefer.androidsplashscreenexample.MAINACTIVITY");
                    startActivity(openMainActivity);*/
                    SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    if (loginPreferences.getBoolean("saveLogin", false)) {
                        String name,dept;

                            name = loginPreferences.getString("name", "");
                            dept= loginPreferences.getString("dept", "");
                        Intent i=new Intent(Splash.this, drawer.class);
                        i.putExtra("name",name);
                        i.putExtra("dept",dept);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(Splash.this, LoginActivity.class);
                        startActivity(i);
                    }
                }
            }
        };
        timer.start();
        imgview.startAnimation(tanim);
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (loginPreferences.getBoolean("saveLogin", false))
            getPendingRows();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    public void getPendingRows() {
        // List<String> result = new ArrayList<String>();
        DBHelper db;
        db = new DBHelper(this);
        Cursor cursor = db.getPendingList();
        //   cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String msg = cursor.getString(0);
            String latitude=cursor.getString(1);
            String longitude = cursor.getString(2);
            String imei=cursor.getString(3);
            String image = cursor.getString(4);
            String status = cursor.getString(5);
            String time = cursor.getString(6);
            BackgroundTask bktask=new BackgroundTask(Splash.this);
            bktask.execute("gps", imei, msg, latitude,longitude, "true", image,"false", time);


        }
        cursor.close();
    }

}





/**
 * Created by Kartik Sethi on 01-Jun-16.
 */
/**
 *
 public class Splash extends Activity {

@Override protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.splash);

final ImageView iv = (ImageView) findViewById(R.id.imageView);
final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_in);
final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);

iv.startAnimation(an);
an.setAnimationListener(new Animation.AnimationListener() {
@Override public void onAnimationStart(Animation animation) {

}

@Override public void onAnimationEnd(Animation animation) {
iv.startAnimation(an2);
finish();
Intent i = new Intent(getBaseContext(),MainActivity.class);
startActivity(i);
}

@Override public void onAnimationRepeat(Animation animation) {

}
});
}
}
 */