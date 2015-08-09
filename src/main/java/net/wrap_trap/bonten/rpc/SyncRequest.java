package net.wrap_trap.bonten.rpc;

import akka.actor.ActorRef;
import net.wrap_trap.bonten.message.Message;

public class SyncRequest extends Request {

  private ActorRef mref;
  
	public SyncRequest(Message messsage, ActorRef mref) {
		super(messsage);
		this.mref = mref;
	}

  public ActorRef getMref() {
    return mref;
  }
}
