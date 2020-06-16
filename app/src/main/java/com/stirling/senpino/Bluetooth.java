package com.stirling.senpino;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

//import com.example.tiflotecnica_tresfocos.Globales.BLE_UUID;
import com.stirling.senpino.ui.home.HomeFragment;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


public class Bluetooth extends AppCompatActivity {

    private String serviceUUID = "4fafc201-1fb5-459e-4fca-c5c9c331789b";
    private String userUUID = "beb5483e-36e1-4688-b7fa-eb07361b26a4";
    private String weightUUID = "beb5483e-36e1-4688-b7fa-eb07361b26a5";
    private String timestampUUID = "beb5483e-36e1-4688-b7fa-eb07361b26a6";


    private static boolean mScanning            = false;
    private static final long SCAN_PERIOD = 5000;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED    = 1;
    public boolean ble_conectado = false;
    public boolean escaneando = false; //IZ
    private static Bluetooth bluetooth;
    private Handler mHandler = new Handler();
    private String connectoToAddress;
    HomeFragment homeFragment;
    private ArrayList<BluetoothLE> aDevices = new ArrayList<>();
    private int    mConnectionState   = STATE_DISCONNECTED;
    private static String FILTER_SERVICE        = "";

    //  public BLE_UUID uuids = new BLE_UUID();
    private Activity act;

    BluetoothDevice bluetoothDevice;
    BluetoothLeScanner btScanner;
    BluetoothAdapter bluetoothAdapter;
    BluetoothGatt mBluetoothGatt;

    BluetoothGattService serviceTiflotecnica;
    BluetoothGattService serviceFoco1;
    BluetoothGattService serviceFoco2;
    BluetoothGattService serviceFoco3;
    BluetoothGattCallback bluetoothCallback;
    BluetoothGattCharacteristic estadoCocinaCaracteristica;
    BluetoothGattCharacteristic potenciaFoco1Caracteristica;
    BluetoothGattCharacteristic timerFoco1Caracteristica;
    BluetoothGattCharacteristic potenciaFoco2Caracteristica;
    BluetoothGattCharacteristic timerFoco2Caracteristica;
    BluetoothGattCharacteristic potenciaFoco3Caracteristica;
    BluetoothGattCharacteristic timerFoco3Caracteristica;

    BluetoothGattService serviceGatt;
    BluetoothGattCharacteristic weightCharacteristic;
    BluetoothGattCharacteristic timestampCharacteristic;
    BluetoothGattCharacteristic userCharacteristic;
    Context MainContext;
    Context contexto;

    //Getters y setters
    public BluetoothGattCharacteristic getWeightCharacteristic() {
        return weightCharacteristic;
    }
    public void setWeightCharacteristic(BluetoothGattCharacteristic weightCharacteristic) {
        this.weightCharacteristic = weightCharacteristic;    }
    public BluetoothGattCharacteristic getTimestampCharacteristic() {
        return timestampCharacteristic;
    }
    public void setTimestampCharacteristic(BluetoothGattCharacteristic timestampCharacteristic) {
        this.timestampCharacteristic = timestampCharacteristic;    }
    public BluetoothGattCharacteristic getUserCharacteristic() {
        return userCharacteristic;
    }
    public void setUserCharacteristic(BluetoothGattCharacteristic userCharacteristic) {
        this.userCharacteristic = userCharacteristic;    }
    public String getConnectoToAddress() {        return connectoToAddress;    }
    public void setConnectoToAddress(String connectoToAddress) {
        this.connectoToAddress = connectoToAddress;
    }

