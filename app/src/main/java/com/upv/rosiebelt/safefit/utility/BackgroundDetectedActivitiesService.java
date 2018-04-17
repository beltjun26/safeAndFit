package com.upv.rosiebelt.safefit.utility;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.upv.rosiebelt.safefit.sql.DBSteps;

/**
 * Created by root on 3/27/18.
 */

public class BackgroundDetectedActivitiesService extends Service  implements SensorEventListener, StepListener{
    private final String TAG = BackgroundDetectedActivitiesService.class.getSimpleName();

    private Intent intentService;
    private PendingIntent pendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;
    public int recentActivity = 0, recentConfidence = 0, numSteps = 0;
    private BackgroundListener backgroundListener;
    private boolean listening = false;
    public String startTime;
    StepDetector stepDetector;
    SensorManager sensorManager;
    Sensor accelometer;
    DBSteps dbSteps;

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

//    create dbSteps for database management

        dbSteps = new DBSteps(BackgroundDetectedActivitiesService.this);

        //        creating instance of SensorManagerdbActivities
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        stepDetector = new StepDetector();
        stepDetector.registerListener(this);

        sensorManager.registerListener(BackgroundDetectedActivitiesService.this, accelometer, SensorManager.SENSOR_DELAY_FASTEST);

//        check for the current day number of steps
        numSteps = dbSteps.getSteps();
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
        sensorManager.unregisterListener(BackgroundDetectedActivitiesService.this);
        dbSteps.setSteps(numSteps);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setBackgroundListener(BackgroundListener listener){
        this.backgroundListener = listener;
        listening = true;
    }

    public void removeBackgroundListener(){
        this.backgroundListener = null;
        listening = false;
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        if(listening){
            backgroundListener.step(numSteps);
        }
    }
    public int getSteps(){
        return numSteps;
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
