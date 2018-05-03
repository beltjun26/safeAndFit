package com.upv.rosiebelt.safefit.fragments;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arbelkilani.bicoloredprogress.BiColoredProgress;
import com.upv.rosiebelt.safefit.R;
import com.upv.rosiebelt.safefit.sql.DBActivities;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    testing section code
    private double px, py, pz;
    private double gx, gy, gz;

    private float[] gravityData = null, magneticData = null;

//    variables
    DBActivities dbActivities;
    private boolean finishThread = false;

    private TextView data_x, data_y, data_z;
//     end testing section

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityFragment newInstance(String param1, String param2) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_activity, container, false);

//        attach view items data's to the variables
        data_x = rootView.findViewById(R.id.data_x);
        data_y = rootView.findViewById(R.id.data_y);
        data_z = rootView.findViewById(R.id.data_z);
        register();
        dbActivities = new DBActivities(getActivity());
        long[] activityLength = dbActivities.getTodayActivityTime();
        String[] activityTime = new String[5];
        long total = 0;
        if(activityLength != null){
//            calculate each activity actual time
            for(int i=0;i<5;i++){
                total = total + activityLength[i];
                String timeUnit = "seconds";
                activityTime[i] = "";
                int time = (int)(activityLength[i] / 1000);
                int tempTime = time/60;
                if(tempTime > 0){
                    activityTime[i] = activityTime[i] + time%60 + " seconds";
                    time = tempTime;
                    if(time == 1){
                        timeUnit = "minute";
                    }else{
                        timeUnit = "minutes";
                    }
                    tempTime = time / 60;
                    if(tempTime > 0){
                        activityTime[i] = time%60 + " " + timeUnit;
                        time = tempTime;
                        if(time == 1){
                            timeUnit = "hour";
                        }else{
                            timeUnit = "hours";
                        }
                    }
                }
                activityTime[i] = time + " " + timeUnit + " " +activityTime[i];
            }

        }else {
            total = 1;
            activityLength = new long[5];
        }

//        this variable is the base for percentage since will measure all activity percentage except that of the still activity
        float allActivityTotalTime = total - activityLength[2];
//        Running -> 4
//        Vehicle -> 3
//        Still -> 2
//        Bicycle -> 1
//        Walking -> 0

        TextView runningTime = rootView.findViewById(R.id.running_time);
        runningTime.setText(activityTime[4]);
        BiColoredProgress progressRunning = rootView.findViewById(R.id.progress_running);
        progressRunning.setProgress(((float)activityLength[4]/(float)allActivityTotalTime)*100);
        progressRunning.setAnimated(
                true, 4000);
        TextView walkingTime = rootView.findViewById(R.id.walking_time);
        walkingTime.setText(activityTime[0]);
        BiColoredProgress progressWalking = rootView.findViewById(R.id.progress_walking);
        progressWalking.setProgress(((float)activityLength[0]/ allActivityTotalTime)*100);
        progressWalking.setAnimated(true, 4000);

        TextView bikingTime = rootView.findViewById(R.id.biking_time);
        bikingTime.setText(activityTime[1]);
        BiColoredProgress progressBiking = rootView.findViewById(R.id.progress_biking);
        progressBiking.setProgress(((float)activityLength[1]/ allActivityTotalTime)*100);
        progressBiking.setAnimated(true, 4000);

        TextView vehicleTime = rootView.findViewById(R.id.vehicle_time);
        vehicleTime.setText(activityTime[3]);
        BiColoredProgress progressVehicle = rootView.findViewById(R.id.progress_vehicle);
        progressVehicle.setProgress(((float)activityLength[3]/ allActivityTotalTime)*100);
        progressVehicle.setAnimated(true, 4000);

        TextView stillTime = rootView.findViewById(R.id.still_time);
        stillTime.setText(activityTime[2]);
        BiColoredProgress progressStill = rootView.findViewById(R.id.progress_still);
        progressStill.setProgress(((float)activityLength[2]/(float)total)*100);
        progressStill.setAnimated(true, 4000);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregister();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    testing section code
    public void register(){
        SensorManager localsensormanager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        localsensormanager.registerListener(this.lis, localsensormanager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        localsensormanager.registerListener(this.lis, localsensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        localsensormanager.registerListener(this.lis, localsensormanager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void unregister(){
        ((SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this.lis);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private SensorEventListener lis = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor sensor = sensorEvent.sensor;
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

                data_x.setText("X: "+ earthAcc[0]);
                data_y.setText("Y: "+ earthAcc[1]);
                data_z.setText("Z: "+ earthAcc[2]);
            }else if (sensor.getType() == Sensor.TYPE_GRAVITY){
                gravityData = sensorEvent.values;
            }else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                magneticData = sensorEvent.values;
            }
        }
        public void onAccuracyChanged(Sensor paramSensor, int paramInt){

        }

    };


}
