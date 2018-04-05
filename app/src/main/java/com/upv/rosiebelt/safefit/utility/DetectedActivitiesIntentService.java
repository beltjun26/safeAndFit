package com.upv.rosiebelt.safefit.utility;

import android.app.IntentService;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by root on 3/26/18.
 */

public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();
    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, "Testing toast", Toast.LENGTH_SHORT).show();
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        ArrayList<DetectedActivity> detectedActivityArrayList = (ArrayList) result.getProbableActivities();
        for(DetectedActivity detectedActivity: detectedActivityArrayList){
            Log.e(TAG, "Detected Activity " + detectedActivity.getType() + " " +detectedActivity.getConfidence());
            broadcastActivity(detectedActivity);
        }
    }

    private void broadcastActivity(DetectedActivity detectedActivity) {
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", detectedActivity.getType());
        intent.putExtra("confidence", detectedActivity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


}
