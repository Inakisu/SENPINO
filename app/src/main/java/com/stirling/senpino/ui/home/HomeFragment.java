package com.stirling.senpino.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.stirling.senpino.BluetoothActivity;
import com.stirling.senpino.MainActivity;
import com.stirling.senpino.R;

public class HomeFragment extends Fragment {

    private TextView textoUsuario;
    private TextView textoPeso;
    private TextView textoTimestamp;

    public TextView getTextoUsuario() {return textoUsuario;    }
    public void setTextoUsuario(TextView textoUsuario) {    this.textoUsuario = textoUsuario;    }
    public TextView getTextoPeso() {        return textoPeso;    }
    public void setTextoPeso(TextView textoPeso) {        this.textoPeso = textoPeso;    }
    public TextView getTextoTimestamp() {        return textoTimestamp;    }
    public void setTextoTimestamp(TextView textoTimestamp) {this.textoTimestamp = textoTimestamp;}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance){
        super.onViewCreated(view, savedInstance);

        textoUsuario = (TextView) view.findViewById(R.id.textValorUsuario);
        textoPeso = (TextView) view.findViewById(R.id.textValorPeso);
        textoTimestamp = (TextView) view.findViewById(R.id.textValorTimestamp);

    }
}
