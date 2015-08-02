package net.wrap_trap.bonten.rpc;

public class Reply {

	private Object message;

	public Reply(Object message) {
		super();
		this.message = message;
	}

	public Object getMessage() {
		return message;
	}
}
