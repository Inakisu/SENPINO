package com.stirling.senpino;
//Información de lo que he ido haciendo obtenida de aquí, son 3 posts, este el 1º: https://medium.com/@martijn.van.welie/making-android-ble-work-part-1-a736dcd53b02
//Iñaki Z - correode.iz@gmail.com
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.stirling.senpino.ui.dashboard.DashboardFragment;
import com.stirling.senpino.ui.home.HomeFragment;
import com.stirling.senpino.ui.notifications.DispositivosFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {




//==============TODO LO RELACIONADO CON BLUETOOTH DEBERÍA IR EN UN BACKGROUND SERVICE PARA QUE SE PUEDA CONECTAR AL DISPOSITIVO AUNQUE LA APP NO ESTÉ ABIERTA==========//
//Posible solución?: https://stackoverflow.com/questions/47007666/how-to-scan-and-connect-ble-devices-when-app-is-in-background-in-android

    private  HomeFragment homeFragment;
    private  DashboardFragment dashboardFragment;
    private  DispositivosFragment dispositivosFragment;

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public List<BluetoothGattCharacteristic> listaChars = new ArrayList<>();
    BluetoothGatt mBluetoothGatt;
    BluetoothDevice bDevice;
    Bluetooth bt;
    BluetoothGattService serviceGatt;
    BluetoothGattCharacteristic weightCharacteristic;
    BluetoothGattCharacteristic timestampCharacteristic;
    BluetoothGattCharacteristic userCharacteristic;

    private String serviceUUID = "4fafc201-1fb5-459e-4fca-c5c9c331789b";
    private String userUUID = "beb5483e-36e1-4688-b7fa-eb07361b26a4";
    private String weightUUID = "beb5483e-36e1-4688-b7fa-eb07361b26a5";
    private String timestampUUID = "beb5483e-36e1-4688-b7fa-eb07361b26a6";

    SharedPreferences preferences;
    private String TAG = "TAG";
    private String macbt = null;
    private Handler mHandler;
    private boolean mScanning;
    private boolean cambioConectado;
    private boolean cambioDesconectado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setOnNavigationItemSelectedListener(this);
        bt = Bluetooth.getInstance(this);
        setInitialFragment();

        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        dispositivosFragment = new DispositivosFragment();

        Log.i("MainAct","onCreate");

        //SharedPreferences
        preferences = getSharedPreferences("preferencias",Context.MODE_PRIVATE);
        macbt = preferences.getString("macbt", null);

        //Search for previously bonded devices
//        Bluetooth.getInstance().findDevices(this);
//        Bluetooth.getInstance(this).scanLeDevice(true);
        if(!macbt.equals(null)){
            scanLeDevice(true);
        }
        //It should connect to previously bonded devices stored in Android BT cache


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_home: //id del boton del bottomNavigationBar
                selectedFragment = homeFragment;
                break;
            case R.id.navigation_dashboard:
                selectedFragment = dashboardFragment;
                break;
            case R.id.navigation_notifications:
                selectedFragment = dispositivosFragment;
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

    /**
     * Método encargado de escanear dispositivos Bluetooth Low Energy
     *
     * @param enable Parameter 1.
     */
    private void scanLeDevice(final boolean enable) {
        if (enable && !mScanning) {
            Log.i("scanLeDeviceFrag: ", "Starting BLE Scan....");
            mHandler = new Handler();
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("scanLeDeviceFrag: ", "Stopping BLE scan...");
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, 2*1000);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * Callback
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    new Runnable() {
                        @Override
                        public void run () {
                            Log.i("BLEFrag", "Name: " + device.getName() + " (" + device.getAddress() + ")");
                            String deviceAddress = device.getAddress();
                            if (deviceAddress.equals(macbt)) {
                                connectToDevice(device);
                            }
                        }
                    }.run();
                }
            };
    /**
     * Método utilizado para conectarse a un dispositivo BT LE.
     *
     * @param device dispositivo BT al que conectarse.
     */
    public void connectToDevice(BluetoothDevice device) {
        if (mBluetoothGatt == null) {
            Log.i("BLEFrag", "Attempting to connect to device " + device.getName() +
                    " (" + device.getAddress() + ")");
            mBluetoothGatt = device.connectGatt(this, true, gattCallback,
                    TRANSPORT_LE);
            try {
                sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bDevice = device;
            scanLeDevice(false);// will stop after first device detection
        }
    }

    /**
     * Callbacks que suceden en determinados eventos relacionados con el BT LE.
     */
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("BLEFrag", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("BLEFrag", "STATE_CONNECTED");
                    cambioConectado = true;
                    gatt.discoverServices();
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Dispositivo CONECTADO", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    //when disconnected, start searching for devices again
                    if (macbt != null && !isScanning() && bDevice==null) {
                        scanLeDevice(true);
                    }
                    cambioDesconectado = true;
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Dispositivo DESconectado", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    tvTemperature.setText("-- ºC");
                    Log.e("BLEFrag", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("BLEFrag", "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            List<BluetoothGattService> services = gatt.getServices();
//            Log.i("BLEFrag", "Services: " + services.toString());
//            BluetoothGattCharacteristic characTemp = null;
//            BluetoothGattCharacteristic characGiro = null;
//            BluetoothGattCharacteristic characLleno = null;
//
//            gattServiceD = services.get(2);
//
//            characLleno = gattServiceD.getCharacteristic(UUID.fromString(charUUID2));
//            characGiro = gattServiceD.getCharacteristic(UUID.fromString(charUUID3));
//            characTemp = gattServiceD.getCharacteristic(UUID.fromString(charUUID));
//            listaChars.add(characTemp);
//            listaChars.add(characGiro);
//            listaChars.add(characLleno);
//
//            requestCharacteristics(gatt);

            serviceGatt = gatt.getService(UUID.fromString(serviceUUID)); //we get the service
            userCharacteristic = serviceGatt.getCharacteristic(UUID.fromString(userUUID));
            weightCharacteristic = serviceGatt.getCharacteristic(UUID.fromString(weightUUID));
            timestampCharacteristic = serviceGatt.getCharacteristic(UUID.fromString(timestampUUID));

            listaChars.add(userCharacteristic);
            listaChars.add(weightCharacteristic);
            listaChars.add(timestampCharacteristic);

            requestCharacteristics(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.i("onCharRead","Entered onCharacteristicRead");



            if(characteristic.getUuid().toString().equals(userUUID)){
                Log.i("onCharRead","read user characteristic");

//                tempString = characteristic.getStringValue(0);
//                temp = Integer.parseInt(tempString);
//                Log.i("BLEFragOnCharRead", "Characteristic: " + tempString); //dataInput
            }else if(characteristic.getUuid().toString().equals(weightUUID)){
                Log.i("onCharRead","read weight characteristic");

//                lleno = characteristic.getStringValue(0).equals("1");
//                Log.i("BLEFragOnCharRead", "Characteristic: " + characteristic.getStringValue(0)); //dataInput

            }else if(characteristic.getUuid().toString().equals(timestampUUID)){
                Log.i("onCharRead","read timestamp characteristic");
//                girado = characteristic.getStringValue(0).equals("1");
//                Log.i("BLEFragOnCharRead", "Characteristic: " + characteristic.getStringValue(0)); //dataInput
            }else{
                Log.e("BLE Read: ","No characteristic matches any given UUID");
            }

            listaChars.remove(listaChars.get(listaChars.size() - 1));

            if (listaChars.size() > 0) {
                requestCharacteristics(gatt);
            }else{
                //gatt.disconnect();
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Generate new received measurement object
                //homeFragment.setTextUser("chocolate");
                gatt.discoverServices();

            }
        }
        public void requestCharacteristics(BluetoothGatt gatt) {
            gatt.readCharacteristic(listaChars.get(listaChars.size()-1));
            Log.i("requestCharRead","Llamado === RRRRRRR");
        }
        @Override
        public synchronized void onCharacteristicChanged(BluetoothGatt gatt,
                                                         BluetoothGattCharacteristic characteristic) {

            Log.i("infoFrag", "onCharacteristicChanged");
            gatt.discoverServices();
//            Log.i("BLEFragOnCharChanged", "Characteristic: " + tempString);

        }
    };

    public boolean isScanning() {
        return mScanning;
    }

    @Override
    public void onResume() {
        super.onResume();
//        bt = Bluetooth.getInstance(this);
        macbt = preferences.getString("macbt", null);
        Log.i(null, "onResume: macbt: " + macbt);
        // Bluetooth is enabled?
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (macbt != null && !bt.isScanning() && bt.getBluetoothDevice()==null) {
            Log.i("MainAct","onResume: scanLeDevice()");
//            bt.scanLeDevice(true);
            scanLeDevice(true);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Bluetooth.getInstance(this).scanLeDevice(false); //porque se pondrá a escanear la activity BluetoothAct.
        scanLeDevice(false);
    }

}
