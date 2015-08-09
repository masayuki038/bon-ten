package net.wrap_trap.bonten.rpc;

import net.wrap_trap.bonten.message.Message;

abstract public class Request {

	private Message messsage;

	public Request(Message messsage) {
		super();
		this.messsage = messsage;
	}

	public Message getMessage() {
		return messsage;
	}
}
