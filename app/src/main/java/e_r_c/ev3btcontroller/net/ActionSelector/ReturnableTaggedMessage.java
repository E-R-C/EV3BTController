package e_r_c.ev3btcontroller.net.ActionSelector;

import java.net.InetAddress;

import e_r_c.ev3btcontroller.net.ActionSelector.TaggedMessage;


abstract public class ReturnableTaggedMessage extends TaggedMessage {
    private InetAddress sender;

    public ReturnableTaggedMessage() {sender = null;}

    abstract public boolean keepGoing();

    public InetAddress getSender() {return sender;}
    public void setSender(InetAddress sender) {this.sender = sender;}
    public boolean senderKnown() {return getSender() != null;}
}