package e_r_c.ev3btcontroller.net.ActionSelector;

import org.joda.time.LocalDateTime;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import e_r_c.ev3btcontroller.BSOCUtil.Move;
import e_r_c.ev3btcontroller.BSOCUtil.StampedStorage;


public class ActionSelectorReply extends TaggedMessage {

    public final static int MAX_STORED = 30;
    public final static int SIZE = 1 + MAX_STORED * (StampedStorage.DATE_TIME_BYTES + Integer.BYTES);

    private ArrayList<DateTimeInt> names;
    private byte tag;
    private boolean isPulse;
    private LocalDateTime pulseTime;
    private Move pulseMove;


    public ActionSelectorReply(byte tag) {
        this.tag = tag;
        names = new ArrayList<>();
    }
    public ActionSelectorReply(){
        isPulse = true;
    }
    public boolean isPulse(){
        return isPulse;
    }
    public void setIsPulse(boolean b){
        isPulse = b;
    }
    public void setPulseTime(LocalDateTime pulseTime){
        this.pulseTime = pulseTime;
    }
    public LocalDateTime getPulseTime(){
        return pulseTime;
    }
    public void setPulseMove(Move m){
        pulseMove = m;
    }
    public Move getPulseMove(){
        return pulseMove;
    }
    public void addName(LocalDateTime name, int suffix) {
        names.add(new DateTimeInt(name, suffix));
    }
    public ArrayList<DateTimeInt> getNames() {
        return names;
    }
    @Override
    public byte getTag() {
        return tag;
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer bytes = ByteBuffer.allocate(SIZE);
        bytes.put(tag);
        bytes.putInt(names.size());
        for (int i = 0; i < names.size(); i++) {
            StampedStorage.putInto(names.get(i).getTime(), bytes);
            bytes.putInt(names.get(i).getStamp());
        }
        return bytes.array();
    }

    public static ActionSelectorReply fromBytes(byte[] bytes) {
        ActionSelectorReply result;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte tag = buffer.get();
        if (tag == -2){ // This the code for the every cycle tick that updates the current move
            result = new ActionSelectorReply();
            result.setPulseMove(Move.values()[buffer.get()]);
            result.setIsPulse(false);
        } else if (tag == -1){
            result = new ActionSelectorReply();
            result.setPulseTime(StampedStorage.getFrom(buffer));
            Move moveFromPulse = Move.values()[buffer.get()];
            result.setPulseMove(moveFromPulse);
        } else {
            result = new ActionSelectorReply(tag);
            int numStored = buffer.getInt();
            for (int i = 0; i < numStored; i++) {
                result.addName(StampedStorage.getFrom(buffer), buffer.getInt());
            }
        }
        return result;
    }
}