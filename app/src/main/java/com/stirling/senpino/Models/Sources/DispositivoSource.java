package com.stirling.senpino.Models.Sources;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.senpino.Models.POJOs.Dispositivo;

@IgnoreExtraProperties
public class DispositivoSource {

    @SerializedName("_source")
    @Expose
    private Dispositivo dispositivo;

    public Dispositivo getDispositivo(){ return dispositivo;}

    public void setDispositivo(Dispositivo dispositivo){ this.dispositivo = dispositivo;}

}
