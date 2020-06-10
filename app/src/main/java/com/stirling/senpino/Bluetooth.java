package com.stirling.senpino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import com.example.tiflotecnica_tresfocos.Globales.BLE_UUID;
import com.stirling.senpino.FuncionesGlobales;
import com.stirling.senpino.Models.BluetoothLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;


public class Bluetooth {

    public boolean ble_conectado = false;
    public boolean escaneando = false; //IZ
    private static Bluetooth bluetooth;

    private String connectoToAddress;

    private ArrayList<BluetoothLE> aDevices = new ArrayList<>();

  //  public BLE_UUID uuids = new BLE_UUID();

    BluetoothDevice bluetoothDevice;
    BluetoothLeScanner btScanner;

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    BluetoothAdapter bluetoothAdapter;

    BluetoothGatt bluetoothGatt;

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

    BluetoothGattCharacteristic CaracterísticaPeso;
    BluetoothGattCharacteristic CaracterísticaFecha;
    BluetoothGattCharacteristic CaracterísticaUsuario;
    Context contexto;

    //Getters y setters
    public BluetoothGattCharacteristic getCaracterísticaPeso() {
        return CaracterísticaPeso;
    }
    public void setCaracterísticaPeso(BluetoothGattCharacteristic característicaPeso) {
        CaracterísticaPeso = característicaPeso;    }
    public BluetoothGattCharacteristic getCaracterísticaFecha() {
        return CaracterísticaFecha;
    }
    public void setCaracterísticaFecha(BluetoothGattCharacteristic característicaFecha) {
        CaracterísticaFecha = característicaFecha;    }
    public BluetoothGattCharacteristic getCaracterísticaUsuario() {
        return CaracterísticaUsuario;
    }
    public void setCaracterísticaUsuario(BluetoothGattCharacteristic característicaUsuario) {
        CaracterísticaUsuario = característicaUsuario;    }
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
        return bluetoothGatt;
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

    private Bluetooth(){
       bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null)
        {/* Continue with bluetooth setup.*/}
    }

    //Crea singleton Bluetooth
    public static Bluetooth getInstance(){
        if(bluetooth==null){
            bluetooth = new Bluetooth();
        }
        return bluetooth;
    }

    //Busqueda de dispositivos Bluetooth
    public void findDevices(Context context) {
        if (bluetoothDevice==null){
            bluetoothAdapter.startDiscovery();
            context.registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }

    }

    //IZ: Buscamos dispositivos y los añadimos a un arrayList
    public void escanearBt(List<ScanFilter> filters){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

        if (scanner != null) {
            //Filtros de búsqueda por parámetro

            //Opciones de escaneos
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED) //balanceado: escanea durante 2 segs y pausa de 3segs
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                    .setReportDelay(0L)
                    .build();

           // scanner.startScan(filters, scanSettings, scanCallback);

            Log.d(TAG, "scan started");
        }  else {
            Log.e(TAG, "could not get scanner object");
        }
    }
    // IZ: Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            aDevices.add(new BluetoothLE( result.getDevice().getName(),
                    result.getDevice().getAddress(),result.getRssi(), result.getDevice()));
        }
        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "BT scan error with code: " + errorCode);
        }
    };

    /**
     * Starts scanning for bt devices
     * */
    public void startScanning() {
        System.out.println("start scanning");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    /**
     * Stops scanning
     * */
    public void stopScanning() {
        System.out.println("stopping scanning");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    //Recibe los dispositivos Bluetooth encontrados e intenta la conexión si es el que se busca
    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address, name;
            BluetoothDevice remoteDevice;
            remoteDevice = bluetoothAdapter.getRemoteDevice("00:A0:50:02:D6:8E"); //getRemoteDevice funciona con la direccion MAC del dispositivo
            name = remoteDevice.getName();
            address = remoteDevice.getAddress();
            contexto = context;
            if(address!=null){
                if(remoteDevice.getAddress().equals("00:A0:50:02:D6:8E") && name.equals( "Tiflo")){
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothDevice=remoteDevice;

                    bluetoothCallback = new BluetoothGattCallback() {
                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                            super.onServicesDiscovered(gatt, status);
                        }
                    };
                    connectGatt("00:A0:50:02:D6:8E", context);//Se conecta mediante direccion MAC del dispositivo
                }
            }
        }
    };

    //Realiza la conexion GATT (Generic attribute profile) -> se encarga del intercambio de los datos mediante características y servicios
    public boolean connectGatt(String address, Context context) {
        if (bluetoothAdapter == null || address == null) {
            return false;
        }
        if (bluetoothGatt != null) {
            if (bluetoothGatt.connect()) {
                Toast.makeText(context, "Conexión BLE establecida", Toast.LENGTH_SHORT).show();
                ble_conectado = true;
                return true;
            } else {
                Toast.makeText(context, "Error en la conexión BLE", Toast.LENGTH_SHORT).show();
                ble_conectado = false;
                return false;
            }
        }
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        bluetoothGatt = device.connectGatt(context, true, mGattCallback);
        return bluetoothGatt.connect();
    }



    //Controla los eventos Bluetooth
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //bluetooth is connected so discover services
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //Bluetooth is disconnected
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
            }
            else if (status == BluetoothGatt.GATT_FAILURE) {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
            }
        }

        //Guarda los servicios y características descubiertos en variables
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // services are discovered
                ble_conectado=true;

