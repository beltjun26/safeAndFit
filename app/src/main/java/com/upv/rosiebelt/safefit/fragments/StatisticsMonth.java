package com.upv.rosiebelt.safefit.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.upv.rosiebelt.safefit.R;
import com.upv.rosiebelt.safefit.sql.DBSteps;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticsMonth.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticsMonth#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsMonth extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View rootView;
    DBSteps dbSteps;
    BarChart barChart;
    LineChart lineChart;

    private OnFragmentInteractionListener mListener;

    public StatisticsMonth() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsMonth.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsMonth newInstance(String param1, String param2) {
        StatisticsMonth fragment = new StatisticsMonth();
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
        rootView =  inflater.inflate(R.layout.fragment_statistics_month, container, false);

//        create dbsteps instance
        dbSteps = new DBSteps(getActivity());

//        creating linechart
        lineChart = rootView.findViewById(R.id.line_chart);
//        creating barchart object to manipulate instance in the layout
        ArrayList<Entry> lineEntries = new ArrayList<Entry>();
        int iterator = 0;
        for (int steps : dbSteps.getDataWholeMonth()) {
            lineEntries.add(new Entry(iterator, steps));
            iterator++;
        }

        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);


        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Month Steps");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setLineWidth(2);
//        lineDataSet.setColor(getActivity().getColor("defaultBlue"));
//        lineDataSet.setCircleColor(getActivity().getColor("defaultOrange"));
//        lineDataSet.setCircleRadius(6);
//        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setDrawValues(false);
        lineDataSet.setValueTextSize(12);
//        lineDataSet.setValueTextColor(getActivity().getColor("primaryDark"));

        LineData lineData = new LineData(lineDataSet);

//        lineChart.getDescription().setText("Price in last 12 days");
//        lineChart.getDescription().setTextSize(12);
//        lineChart.setDrawMarkers(true);
//        lineChart.setMarker(markerView(context));
//        lineChart.getAxisLeft().addLimitLine(lowerLimitLine(2,"Lower Limit",2,12,getColor("defaultOrange"),getColor("defaultOrange")));
//        lineChart.getAxisLeft().addLimitLine(upperLimitLine(5,"Upper Limit",2,12,getColor("defaultGreen"),getColor("defaultGreen")));
//        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.animateY(1000);
//        lineChart.getXAxis().setGranularityEnabled(true);
//        lineChart.getXAxis().setGranularity(1.0f);
//        lineChart.getXAxis().setLabelCount(lineDataSet.getEntryCount());
        lineChart.setData(lineData);

//        create pie chart
        PieChart pieChart = (PieChart) rootView.findViewById(R.id.week_piechart);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(4f, 0));
        pieEntries.add(new PieEntry(12f, 0));
        pieEntries.add(new PieEntry(2f, 0));
        pieEntries.add(new PieEntry(8f, 0));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Activites");
        pieDataSet.setColors(getResources().getColor(R.color.blue_pie), getResources().getColor(R.color.yellow_pie), getResources().getColor(R.color.red_pie), getResources().getColor(R.color.green_pie));
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Activities");
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
