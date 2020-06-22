package com.stirling.senpino;

import android.bluetooth.BluetoothGattCharacteristic;

//import com.example.tiflotecnica_tresfocos.Singleton.Cocina;


public class FuncionesGlobales {

    static BluetoothGattCharacteristic caracteristica;
    public static int[] hexVal = new int[4];
    public static String hexadecimal="";
    public static boolean read = false;
    public static boolean write = false;

    //Lectura de valores de las características
    public static int convertirValorLectura(BluetoothGattCharacteristic characteristic){
        byte[] valores;
        int values, aux = 0, aux1;

        valores = characteristic.getValue();

        if ((valores[0]==0 && valores[1]==0)||valores==null){
            aux=0x00;
            aux1=0x00;
        }
        else{
            aux = FuncionesGlobales.unsignedByteToInt(valores[0]);
            aux1 = FuncionesGlobales.unsignedByteToInt(valores[1]);
        }
        values = aux1<<8 | aux;

        return values;
    }

    //Leer valores de las características e inicializar los valores de la cocina
    public static void inicializarCocina(){
       /* caracteristica = Bluetooth.getInstance().getPotenciaFoco1Caracteristica();
        read=false;
        Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(caracteristica);
        while (!read){}
        read=false;*/
//        Cocina.getInstance().getFocos().get(Cocina.FOCO_UNO).setPotencia(FuncionesGlobales.convertirValorLectura(caracteristica));

       /* caracteristica = Bluetooth.getInstance().getTimerFoco1Caracteristica();
        read=false;

        Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(caracteristica);
        while (!read){}
        read=false;*/
//        Cocina.getInstance().getFocos().get(Cocina.FOCO_UNO).setTiempo(FuncionesGlobales.convertirValorLectura(caracteristica));

        /*caracteristica = Bluetooth.getInstance().getPotenciaFoco2Caracteristica();
        read=false;
        Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(caracteristica);
        while (!read){}
        read=false;*/
//        Cocina.getInstance().getFocos().get(Cocina.FOCO_DOS).setPotencia(FuncionesGlobales.convertirValorLectura(caracteristica));

        /*caracteristica = Bluetooth.getInstance().getTimerFoco2Caracteristica();
        read=false;
        Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(caracteristica);
        while (!read){}
        read=false;*/
//        Cocina.getInstance().getFocos().get(Cocina.FOCO_DOS).setTiempo(FuncionesGlobales.convertirValorLectura(caracteristica));

        /*
        caracteristica = Bluetooth.getInstance().getPotenciaFoco3Caracteristica();
        read=false;
        Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(caracteristica);
        while (!read){}
        read=false;
        Cocina.getInstance().getFocos().get(Cocina.FOCO_TRES).setPotencia(FuncionesGlobales.convertirValorLectura(caracteristica));
        caracteristica = Bluetooth.getInstance().getTimerFoco3Caracteristica();
        read=false;
        Bluetooth.getInstance().getBluetoothGatt().readCharacteristic(caracteristica);
        while (!read){}
        read=false;
        Cocina.getInstance().getFocos().get(Cocina.FOCO_TRES).setTiempo(FuncionesGlobales.convertirValorLectura(caracteristica));

        */

    }
    public static int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    //Convertir int a hexadecimal
    public static void convertirHexadecimal(int value, int i){

        int cociente=0;
        int resto=0;
        int suma=0;
        if(value>16){
            hexVal[i]=(value%16);
            i++;

            cociente = (value/16);

            if(cociente>16){
                convertirHexadecimal(cociente, i);
            }
            if(cociente<=16){
                resto=(cociente%16);
                hexVal[i]=resto;
                i++;
                valorHexadecimal(hexVal);
            }
        }
    }
    //Convierte los valores convertidos a hexadecimal en los caracteres hexadecimales
    public static void valorHexadecimal(int[] hexVal) {
        String value="";
        System.out.println("Longitud: "+hexVal.length);
        for(int i = 3;i>=0;i--){
            switch(hexVal[i]){
                case 0:
                    value="0";
                    break;
                case 1:
                    value="1";
                    break;
                case 2:
                    value="2";
                    break;
                case 3:
                    value="3";
                    break;
                case 4:
                    value="4";
                    break;
                case 5:
                    value="5";
                    break;
                case 6:
                    value="6";
                    break;
                case 7:
                    value="7";
                    break;
                case 8:
                    value="8";
                    break;
                case 9:
                    value="9";
                    break;
                case 10:
                    value="A";
                    break;
                case 11:
                    value="B";
                    break;
                case 12:
                    value="C";
                    break;
                case 13:
                    value="D";
                    break;
                case 14:
                    value="E";
                    break;
                case 15:
                    value="F";
                    break;
            }
            hexadecimal=hexadecimal+value;
        }
        byte[] bytes = hexadecimal.getBytes();
    }

    //Apgado de encimera. Resetea todas las características a 0
    public static void apagarEncimera() {
        /*write=false;
        Bluetooth.getInstance().escribirPotencia(0, Bluetooth.getInstance().getPotenciaFoco1Caracteristica());
        while (!write){}
        write=false;
        Bluetooth.getInstance().escribirPotencia(0, Bluetooth.getInstance().getPotenciaFoco2Caracteristica());
        while (!write){}
        write=false;*/

        /*
        Bluetooth.getInstance().escribirPotencia(0, Bluetooth.getInstance().getPotenciaFoco3Caracteristica());
        while (!write){}
        write=false;
        Bluetooth.getInstance().escribirPotencia(0, Bluetooth.getInstance().getPotenciaFoco4Caracteristica());
        while (!write){}
        write=false;
        */

        /*Bluetooth.getInstance().escribirTiempo(0, Bluetooth.getInstance().getTimerFoco1Caracteristica());
        while (!write){}
        write=false;
        Bluetooth.getInstance().escribirTiempo(0, Bluetooth.getInstance().getTimerFoco2Caracteristica());
        while (!write){}
        write=false;*/

        /*Bluetooth.getInstance().escribirTiempo(0, Bluetooth.getInstance().getTimerFoco3Caracteristica());
        */

    }
}
