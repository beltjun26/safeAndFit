package com.upv.rosiebelt.safefit.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.upv.rosiebelt.safefit.R;
import com.upv.rosiebelt.safefit.sql.DBActivities;
import com.upv.rosiebelt.safefit.sql.DBSteps;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticsWeek.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticsWeek#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsWeek extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View rootView;
    BarChart barChart;
    DBSteps dbSteps;
    DBActivities dbActivities;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StatisticsWeek() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsWeek.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsWeek newInstance(String param1, String param2) {
        StatisticsWeek fragment = new StatisticsWeek();
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
//        create dbsteps instance
        dbSteps = new DBSteps(getActivity());

//         Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_statistics_week, container, false);

//        creating barchart object to manipulate instance in the layout
        barChart = (BarChart) rootView.findViewById(R.id.bar_chart);
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        int index = 1;
        for(int daySteps : dbSteps.dataWeek()){
            barEntries.add(new BarEntry(index, daySteps));
            index++;
        }
        /**
         *
         * Creating bar chart
         */

        BarDataSet dataset = new BarDataSet(barEntries, "# of steps");
//        prepare the label for the barchart
        final ArrayList<String> labels = new ArrayList<>();
        labels.add("Sun");
        labels.add("Mon");
        labels.add("Tue");
        labels.add("Wed");
        labels.add("Thu");
        labels.add("Fri");
        labels.add("Sat");
        BarData barData = new BarData(dataset);
        barChart.setData(barData);
//        removing x and y axis line and labels to make it cleaner to look at
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int)value-1);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.animateY(2000);
        barChart.invalidate();
        /**
         *activity calculation
         */

        dbActivities = new DBActivities(getActivity());
        long[] activityLength = dbActivities.getTodayActivityTime();
        long total = 0;
        if(activityLength != null){
            for(long time: activityLength){
                total = total + time;
            }
        }else{
            activityLength = new long[5];
        }

        float allActivityTotalTime = total - activityLength[2];
        /**
         *
         * Creating pie chart
         */

//        Running -> 4
//        Vehicle -> 3
//        Still -> 2
//        Bicycle -> 1
//        Walking -> 0
        PieChart pieChart = (PieChart) rootView.findViewById(R.id.week_piechart);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(((float)activityLength[1]/(float)allActivityTotalTime)*100, 0));
        pieEntries.add(new PieEntry(((float)activityLength[3]/(float)allActivityTotalTime)*100, 0));
        pieEntries.add(new PieEntry(((float)activityLength[4]/(float)allActivityTotalTime)*100, 0));
        pieEntries.add(new PieEntry(((float)activityLength[0]/(float)allActivityTotalTime)*100, 0));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Activites");
        pieDataSet.setColors(getResources().getColor(R.color.blue_pie), getResources().getColor(R.color.yellow_pie), getResources().getColor(R.color.red_pie), getResources().getColor(R.color.green_pie));
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Activities");
        pieChart.animateY(2000, Easing.EasingOption.EaseOutBounce);
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
}
