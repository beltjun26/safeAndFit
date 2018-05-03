package com.upv.rosiebelt.safefit.fragments;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.upv.rosiebelt.safefit.R;
import com.upv.rosiebelt.safefit.utility.AccidentDetection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int testVariable;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private float[] gravityData = null, magneticData = null;

    private OnFragmentInteractionListener mListener;

    private SensorManager sensorManager;
    private Sensor mLinearAcceleration;

    private Thread thread, testThread;
    private boolean plotData = true;
    private LineChart lineChart;

    private View rootView;

    private AccidentDetection accidentDetection;
    public DataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataFragment newInstance(String param1, String param2) {
        DataFragment fragment = new DataFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_data, container, false);
        register();
        lineChart = rootView.findViewById(R.id.line_chart);


        lineChart.getDescription().setEnabled(true);
        lineChart.getDescription().setText("Sensor Plot Data");

        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(false);
        lineChart.setBackgroundColor(Color.WHITE);

        LineData lineData = new LineData();
        lineData.setValueTextColor(Color.WHITE);
        lineChart.setData(lineData);


        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setDrawBorders(false);


        startPlot();
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
    public void onPause() {
        super.onPause();
        if(thread != null){
            thread.interrupt();
        }
        unregister();
    }

    @Override
    public void onResume() {
        super.onResume();
        thread.start();
        register();
    }

    @Override
    public void onDestroy() {
        unregister();
        thread.interrupt();
        super.onDestroy();
    }

    private void addEntry(float[] earthAcc){

        LineData data = lineChart.getData();

        if(data != null){
            ILineDataSet set1 = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);
            if(set1 == null){
                set1 = createSet(1);
                data.addDataSet(set1);
            }
            if(set2 == null){
                set2 = createSet(2);
                data.addDataSet(set2);
            }
            if(set3 == null){
                set3 = createSet(3);
                data.addDataSet(set3);
            }

            set1.addEntry(new Entry(set1.getEntryCount(), (earthAcc[0] + 5), 0));
            set2.addEntry(new Entry(set2.getEntryCount(), (earthAcc[1] + 5), 0));
            set3.addEntry(new Entry(set3.getEntryCount(), (earthAcc[2] + 5), 0));
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(15);
            lineChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet(int setNumber){
        LineDataSet set= new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        if(setNumber == 1){
            set.setColor(Color.MAGENTA);
        }else if(setNumber == 2){
            set.setColor(Color.BLUE);
        }else{
            set.setColor(Color.GREEN);
        }
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private SensorEventListener lis = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
//            Calculation to compute the accelation on earthsPerspective rather the the acceleration given by the accelerometer (mobile perspective)
            if(plotData){
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

                    addEntry(earthAcc);
                    testVariable++;
                    plotData = false;
                }else if (sensor.getType() == Sensor.TYPE_GRAVITY){
                    gravityData = sensorEvent.values;
                }else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                    magneticData = sensorEvent.values;
                }


            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void startPlot(){
        if(thread != null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    plotData = true;

                    try {
                        thread.sleep(10);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
