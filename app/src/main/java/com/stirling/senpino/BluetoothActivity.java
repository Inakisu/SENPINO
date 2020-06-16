package com.stirling.senpino;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.stirling.senpino.Utils.BleCallback;
//import com.stirling.senpino.Utils.BluetoothLEHelper;

//import org.apache.commons.codec.DecoderException;
//import org.apache.commons.codec.binary.Hex;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Retrofit;

public class BluetoothActivity extends AppCompatActivity {

    SharedPreferences preferences;

    private final UUID my_UUID = UUID.fromString("ba3f52c2-5caf-4f4d-9b2d-e981698856a7");
    private final static int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 5 seconds.
    private static final long SCAN_PERIOD = 5000;
    private boolean mScanning;
    private Handler mHandler;
    private ListView foundDevicesListView;
    private ProgressBar progressBar2;
    BluetoothDevice selDevice;
    private ArrayList<BluetoothLE> arBLEFound;
    private Button botonBuscar;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> foundDevices;
    private ArrayList<String> foundDevicesNames;
    private ArrayAdapter<String> foundDevicesNamesAdapter;
    private String queryJson = "";
    private JSONObject jsonObject;
    private String mElasticSearchPassword = Constants.elasticPassword;
    private Retrofit retrofit;
    private ElasticSearchAPI searchAPI;
    Bluetooth bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getBaseContext().getSharedPreferences("preferencias",
                Context.MODE_PRIVATE);
        setContentView(R.layout.activity_bluetooth);
        mHandler = new Handler();
        bt = new Bluetooth(this   );

        //Verificamos que el Bluetooth esté encendido, y si no lo está, pedimos encenderlo
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
        //Obtenemos instancia Bluetooth
        bt = Bluetooth.getInstance(this);
        mBluetoothAdapter = bt.getBluetoothAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        //Inicializamos la API
        //inicializarAPI(); //todavía no implementado

        foundDevices = new ArrayList<BluetoothDevice>();
        foundDevicesNames = new ArrayList<String>();

        //Inicializamos elementos de la interfaz
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        botonBuscar = (Button) findViewById(R.id.buscarButton);
        botonBuscar.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.GONE);

        //Pedimos permisos en runtime, aparte de en el AndroidManifest, en caso de ser necesario
        solicitarPermisos();

        //Inicializamos list views para poder ir añadiendo
        foundDevicesListView = (ListView) findViewById(R.id.listView1);

        //Adapters para poder pasar a las ListViews desde arrays
        foundDevicesNamesAdapter = new ArrayAdapter<String>(this,
                R.layout.text1, foundDevicesNames);

        //Listener for 'find' button
        botonBuscar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!foundDevices.isEmpty()){
                    foundDevices.clear();
                }
                if(!foundDevicesNames.isEmpty()){
                    foundDevicesNames.clear();
                }
                if(!foundDevicesNamesAdapter.isEmpty()){
                    foundDevicesNamesAdapter.clear();
                }
                if(bt.isReadyForScan()){
                    bt.scanLeDevice(true);
                    botonBuscar.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar2.setVisibility(View.GONE);
                            botonBuscar.setVisibility(View.VISIBLE);
                            //Obtenemos lista de dispositivos encontrados
                            arBLEFound = bt.getListDevices();
                            //Convertimos a String para poder mostrarlos en la ListView de disp. encont.
                            fromDeviceToString(arBLEFound);
                        }
                    }, bt.getScanPeriod());
                }

            }
        });

        foundDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Stop progressbar animation
                progressBar2.setVisibility(View.GONE);
                botonBuscar.setVisibility(View.VISIBLE);
                final String info = ((TextView) view).getText().toString();

                //Get device MAC address on click
                String dirMAC = info.substring(info.length()-17);
                Log.i("TAG", "Selected devices' mac address: " + dirMAC);

                //Connect to selected device
              for (BluetoothLE bte : arBLEFound){
                    if(bte.getMacAddress().equals(dirMAC)){
                        selDevice = bte.getDevice();
                        break;
                    }
                }
                bt.connect(selDevice);

                //.................................................
            }
        });


    }

    /**
     * Fill an array with devices names to show on ListView
     * @param arrayEncBTDevice array of btDevices from wich we want to get names
     */
    //Transformar disp. Bluetooth a información en String
    private void fromDeviceToString(ArrayList<BluetoothLE> arrayEncBTDevice){
        ArrayList<String> arrayEncString = new ArrayList<String>();
        for(BluetoothLE bluetoothLE : arrayEncBTDevice){
            String aString = bluetoothLE.getName() +"\n"+bluetoothLE.getMacAddress();
            //arrayEncString.add(aString);
            foundDevicesNames.add(aString);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }



    /**
     * IZ :Necessary permissions for using Bluetooth are asked
     */
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




