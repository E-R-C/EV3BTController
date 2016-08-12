package e_r_c.ev3btcontroller.net.ActionSelector;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import e_r_c.ev3btcontroller.net.NetworkConstants;

public abstract class TaggedMessage {
    abstract byte getTag();
    abstract byte[] toBytes();
    String ipaddress = NetworkConstants.defaultIP;

    public void ship(DatagramSocket sock) throws IOException {
        ship(sock, InetAddress.getByName(ipaddress));
    }

    public void ship(DatagramSocket sock, InetAddress target) throws IOException {
        byte[] bytes = toBytes();
        DatagramPacket info = new DatagramPacket(bytes, bytes.length, target, NetworkConstants.defaultPort);
        sock.send(info);
    }
}

