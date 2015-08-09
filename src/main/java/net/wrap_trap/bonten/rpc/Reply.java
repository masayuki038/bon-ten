package net.wrap_trap.bonten.rpc;

import akka.actor.ActorRef;
import net.wrap_trap.bonten.message.Message;

public class Reply {

	private Message message;
	private ActorRef mref;

	public Reply(Message message, ActorRef mref) {
		super();
		this.message = message;
		this.mref = mref;
	}

	public Message getMessage() {
		return message;
	}

  public ActorRef getMref() {
    return mref;
  }
}
