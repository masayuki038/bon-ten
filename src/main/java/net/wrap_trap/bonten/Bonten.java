package net.wrap_trap.bonten;

public class Bonten {

	public static final int TOP_LEVEL = 8;
	
	private static BontenConfig bontenConfig;

	public static synchronized void init() {
		bontenConfig = BontenConfig.loadConfig();
		Utils.ensureExpiry(); 
	}
	
	public static BontenConfig getConfig() {
		return bontenConfig;
	}
}
