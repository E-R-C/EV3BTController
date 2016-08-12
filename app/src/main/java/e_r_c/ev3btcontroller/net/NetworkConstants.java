package e_r_c.ev3btcontroller.net;

public class NetworkConstants {
    public static String defaultIP = "10.0.1.1";
    public static final int defaultPort = 8001;
    public static void changeIP(String ip){
        defaultIP = ip;
    }
}
