package com.stirling.senpino.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.stirling.senpino.Measurement;
import com.stirling.senpino.ObjectSerializer;
import com.stirling.senpino.R;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView textUser;
    private TextView textWeight;
    private TextView textTimestamp;

    SharedPreferences preferences;
    private ArrayList<Measurement> arListMeas;
    private SwipeRefreshLayout swipeRefreshLayout;

    public String getTextUser() {return textUser.getText().toString();}
    public void setTextUser(String text) {textUser.setText(text);}
    public String getTextWeight() {return textWeight.getText().toString();}
    public void setTextWeight(String text) {textWeight.setText(text);}
    public String getTextTimestamp() {return textTimestamp.getText().toString();}
    public void setTextTimestamp(String text) {textTimestamp.setText(text);}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance){
        super.onViewCreated(view, savedInstance);
        //we'll fill this arrayList from shared preferences
        arListMeas = new ArrayList<Measurement>();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("RefreshLayout", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        myUpdateOperation();
                    }
                }
        );

        //Shared preferences
        preferences = getActivity().getSharedPreferences("preferencias", Context.MODE_PRIVATE);


    }
    //Update the view with measurements stored in list, wich is first obtained from shared preferences
    private void myUpdateOperation() {
        try {
            arListMeas = (ArrayList<Measurement>) ObjectSerializer.deserialize(
                    preferences.getString("MARRAYKEY",
                    ObjectSerializer.serialize(new ArrayList<Measurement>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
