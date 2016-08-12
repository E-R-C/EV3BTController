package e_r_c.ev3btcontroller.GUI;

import android.os.AsyncTask;

import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.util.ArrayList;

import e_r_c.ev3btcontroller.BSOCUtil.Mode;
import e_r_c.ev3btcontroller.BSOCUtil.Move;
import e_r_c.ev3btcontroller.net.ActionSelector.ActionSelectorCommand;
import e_r_c.ev3btcontroller.net.ActionSelector.ActionSelectorReply;
import e_r_c.ev3btcontroller.net.ActionSelector.DateTimeInt;
import e_r_c.ev3btcontroller.net.ActionSelector.UpdateListener;
import e_r_c.ev3btcontroller.net.ClientSender;
import e_r_c.ev3btcontroller.net.NetworkConstants;
import e_r_c.ev3btcontroller.sysUtil.WifiHandler;

public class ClientSenderWrapper {
    Move lastRobMove;
    LocalDateTime lastSentTime, lastReceivedTime;
    ClientSender clientSender;
    Mode current;
    byte currentMoveCount = 0;
    ArrayList<DateTimeInt> storedBSOCs;
    private int sentTime;

    public ClientSenderWrapper() {
        lastSentTime = LocalDateTime.now();
        lastReceivedTime = LocalDateTime.now();
        clientSender = new ClientSender(ActionSelectorReply.SIZE);
        current = Mode.STARTING;
        storedBSOCs = new ArrayList<>();
        clientSender.addUpdateListener(new UpdateListener<ActionSelectorReply>() {
            @Override
            public void report(ActionSelectorReply reply) {
                if (reply.isPulse()){
                    lastReceivedTime = reply.getPulseTime();
                    lastRobMove = reply.getPulseMove();
                    System.out.println("Got Pulse!");
                } else {
                    storedBSOCs.clear();
                    for (DateTimeInt d : reply.getNames()) {
                        storedBSOCs.add(d);
                    }
                }
            }
        });

    }
    public void addUpdateListener(UpdateListener<ActionSelectorReply> listener){
        clientSender.addUpdateListener(listener);
    }
    private class sender extends AsyncTask<ActionSelectorCommand, Void, Boolean>{
        @Override
        protected Boolean doInBackground(ActionSelectorCommand... msgs) {
            try {
                clientSender.send(msgs[0]);
                lastSentTime = LocalDateTime.now();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
    public void sendFirstMessage(int BSOC, int shrink){
        ActionSelectorCommand msg = new ActionSelectorCommand(Mode.STARTING, Move.STOP, LocalDateTime.now(), 0,
                currentMoveCount++, BSOC, shrink);
        send(msg);
    }
    public static boolean pingTest(String ipAddress){
        if (WifiHandler.pingTest(ipAddress)) {
            NetworkConstants.changeIP(ipAddress);
            return true;
        } else {
            return false;
        }
    }
    public void send(ActionSelectorCommand msg){
        new sender().execute(msg);
        sentTime = msg.getStamp().getMillisOfDay();

    }
    public int getSentTime(){
        return sentTime;
    }
    public void sendEV3ToReceiving(){
        ActionSelectorCommand msg = new ActionSelectorCommand(Mode.RETRIEVING,
                Move.STOP, LocalDateTime.now(), 0, currentMoveCount++, 0, 0);
        send(msg);
    }
    public void sendToLivePreview(){
        ActionSelectorCommand msg = new ActionSelectorCommand(Mode.LIVE_DEMO, Move.STOP, LocalDateTime.now(), 0,
                currentMoveCount++, 0, 0);
        send(msg);
    }
    public ArrayList<DateTimeInt> getStoredBSOCs(){
        return storedBSOCs;
    }
    public void sendBSOC(DateTimeInt BSOC){
        ActionSelectorCommand msg = new ActionSelectorCommand(Mode.APPLYING, Move.STOP,
                BSOC.getTime(),BSOC.getStamp(),currentMoveCount++, 0,0);
        send(msg);
    }

    public void sendLearnMove(String moveAsString){
        Move m = Move.valueOf(moveAsString.toUpperCase());
        ActionSelectorCommand msg = new ActionSelectorCommand(Mode.PULSE,m, LocalDateTime.now(), 0,
                currentMoveCount++, 0, 0);
        send(msg);
    }
    public ClientSender getClientSender(){
        return clientSender;
    }
    public void quit(){
        clientSender.quit();
    }


}
