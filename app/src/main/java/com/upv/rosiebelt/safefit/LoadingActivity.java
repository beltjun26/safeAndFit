package com.upv.rosiebelt.safefit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.upv.rosiebelt.safefit.sql.DBUser;

public class LoadingActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private ProgressBar mProgressBar;
    private int progressStatus = 0;
    private Handler mHandler = new Handler();
    private Sensor accelerometer, gyroscope;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        setContentView(R.layout.activity_loading);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        final DBUser dbuser = new DBUser(LoadingActivity.this);
        int userid = dbuser.exist();
        if(userid == -1){
            final View view = (LayoutInflater.from(LoadingActivity.this)).inflate(R.layout.popup_username, null);
            AlertDialog.Builder alertbuilder = new AlertDialog.Builder(LoadingActivity.this);
            alertbuilder.setView(view);

            alertbuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name_input = ((EditText)view.findViewById(R.id.userName)).getText().toString();
                    long id = dbuser.setUser(name_input);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(20);
                            while(progressStatus < 100){
                                if(progressStatus == 30){
                                    Intent intent1 = new Intent(LoadingActivity.this, com.upv.rosiebelt.safefit.utility.BackgroundDetectedActivitiesService.class);
                                    startService(intent1);
                                    progressStatus = 70;
                                }
                                progressStatus++;
                                android.os.SystemClock.sleep(10);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setProgress(progressStatus);
                                    }
                                });
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LoadingActivity.this, HomeActivity.class);
                                    intent.putExtra("USER_ID", dbuser.exist());
                                    startActivity(intent);

                                }
                            });
                        }
                    }).start();
                }
            });
            Dialog dialog = alertbuilder.create();
            dialog.show();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {

                    mProgressBar.setProgress(20);
                    while(progressStatus < 100){
                        progressStatus++;
                        android.os.SystemClock.sleep(10);
                        if(progressStatus == 30){
                            Intent intent1 = new Intent(LoadingActivity.this, com.upv.rosiebelt.safefit.utility.BackgroundDetectedActivitiesService.class);
                            startService(intent1);
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(progressStatus);
                            }
                        });
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoadingActivity.this, HomeActivity.class);
                            intent.putExtra("USER_ID", dbuser.exist());

                            startActivity(intent);
                        }
                    });
                }
            }).start();
        }
    }
}
