package com.stirling.senpino.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stirling.senpino.Measurement;
import com.stirling.senpino.R;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private LineChart chart;
    SharedPreferences preferences;
    private ArrayList<Measurement> arListMeas;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int lastX = 0;
    private List<Entry> entries = new ArrayList<Entry>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get shared preferences
        preferences = getActivity().getSharedPreferences("preferencias",
                Context.MODE_PRIVATE);

        arListMeas = new ArrayList<Measurement>();

        //initialize chart
        chart = (LineChart) view.findViewById(R.id.chart);
        initializeChart(chart);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("RefreshLayout", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        initializeChart(chart);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    private void initializeChart(LineChart chart){
        entries.clear();
        String jsonArList = preferences.getString("MARRAYKEY","");
        Type type = new TypeToken<ArrayList<Measurement>>(){}.getType();
        Gson gson = new Gson();
        arListMeas = gson.fromJson(jsonArList, type);
        if(!arListMeas.isEmpty()){
            for (int i = 0; arListMeas.size() > i ; i++){
                lastX++;
//                Log.i("arList","Meas List from SP: " + arListMeas.get(i).getUser()
//                        +", " + arListMeas.get(i).getTimestamp() + ", " + arListMeas.get(i).getWeight());
                entries.add(new Entry(lastX,Float.parseFloat(arListMeas.get(i).getWeight())));
            }
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorDataSet));
        Description description = chart.getDescription();
        Legend legend = chart.getLegend();
//        dataSet.setValueTextColor(...); // styling, ...
        LineData lineData = new LineData(dataSet);
        legend.setEnabled(false);
        description.setText(" ");
        //lineData.set
        chart.setData(lineData);
        chart.notifyDataSetChanged(); // let the chart know it's data changed
        chart.invalidate(); // refresh


    }
}
