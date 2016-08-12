package e_r_c.ev3btcontroller.sysUtil;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import e_r_c.ev3btcontroller.BSOCUtil.Move;
import e_r_c.ev3btcontroller.net.NetworkConstants;

public class WifiHandler {
    String ipAddress;
    boolean connected;
    DatagramSocket datagramSocket;
    InetAddress address;

    public WifiHandler() {
        ipAddress = "10.0.1.1";
        if (pingTest(ipAddress)){
            connected = true;
            System.out.println("Wifi Connected");
        } else {
            connected = false;
            System.out.println("Wifi Not connected");
        }
    }

    public static boolean pingTest(String ipAddress){
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + ipAddress);
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }

    public void setIpAddress(String newipAddress){
        ipAddress = newipAddress;

    }
    public String getIpAddress(){
        return ipAddress;
    }

}
