package com.stirling.senpino;

import com.stirling.senpino.Models.HitsObjects.HitsObjectD;
import com.stirling.senpino.Models.POJOs.RespuestaB;
import com.stirling.senpino.Models.POJOs.RespuestaU;
import com.stirling.senpino.Models.gson2pojo.Example;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

//import com.stirling.developments.Models.gson2pojo.Example;

public interface ElasticSearchAPI {

    //Llamada para buscar dispositivos. Headermap para autenticacion y body para query json
    @POST("/stf_dispositivo/_search")
    Call<HitsObjectD> searchDispositivo(@HeaderMap Map<String, String> headers,
                                        @Body RequestBody params);

    //Llamada para introducir un usuario nuevo dispositivo en la base de datos. //no utiliz.
    @POST("/stf_dispositivo/_doc")
    Call<RespuestaU> postDispReg(@HeaderMap Map<String, String> headers,
                                 @Body RequestBody params);

    //Prueba: LLamada para eliminar la entrada   de un dispositivo
    //usado a la hora de actualizar la ubicación de un dispositivo
    @POST("/stf_dispositivo/_delete_by_query")
    Call<RespuestaB> deleteDispByQuery(@HeaderMap Map<String, String> headers,
                                       @Body RequestBody params); //antes <RequestBody>

    //Llamada para obtener información sobre una medición
    @POST("/stf_dispositivo/_search")
    Call<HitsObjectD> searchDisp(@HeaderMap Map<String, String> headers,
                                 @Body RequestBody params);

    //Llamada para introducir una cazuela nueva en la base de datos
    @POST("/cazuelas_sukaldatzen/_doc")
    Call<RespuestaU> postCazuela(@HeaderMap Map<String, String> headers,
                                 @Body RequestBody params);

    //Lamada para borrar una entrada del índice de una cazuela
    @POST("/cazuelas_sukaldatzen/_delete_by_query")
    Call<RespuestaB> deleteCazuela(@HeaderMap Map<String, String> headers,
                                   @Body RequestBody params);

    //Llamada para obtener información acerca de una medición. En example
    @POST("/tsf_mediciontabla/_search")
    Call<Example> searchMedicion(@HeaderMap Map<String, String> headers,
                                 @Body RequestBody params);

    //Llamada para obtener información acerca de una medición. Con aggregations
    @POST("/mediciones_sukaldatzen/_search?filter_path=aggregations.myAgg.hits.hits._source*")
    Call<Example> searchHitsAgg(@HeaderMap Map<String, String> headers,
                                @Body RequestBody params);

    //Llamada para introducir una medición nueva en la base de datos
    @POST("/mediciones_sukaldatzen/_doc")
    Call<RequestBody> postMedicion();

    //
}
