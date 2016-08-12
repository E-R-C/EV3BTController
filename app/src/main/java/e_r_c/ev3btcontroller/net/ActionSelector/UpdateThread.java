package e_r_c.ev3btcontroller.net.ActionSelector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class UpdateThread extends Thread {
    private ArrayList<UpdateListener<ActionSelectorReply>> listeners = new ArrayList<>();
    private boolean quit = false;
    private DatagramSocket sock;
    private int expectedBytes;


    public UpdateThread(DatagramSocket sock, int expectedBytes) {
        this.sock = sock;
        this.expectedBytes = expectedBytes;
    }

    public void addListener(UpdateListener<ActionSelectorReply> listener) {
        listeners.add(listener);
    }
    public void quit(){
        quit = true;
    }

    @Override
    public void run() {
        try {
            while (!quit) {
                byte[] bytes = new byte[expectedBytes];
                DatagramPacket incoming = new DatagramPacket(bytes, bytes.length);
                sock.receive(incoming);
                for (byte b: bytes) {System.out.print(b + " ");}
                System.out.println();
                ActionSelectorReply reported = ActionSelectorReply.fromBytes(bytes);
                for (UpdateListener<ActionSelectorReply> listener : listeners) {
                    listener.report(reported);
                }
            }
        } catch (SocketException socky) {
            socky.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
