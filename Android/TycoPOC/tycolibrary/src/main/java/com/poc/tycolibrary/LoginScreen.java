package com.poc.tycolibrary;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by inkthanga on 2/25/2015.
 */
public class LoginScreen extends BaseActivity implements SurfaceHolder.Callback {
    EditText  passwordValue_login;
    String check_password="1234";

    private static final String TAG = "Recorder";
    public static SurfaceView mSurfaceView;
    public static SurfaceHolder mSurfaceHolder;
    public static Camera mCamera;
    public static boolean mPreviewRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);
        //userIDValue_login = (EditText) findViewById(R.id.userIDValue_login);
        passwordValue_login = (EditText) findViewById(R.id.passwordValue_login);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String password = passwordValue_login.getText().toString().trim();
                        if(password.equals(check_password)){
                           Intent mainIntent = new Intent(getApplicationContext(),DriverRoute.class);
                            startActivity(mainIntent);
                        }

                       /* Intent mainIntent = new Intent(getApplicationContext(),DriverRoute.class);
                        startActivity(mainIntent);*/
                    }
                });

        ImageButton panicBtn = (ImageButton) findViewById(R.id.panicBtn);
        panicBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent mainIntent = new Intent(getApplicationContext(),CameraRecorder.class);
                        startActivity(mainIntent);
                    }
                });

        Button settingsBtn = (Button) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        showToast("Yet to be implemented..");
                    }
                });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}
