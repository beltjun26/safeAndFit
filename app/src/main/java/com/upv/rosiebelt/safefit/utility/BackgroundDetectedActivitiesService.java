package com.upv.rosiebelt.safefit.utility;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.upv.rosiebelt.safefit.HomeActivity;

/**
 * Created by root on 3/27/18.
 */

public class BackgroundDetectedActivitiesService extends Service{
    private final String TAG = BackgroundDetectedActivitiesService.class.getSimpleName();

    private Intent intentService;
    private PendingIntent pendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;
    public int recentActivity = 0, recentConfidence = 0;
    public String startTime;

    IBinder iBinder = new BackgroundDetectedActivitiesService.LocalBinder();

    public class LocalBinder extends Binder{
        public BackgroundDetectedActivitiesService getServiceInstant(){
            return BackgroundDetectedActivitiesService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activityRecognitionClient = ActivityRecognition.getClient(this);
        intentService = new Intent(this, DetectedActivitiesIntentService.class);
        pendingIntent = PendingIntent.getService(this, 1, intentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler();


    }
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void requestActivityUpdatesButtonHandler(){
        Task<Void> task = activityRecognitionClient.requestActivityUpdates(Constants.DETECTION_INTERVAL_IN_MILLISECONDS, pendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Success requested activity updates", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Requesting activity updates failed to start", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void removeActivityUpdatesButtonHandler(){
        Task<Void> task = activityRecognitionClient.removeActivityUpdates(
                pendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(), "Removed activity updates successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to remove activity updates!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdatesButtonHandler();
    }

    public void setRecentActivity(int recentActivity){
        this.recentActivity = recentActivity;
    }
    public int getRecentActivity(){
        return this.recentActivity;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public String getStartTime(){
        return startTime;
    }

    public void setRecentConfidence(int confidence){
        this.recentConfidence = confidence;
    }
    public int getRecentConfidence(){
        return recentConfidence;
    }
}
