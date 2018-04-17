package com.upv.rosiebelt.safefit.fragments;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arbelkilani.bicoloredprogress.BiColoredProgress;
import com.upv.rosiebelt.safefit.R;
import com.upv.rosiebelt.safefit.sql.DBActivities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


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

//    variables
    DBActivities dbActivities;
    private boolean finishThread = false;

    private TextView textview_x, textview_y, textview_z;
    private TextView textgyro_x, textgyro_y, textgyro_z;
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_activity, container, false);

        dbActivities = new DBActivities(getActivity());
        Cursor todayActivity = dbActivities.dataToday();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String start, end;
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        while(todayActivity.moveToNext()){
            end = todayActivity.getString(todayActivity.getColumnIndex(DBActivities.ActivitiesEntry.COLUMN_TIME_END));
            start = todayActivity.getString(todayActivity.getColumnIndex(DBActivities.ActivitiesEntry.COLUMN_TIME_START));
            try{
                calendarEnd.setTime(dateFormat.parse(end));
                calendarStart.setTime(dateFormat.parse(start));

            }catch (Exception e){
                Toast.makeText(getActivity(), "Error in Parsing Date", Toast.LENGTH_SHORT).show();
            }

        }

        BiColoredProgress progressRunning = rootView.findViewById(R.id.progress_running);
        progressRunning.setProgress(87f);
        progressRunning.setAnimated(true, 4000);

        BiColoredProgress progressWalking = rootView.findViewById(R.id.progress_walking);
        progressWalking.setProgress(33f);
        progressWalking.setAnimated(true, 4000);

        BiColoredProgress progressBiking = rootView.findViewById(R.id.progress_biking);
        progressBiking.setProgress(34f);
        progressBiking.setAnimated(true, 4000);

        BiColoredProgress progressVehicle = rootView.findViewById(R.id.progress_vehicle);
        progressVehicle.setProgress(10f);
        progressVehicle.setAnimated(true, 4000);


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
        localsensormanager.registerListener(this.lis, localsensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        localsensormanager.registerListener(this.lis, localsensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);

    }

    public void unregister(){
        ((SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this.lis);
    }

    private SensorEventListener lis = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor sensor = sensorEvent.sensor;
            if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                textview_x.setText("X: "+ sensorEvent.values[0]);
                textview_y.setText("Y: "+ sensorEvent.values[1]);
                textview_z.setText("Z: "+ sensorEvent.values[2]);
            }

            if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
                textgyro_x.setText("X: "+sensorEvent.values[0]);
                textgyro_y.setText("X: "+sensorEvent.values[1]);
                textgyro_z.setText("X: "+sensorEvent.values[2]);
            }

        }

        public void onAccuracyChanged(Sensor paramSensor, int paramInt){

        }

    };


}