//                serviceTiflotecnica = gatt.getService(uuids.TIFLOTECNICA_UUID);
//                estadoCocinaCaracteristica = serviceTiflotecnica.getCharacteristic(uuids.ESTADO_UUID);
//                serviceFoco1 = gatt.getService(uuids.FOCO_UNO_UUID);
//                potenciaFoco1Caracteristica = serviceFoco1.getCharacteristic(uuids.POTENCIA_UNO_UUID);
//                /*BluetoothGattDescriptor descriptor = potenciaFoco1Caracteristica.getDescriptor(uuids.CHARACTERISTIC_DESCRIPTOR);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);*/
//                //gatt.setCharacteristicNotification(potenciaFoco1Caracteristica, true);
//                timerFoco1Caracteristica = serviceFoco1.getCharacteristic(uuids.TIMER_UNO_UUID);
//                /*descriptor = timerFoco1Caracteristica.getDescriptor(uuids.CHARACTERISTIC_DESCRIPTOR);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);*/
//                //gatt.setCharacteristicNotification(timerFoco1Caracteristica, true);
//
//                serviceFoco2 = gatt.getService(uuids.FOCO_DOS_UUID);
//                potenciaFoco2Caracteristica = serviceFoco2.getCharacteristic(uuids.POTENCIA_DOS_UUID);
//                /*descriptor = potenciaFoco2Caracteristica.getDescriptor(uuids.CHARACTERISTIC_DESCRIPTOR);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);*/
//                //gatt.setCharacteristicNotification(potenciaFoco2Caracteristica, true);
//                timerFoco2Caracteristica = serviceFoco2.getCharacteristic(uuids.TIMER_DOS_UUID);
//                /*descriptor = timerFoco2Caracteristica.getDescriptor(uuids.CHARACTERISTIC_DESCRIPTOR);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);*/
//                //gatt.setCharacteristicNotification(timerFoco2Caracteristica, true);
//
//                serviceFoco3 = gatt.getService(uuids.FOCO_TRES_UUID);
//                potenciaFoco3Caracteristica = serviceFoco3.getCharacteristic(uuids.POTENCIA_TRES_UUID);
//                /*descriptor = potenciaFoco3Caracteristica.getDescriptor(uuids.CHARACTERISTIC_DESCRIPTOR);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);*/
//                //gatt.setCharacteristicNotification(potenciaFoco3Caracteristica, true);
//                timerFoco3Caracteristica = serviceFoco3.getCharacteristic(uuids.TIMER_TRES_UUID);
//                /*descriptor = timerFoco3Caracteristica.getDescriptor(uuids.CHARACTERISTIC_DESCRIPTOR);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);*/
//                //gatt.setCharacteristicNotification(timerFoco3Caracteristica, true);

            }
            if (status!=BluetoothGatt.GATT_SUCCESS){
                bluetoothGatt.disconnect();
                Toast.makeText(contexto, "ERROR DE CONEXIÓN", Toast.LENGTH_SHORT).show();
                System.exit(0);
                return;
            }
        }
