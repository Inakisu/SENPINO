package com.stirling.senpino.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stirling.senpino.Measurement;
import com.stirling.senpino.R;
import com.stirling.senpino.ui.MeasurementDataAdapter;
import com.stirling.senpino.ui.SwipeController;
import com.stirling.senpino.ui.SwipeControllerActions;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView textUser;
    private TextView textWeight;
    private TextView textTimestamp;

    SharedPreferences preferences;
    private ArrayList<Measurement> arListMeas;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MeasurementDataAdapter mAdapter;
    SwipeController swipeController = null;

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
                        setupRecyclerView();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        //Shared preferences
        preferences = getActivity().getSharedPreferences("preferencias", Context.MODE_PRIVATE);


    }
    //Update the view with measurements stored in list, wich is first obtained from shared preferences
    private void myUpdateOperation() {

        String jsonArList = preferences.getString("MARRAYKEY","");
        Type type = new TypeToken<ArrayList<Measurement>>(){}.getType();
        Gson gson = new Gson();
        arListMeas = gson.fromJson(jsonArList, type);
        if(!arListMeas.isEmpty()){
            for (int i = 0; arListMeas.size() >i ; i++){
                Log.i("arList","Meas List from SP: " + arListMeas.get(i).getUser()
                        +", " + arListMeas.get(i).getTimestamp() + ", " + arListMeas.get(i).getWeight());
            }
        }
        mAdapter = new MeasurementDataAdapter(arListMeas);


    }

    private void setupRecyclerView(){


        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                //mAdapter.players.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

}
