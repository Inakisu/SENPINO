package com.stirling.senpino;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stirling.senpino.Models.BluetoothLE;
import com.stirling.senpino.Models.POJOs.RespuestaB;
import com.stirling.senpino.Models.POJOs.RespuestaU;
//import com.stirling.senpino.Utils.BleCallback;
//import com.stirling.senpino.Utils.BluetoothLEHelper;

//import org.apache.commons.codec.DecoderException;
//import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class BluetoothActivity extends AppCompatActivity {

    SharedPreferences preferences;

    private final UUID my_UUID = UUID.fromString("ba3f52c2-5caf-4f4d-9b2d-e981698856a7");
    private final static int REQUEST_ENABLE_BT = 1;

    private ListView dispEncontrados;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private ProgressBar progressBar32;
    private Button botonBuscar;
    private Button botonAceptar;
    private Button botonAceptar2;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;
    private RelativeLayout relativeLayout;
    private EditText editText;
    private EditText editTextPass;
    private EditText editTextPass2;

    private ArrayList<String> arListEncont;

    private ArrayAdapter<String> arrayAdapterDispEncontrados;

    private String queryJson = "";
    private JSONObject jsonObject;
    private String mElasticSearchPassword = Constants.elasticPassword;
    private Retrofit retrofit;
    private ElasticSearchAPI searchAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getBaseContext().getSharedPreferences("preferencias",
                Context.MODE_PRIVATE);
        setContentView(R.layout.activity_bluetooth);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        //Verificamos que el Bluetooth esté encendido, y si no lo está pedimos encenderlo
       /* if (adapter == null || !adapter.isEnabled()) {
            Intent enableBtIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/

        //Inicializamos la API
        //inicializarAPI();

        arListEncont = new ArrayList<String>();

        //Inicializamos elementos de la interfaz
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        botonBuscar = (Button) findViewById(R.id.buscarButton);
        botonBuscar.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.GONE);

        //Pedimos permisos en runtime, aparte de en el AndroidManifest, en caso de ser necesario
        solicitarPermisos();

        //Inicializamos list views para poder ir añadiendo
        dispEncontrados = (ListView) findViewById(R.id.listView1);

        //Adapters para poder pasar a las ListViews desde arrays
        arrayAdapterDispEncontrados = new ArrayAdapter<String>(this,
                R.layout.text1, arListEncont);

        //Hacemos visible nuestro dispositivo
        //hacerVisible(); //EN MAINACTIVITY??


    }
    //En este método se solicitan los permisos necesarios para utilizar
    // funcionalidades Bluetooth
    private void solicitarPermisos() { //hay alternativa en la pag web indicada al comienzo del documento
        final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    ((TextView) new AlertDialog.Builder(this)
                            .setTitle("Runtime Permissions up ahead")
                            .setMessage(Html.fromHtml("<p>Para ver dispositivos " +
                                    "bluetooth cercanos pulse \"Permitir\" en el popup de " +
                                    "permisos.</p><p>Para más información " +
                                    " <a href=\"http://developer.android.com/about/versions/" +
                                    "marshmallow/android-6.0-changes.html#behavior-hardware-id\">" +
                                    "pulse aquí.</a>.</p>"))
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ContextCompat.checkSelfPermission(getBaseContext(),
                                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                            PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(BluetoothActivity.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                                    }
                                }
                            })
                            .show()
                            .findViewById(android.R.id.message))
                            .setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }
    }
}



