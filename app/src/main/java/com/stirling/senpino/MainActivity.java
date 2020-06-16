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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.stirling.senpino.ui.dashboard.DashboardFragment;
import com.stirling.senpino.ui.home.HomeFragment;
import com.stirling.senpino.ui.notifications.DispositivosFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {




//==============TODO LO RELACIONADO CON BLUETOOTH DEBERÍA IR EN UN BACKGROUND SERVICE PARA QUE SE PUEDA CONECTAR AL DISPOSITIVO AUNQUE LA APP NO ESTÉ ABIERTA==========//
//Posible solución?: https://stackoverflow.com/questions/47007666/how-to-scan-and-connect-ble-devices-when-app-is-in-background-in-android



    private String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setOnNavigationItemSelectedListener(this);

        setInitialFragment();

        //Search for previously bonded devices
        Bluetooth.getInstance().findDevices(this);
//        Bluetooth.getInstance().scanLeDevice(true, this);
        //It should connect to previously bonded devices stored in Android BT cache
    }
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout,
                selectedFragment).commit();

        return true;
    }
    //Stablishes the fragment to be shown on app launch
    private void setInitialFragment() {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_layout, new HomeFragment());
            fragmentTransaction.commit();
            }

}
