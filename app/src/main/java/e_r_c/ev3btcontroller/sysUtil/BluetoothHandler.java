package e_r_c.ev3btcontroller.sysUtil;

import android.bluetooth.BluetoothAdapter;

public class BluetoothHandler {
    BluetoothAdapter bluetoothAdapter;
    boolean connected;
    String ipAdress;
    public BluetoothHandler() {
        retrieveAdapter();
    }

    public void retrieveAdapter(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    public boolean isConnected(){
        return connected;
    }

    public boolean isEnabled(){
        boolean result = true;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            result = false;
        }
        return result;
    }

    public boolean connectBT(String ipAddress) throws IllegalArgumentException{
        connected = true;
        return connected;
    }

    public void quit(){

    }

}
