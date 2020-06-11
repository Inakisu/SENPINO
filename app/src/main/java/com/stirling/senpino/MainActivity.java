package com.stirling.senpino;
//Información de lo que he ido haciendo obtenida de aquí, son 3 posts, este el 1º: https://medium.com/@martijn.van.welie/making-android-ble-work-part-1-a736dcd53b02
//Iñaki Z - correode.iz@gmail.com
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.stirling.senpino.ui.dashboard.DashboardFragment;
import com.stirling.senpino.ui.home.HomeFragment;
import com.stirling.senpino.ui.notifications.DispositivosFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {



//==============TODO LO RELACIONADO CON BLUETOOTH DEBERÍA IR EN UN BACKGROUND SERVICE PARA QUE SE PUEDA CONECTAR AL DISPOSITIVO AUNQUE LA APP NO ESTÉ ABIERTA==========//
//Posible solución?: https://stackoverflow.com/questions/47007666/how-to-scan-and-connect-ble-devices-when-app-is-in-background-in-android



    private String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Search for previously bonded devices
//        Bluetooth.getInstance().findDevices(getApplicationContext());
        Bluetooth.getInstance().scanLeDevice(true, getApplicationContext());

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home: //id del boton del bottomNavigationBar
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.navigation_dashboard:
                            selectedFragment = new DashboardFragment();
                            break;
                        case R.id.navigation_notifications:
                            selectedFragment = new DispositivosFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                            selectedFragment).commit();

                    return true;
                }
            };

    private void escanearDispositivos(){
         BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter(); //adapt. BT
         BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

        ScanSettings scanSettings = new ScanSettings.Builder() //opciones de escaneo
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED) //modo low power: busca 0.5 segs pausa de 4.5 segs. Balanced: busca 2 segs pausa de 2 segs. Low_latency: búsqueda contínua
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE) //agresivo en la búsqueda de disps. Los muestra aunque tenga baja señal...
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT) // One advertisement is enough for a match
                .setReportDelay(0L)
                .build();

        List<ScanFilter> filters = null;
        /*if(serviceUUIDs != null) { //si filtramos por UUID
            filters = new ArrayList<>();
            for (UUID serviceUUID : serviceUUIDs) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setServiceUuid(new ParcelUuid(serviceUUID))
                        .build();
                filters.add(filter);
            }
        }*/
        /*if(names != null) { //Si filtramos por el nombre del dispositivo
            filters = new ArrayList<>();
            for (String name : names) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setDeviceName(name)
                        .build();
                filters.add(filter);
            }
        }*/ //también se puede filtrar por dirección MAC pero no nos interesa

//        Bluetooth.getInstance().escanearBt(filters);

        /*if (scanner != null) {
            scanner.startScan(filters, scanSettings, scanCallback);
            Log.d(TAG, "Scan started.");
        }  else {
            Log.e(TAG, "Could not get scanner object.");
        }*/
    }


}
