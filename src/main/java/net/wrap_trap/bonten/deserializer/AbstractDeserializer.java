package net.wrap_trap.bonten.deserializer;

public abstract class AbstractDeserializer implements Deserializer {

	private boolean readTimestamp;

	public AbstractDeserializer(boolean readTimestamp) {
		super();
		this.readTimestamp = readTimestamp;
	}

	protected boolean isReadTimestamp() {
		return readTimestamp;
	}
}
