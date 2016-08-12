package e_r_c.ev3btcontroller.net;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import e_r_c.ev3btcontroller.net.ActionSelector.ActionSelectorCommand;
import e_r_c.ev3btcontroller.net.ActionSelector.ActionSelectorReply;
import e_r_c.ev3btcontroller.net.ActionSelector.UpdateListener;
import e_r_c.ev3btcontroller.net.ActionSelector.UpdateThread;

public class ClientSender implements UpdateListener<ActionSelectorReply> {
    private DatagramSocket sock;
    private UpdateThread updater;
    private byte pendingTag, receivedTag;
    private ActionSelectorReply lastReply;

    public ClientSender(int expectedBytes){
        try {
            sock = new DatagramSocket(NetworkConstants.defaultPort);
            System.out.println(NetworkConstants.defaultPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        updater = new UpdateThread(sock, expectedBytes);
        updater.addListener(this);
        updater.start();
        pendingTag = receivedTag = 0;
    }

    public void addUpdateListener(UpdateListener<ActionSelectorReply> listener) {
        updater.addListener(listener);
    }

    public void send(ActionSelectorCommand msg) throws IOException {
        pendingTag = msg.getTag();
        receivedTag = (byte) (pendingTag - 1);
        msg.ship(sock);
    }

    public boolean waitingForReply() {return pendingTag != receivedTag;}

    public ActionSelectorReply getLastReply() {return lastReply;}
    public void quit(){
        updater.quit();
        sock.close();
    }
    @Override
    public void report(ActionSelectorReply msg) {
        receivedTag = msg.getTag();
        lastReply = msg;
        if (receivedTag == -2){
            System.out.println("Message request received");
        }
    }
}
