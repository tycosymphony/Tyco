package com.poc.tycolibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by inkthanga on 2/25/2015.
 */
public class BaseActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    public void startSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(mainIntent);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
