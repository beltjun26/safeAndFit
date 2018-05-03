package com.upv.rosiebelt.safefit.utility;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.upv.rosiebelt.safefit.AccidentActivity;
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
    Sensor accelometer, magnetic, linear, gravity;
    DBSteps dbSteps;
    AccidentDetection accidentDetection;

    private float[] gravityData = null, magneticData = null;

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
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        linear = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);



        stepDetector = new StepDetector();
        stepDetector.registerListener(this);

        sensorManager.registerListener(BackgroundDetectedActivitiesService.this, accelometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(BackgroundDetectedActivitiesService.this, linear, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(BackgroundDetectedActivitiesService.this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(BackgroundDetectedActivitiesService.this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);

//        check for the current day number of steps
        numSteps = dbSteps.getSteps();
        accidentDetection = new AccidentDetection();
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

    public void updateSteps(){
        dbSteps.setSteps(numSteps);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdatesButtonHandler();
        sensorManager.unregisterListener(BackgroundDetectedActivitiesService.this);
        updateSteps();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }
        if((gravityData != null) && (magneticData != null) && (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)){
            float[] rotationMatrix = new float[16];
            float[] I = new float[16];
            float[] deviceRelativeAcceleration = new float[4];
            float[] inv = new float[16];
            float[] earthAcc = new float[16];

            deviceRelativeAcceleration[0] = sensorEvent.values[0];
            deviceRelativeAcceleration[1] = sensorEvent.values[1];
            deviceRelativeAcceleration[2] = sensorEvent.values[2];
            deviceRelativeAcceleration[3] = 0;

            SensorManager.getRotationMatrix(rotationMatrix, I, gravityData, magneticData);
            Matrix.invertM(inv, 0, rotationMatrix, 0);
            Matrix.multiplyMV(earthAcc, 0 , inv, 0, deviceRelativeAcceleration, 0);

            accidentDetection.updateData(earthAcc);
            accidentDetection.checkAccident();
            if(accidentDetection.checkAccident()){
                Intent alertAccident = new Intent(this, AccidentActivity.class);
                alertAccident.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(alertAccident);
            }
        }else if (sensor.getType() == Sensor.TYPE_GRAVITY){
            gravityData = sensorEvent.values;
        }else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magneticData = sensorEvent.values;
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
