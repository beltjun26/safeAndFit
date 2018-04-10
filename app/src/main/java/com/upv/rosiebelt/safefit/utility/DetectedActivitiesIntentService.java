package com.upv.rosiebelt.safefit.utility;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.upv.rosiebelt.safefit.sql.DBActivities;
import com.upv.rosiebelt.safefit.sql.DBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by root on 3/26/18.
 */

public class DetectedActivitiesIntentService extends IntentService{

    DBActivities dbActivities;
    int recentActivity = 0;
    private int recentConfidence = 0;
    boolean mBound;

    BackgroundDetectedActivitiesService backgroundDetectedActivitiesService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundDetectedActivitiesService.LocalBinder binder  = (BackgroundDetectedActivitiesService.LocalBinder) iBinder;
            backgroundDetectedActivitiesService = binder.getServiceInstant();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();
    public DetectedActivitiesIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("on Handle Intent", "Created successufuly");
        if(mBound){
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            ArrayList<DetectedActivity> detectedActivityArrayList = (ArrayList) result.getProbableActivities();
            recentActivity = backgroundDetectedActivitiesService.getRecentActivity();
            String startTime = backgroundDetectedActivitiesService.getStartTime();
            for(DetectedActivity detectedActivity: detectedActivityArrayList){
                if(detectedActivity.getConfidence() > Constants.CONFIDENCE){
                    Log.e(TAG, "Detected Activity " + detectedActivity.getType() + " " +detectedActivity.getConfidence());
                    if(filterActivity(detectedActivity.getType()) != -1 && detectedActivity.getType() != recentActivity){
                        Log.e(TAG, "Recent " + recentActivity);
                        if (recentActivity != 0) {
                            Log.e("Output", "Enters the db manipulation");
                            dbActivities.addData(activityToString(recentActivity), startTime, getDateTime());
                        }
                        if(detectedActivity.getType() == DetectedActivity.ON_BICYCLE){
                            recentActivity = DetectedActivity.ON_BICYCLE;
                        }
                        if(detectedActivity.getType() == DetectedActivity.WALKING){
                            recentActivity = DetectedActivity.WALKING;
                        }
                        if(detectedActivity.getType() == DetectedActivity.RUNNING){
                            recentActivity = DetectedActivity.RUNNING;
                        }
                        if(detectedActivity.getType() == DetectedActivity.STILL){
                            recentActivity = DetectedActivity.STILL;
                        }
                        if(detectedActivity.getType() == DetectedActivity.IN_VEHICLE){
                            recentActivity = DetectedActivity.IN_VEHICLE;
                        }
                        backgroundDetectedActivitiesService.setStartTime(getDateTime());
                        backgroundDetectedActivitiesService.setRecentActivity(recentActivity);
                        backgroundDetectedActivitiesService.setRecentConfidence(detectedActivity.getConfidence());
                        broadcastActivity(detectedActivity);
                    }

                }
            }
        }
    }

    private void broadcastActivity(DetectedActivity detectedActivity) {
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", detectedActivity.getType());
        intent.putExtra("confidence", detectedActivity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Creation", "Created the Intent Service");
        dbActivities = new DBActivities(DetectedActivitiesIntentService.this);
        Intent intent = new Intent(DetectedActivitiesIntentService.this, BackgroundDetectedActivitiesService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    String activityToString(int activityInt){
        if(activityInt==DetectedActivity.IN_VEHICLE){
            return "Vehicle";
        }if(activityInt==DetectedActivity.ON_BICYCLE){
            return "Bicycle";
        }if(activityInt==DetectedActivity.RUNNING){
            return "Running";
        }if(activityInt==DetectedActivity.WALKING){
            return "Walking";
        }if(activityInt==DetectedActivity.STILL){
            return "Still";
        }
        return null;
    }

    int filterActivity(int activityInt){
        if(activityInt==DetectedActivity.IN_VEHICLE){
            return activityInt;
        }if(activityInt==DetectedActivity.ON_BICYCLE){
            return activityInt;
        }if(activityInt==DetectedActivity.RUNNING){
            return activityInt;
        }if(activityInt==DetectedActivity.WALKING){
            return activityInt;
        }if(activityInt==DetectedActivity.STILL){
            return activityInt;
        }
        return -1;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
