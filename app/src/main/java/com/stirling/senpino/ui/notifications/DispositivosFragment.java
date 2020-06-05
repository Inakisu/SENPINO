package com.stirling.senpino.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.stirling.senpino.BluetoothActivity;
import com.stirling.senpino.ElasticSearchAPI;
import com.stirling.senpino.Constants;
import com.stirling.senpino.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DispositivosFragment extends Fragment {

    private FloatingActionButton btnAnadir;
    private Retrofit retrofit;
    private ElasticSearchAPI searchAPI;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dispositivos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance){
        super.onViewCreated(view, savedInstance);

        //Inicializamos la api de Elasticsearch
        inicializarAPI();

        btnAnadir = (FloatingActionButton) view.findViewById(R.id.anadirDispFloatingButton);

        btnAnadir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Abrimos activity de búsqueda de dispositivos BLE para sincronización
                Intent intent = new Intent(getActivity(), BluetoothActivity.class);
                startActivity(intent);
            }
        });
    }





    /**
     * Inicialización retrofit y API de Elasticsearch
     */
    private void inicializarAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_ELASTICSEARCH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        searchAPI = retrofit.create(ElasticSearchAPI.class);

    }
}
