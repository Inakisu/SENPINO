package com.stirling.senpino.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.stirling.senpino.R;

public class HomeFragment extends Fragment {

    private TextView textUser;
    private TextView textWeight;
    private TextView textTimestamp;

    public String getTextUser() {return textUser.getText().toString();}
    public void setTextUser(String text) {
        textUser.setText(text);}
    public String getTextWeight() {return textWeight.getText().toString();}
    public void setTextWeight(String text) {
        textWeight.setText(text);}
    public String getTextTimestamp() {return textTimestamp.getText().toString();}
    public void setTextTimestamp(String text) {
        textTimestamp.setText(text);}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance){
        super.onViewCreated(view, savedInstance);

        textUser = (TextView) view.findViewById(R.id.textValorUsuario);
        textWeight = (TextView) view.findViewById(R.id.textValorPeso);
        textTimestamp = (TextView) view.findViewById(R.id.textValorTimestamp);

    }
    public void updateVars(){

    }
}