/*
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
            BluetoothGattCharacteristic characteristic = gatt.getService(HEART_RATE_SERVICE_UUID).getCharacteristic(HEART_RATE_CONTROL_POINT_CHAR_UUID);
            characteristic.setValue(new byte[]{1, 1});
            gatt.writeCharacteristic(characteristic);
        }
*/
        //Recibe un evento cuando se lee una característica
        @Override
        public synchronized void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                FuncionesGlobales.read=true;
            }
        }

        //Recibe un evento cuando se modifica una característica
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
          /*  if (characteristic.getUuid()==potenciaFoco1Caracteristica.getUuid()){
                Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(characteristic);
                Cocina.getInstance().getFocos().get(Cocina.FOCO_UNO).setPotencia(FuncionesGlobales.convertirValorLectura(characteristic));

                //NotificationCenter.default.post(name: Notification.Name(rawValue: "updatePantallaFocoUno"), object: nil)
            }
            if (characteristic.getUuid()==potenciaFoco2Caracteristica.getUuid()){
                Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(characteristic);
                Cocina.getInstance().getFocos().get(Cocina.FOCO_DOS).setPotencia(FuncionesGlobales.convertirValorLectura(characteristic));
            }*/
            /*if (characteristic.getUuid()==potenciaFoco3Caracteristica.getUuid()){
                Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(characteristic);
                Cocina.getInstance().getFocos().get(2).setPotencia(FuncionesGlobales.convertirValorLectura(characteristic));
            }
            if (characteristic.getUuid()==potenciaFoco4Caracteristica.getUuid()){
                Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(characteristic);
                Cocina.getInstance().getFocos().get(3).setPotencia(FuncionesGlobales.convertirValorLectura(characteristic));
            }*/
         /*   if (characteristic.getUuid()==timerFoco1Caracteristica.getUuid()){
                Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(characteristic);
                Cocina.getInstance().getFocos().get(Cocina.FOCO_UNO).setPotencia(FuncionesGlobales.convertirValorLectura(characteristic));
            }
            if (characteristic.getUuid()==timerFoco2Caracteristica.getUuid()){
                Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(characteristic);
                Cocina.getInstance().getFocos().get(Cocina.FOCO_DOS).setPotencia(FuncionesGlobales.convertirValorLectura(characteristic));
            }*/
            /*if (characteristic.getUuid()==timerFoco3Caracteristica.getUuid()){
                Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(characteristic);
                Cocina.getInstance().getFocos().get(2).setPotencia(FuncionesGlobales.convertirValorLectura(characteristic));
            }
            if (characteristic.getUuid()==timerFoco4Caracteristica.getUuid()){
                Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(characteristic);
                Cocina.getInstance().getFocos().get(3).setPotencia(FuncionesGlobales.convertirValorLectura(characteristic));
            }*/

            //NotificationCenter.default.post(name: Notification.Name(rawValue: "updatePantalla"), object: nil)
            Intent intent = new Intent();
            intent.setAction("updatePantalla");
            contexto.sendBroadcast(intent);
        }

        //Recibe un evento cuando se escribe una característica
        @Override
        public synchronized void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                FuncionesGlobales.write = true;
            }
        }

    };

    //Escribe el valor de potencia en la característica
    public void escribirPotencia(int potencia, BluetoothGattCharacteristic caracteristica){
        byte value = (byte)potencia;
        if (potencia!=0) {
            caracteristica.setValue(new byte[]{value, 0});
            Bluetooth.getInstance().getBluetoothGatt().writeCharacteristic(caracteristica);
        }
        if (potencia==0){
            caracteristica.setValue(new byte[] {0, 0});
            Bluetooth.getInstance().getBluetoothGatt().writeCharacteristic(caracteristica);
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
            Bluetooth.getInstance().getBluetoothGatt().writeCharacteristic(caracteristica);
        }
        if (tiempo==0){
            caracteristica.setValue(new byte[] {0, 0});
            Bluetooth.getInstance().getBluetoothGatt().writeCharacteristic(caracteristica);
        }
    }
}
