package com.poc.tycolibrary;

import android.os.Bundle;


public class SplashScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startSplash();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