    //Getters and setters
    public BluetoothDevice getBluetoothDevice() {
       return bluetoothDevice;
   }
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }
    public BluetoothGattCharacteristic getPotenciaFoco1Caracteristica() {
        return potenciaFoco1Caracteristica;
    }
    public BluetoothGattCharacteristic getTimerFoco1Caracteristica() {
        return timerFoco1Caracteristica;
    }
    public BluetoothGattCharacteristic getPotenciaFoco2Caracteristica() {
        return potenciaFoco2Caracteristica;
    }
    public BluetoothGattCharacteristic getTimerFoco2Caracteristica() {
        return timerFoco2Caracteristica;
    }
    public BluetoothGattCharacteristic getPotenciaFoco3Caracteristica() {
        return potenciaFoco3Caracteristica;
    }
    public BluetoothGattCharacteristic getTimerFoco3Caracteristica() {
        return timerFoco3Caracteristica;
    }
    public ArrayList<BluetoothLE> getaDevices() {
        return aDevices;
    }
    public boolean isEscaneando() {   return escaneando;    }
    public void setEscaneando(boolean escaneando) {   this.escaneando = escaneando;    }
    public boolean isBle_conectado() {        return ble_conectado;    }
    public void setBle_conectado(boolean ble_conectado) {    this.ble_conectado = ble_conectado; }

    private Bluetooth(Activity _act){
        act = _act;
        BluetoothManager bluetoothManager = (BluetoothManager)
                act.getSystemService(Context.BLUETOOTH_SERVICE);
       bluetoothAdapter = bluetoothManager.getAdapter(); // BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null)
        {/* Continue with bluetooth setup.*/}
    }

    //Crea singleton Bluetooth
    public static Bluetooth getInstance(Activity _act){
        if(bluetooth==null){
            bluetooth = new Bluetooth(_act);
        }
        return bluetooth;
    }

    public Bluetooth ( Context context ){
        this.MainContext = context;
    }

    /**
     * IZ: Starts scanning for a limited period of time.
     * @param enable if true, it enables scanning. If false, it stops scanning
     */
    public void scanLeDevice(boolean enable) {
        Handler mHandler = new Handler();

        if (enable) {
            mScanning = true;

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            if(!FILTER_SERVICE.equals("")) {
                UUID[] filter  = new UUID[1];
                filter [0]     = UUID.fromString(FILTER_SERVICE);
                bluetoothAdapter.startLeScan(filter, mLeScanCallback);
            }else{
                bluetoothAdapter.startLeScan(mLeScanCallback);
            }

        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                    if(aDevices.size() > 0) {

                        boolean isNewItem = true;

                        for (int i = 0; i < aDevices.size(); i++) {
                            if (aDevices.get(i).getMacAddress().equals(device.getAddress())
                                    || device.getName() == null) {
                                isNewItem = false;
                            }
                        }

                        if(isNewItem) {
                            aDevices.add(new BluetoothLE(device.getName(),
                                    device.getAddress(), rssi, device));
                        }

                    }else{
                        aDevices.add(new BluetoothLE(device.getName(),
                                device.getAddress(), rssi, device));
                    }
                }
            });
        }
    };

    public void connect(BluetoothDevice device){
        if (mBluetoothGatt == null && !isConnected()) {
//            bleCallback = _bleCallback;
            mBluetoothGatt = device.connectGatt(act, false, mGattCallback);
        }
        try {
            Log.d("en connect","******Time sleep 3 seconds");
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Log.e("en connect","Error a la hora de sleep después de connect");
            e.printStackTrace();
        }

    }

    public void disconnect(){
        if (mBluetoothGatt != null && isConnected()) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }
    public boolean isReadyForScan(){

        return Permissions.checkPermisionStatus(act, Manifest.permission.BLUETOOTH)
                && Permissions.checkPermisionStatus(act, Manifest.permission.BLUETOOTH_ADMIN)
                && Permissions.checkPermisionStatus(act, Manifest.permission.ACCESS_COARSE_LOCATION)
                && Functions.getStatusGps(act);
    }

    private final BluetoothGattCallback mGattCallback;
    {
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
//                    Log.i("BluetoothLEHelper", "Attempting to start service discovery: "
//                            + mBluetoothGatt.discoverServices());
                    mConnectionState = STATE_CONNECTED;
                }

                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    mConnectionState = STATE_DISCONNECTED;
                }
                try{
                    //bleCallback.onBleConnectionStateChange(gatt, status, newState);

                }catch (Exception e){
                    Log.e("bleCallback.onBleCon...", "El error: " + e);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                bleCallback.onBleServiceDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                    characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
//                bleCallback.onBleWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                    characteristic, int status) {
//                bleCallback.onBleRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                    characteristic) {
//                bleCallback.onBleCharacteristicChange(gatt, characteristic);
            }

        };
    }

    public boolean isConnected(){
        return mConnectionState == STATE_CONNECTED;
    }

    //Escribe el valor de potencia en la característica
    public void escribirPotencia(int potencia, BluetoothGattCharacteristic caracteristica){
        byte value = (byte)potencia;
        if (potencia!=0) {
            caracteristica.setValue(new byte[]{value, 0});
            Bluetooth.getInstance(act).getBluetoothGatt().writeCharacteristic(caracteristica);
        }
        if (potencia==0){
            caracteristica.setValue(new byte[] {0, 0});
            Bluetooth.getInstance(act).getBluetoothGatt().writeCharacteristic(caracteristica);
        }
    }

    //Escribe el valor del tiempo en la característica
    public void escribirTiempo(int tiempo, BluetoothGattCharacteristic caracteristica){
        if (tiempo!=0){
            FuncionesGlobales.convertirHexadecimal(tiempo,0);
            String hex = FuncionesGlobales.hexadecimal;
            int lsb = Integer.parseInt(hex.substring(0, 2), 16);
            int msb = Integer.parseInt(hex.substring(2, 4), 16);
            caracteristica.setValue(new byte[] {(byte) msb, (byte) lsb});
            FuncionesGlobales.hexadecimal="";
            FuncionesGlobales.hexVal = new int[4];
            Bluetooth.getInstance(act).getBluetoothGatt().writeCharacteristic(caracteristica);
        }
        if (tiempo==0){
            caracteristica.setValue(new byte[] {0, 0});
            Bluetooth.getInstance(act).getBluetoothGatt().writeCharacteristic(caracteristica);
        }
    }

    public ArrayList<BluetoothLE> getListDevices(){
        return aDevices;
    }
    public long getScanPeriod(){
        return SCAN_PERIOD;
    }

}
