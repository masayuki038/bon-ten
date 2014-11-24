package net.wrap_trap.bonten;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

public class BontenConfig {

	private static final String BONTEN_CONFIG = "bonten.conf";
	
	private Config config;
	
	public static BontenConfig loadConfig() {
		ClassLoader classLoader = BontenConfig.class.getClassLoader();
		return new BontenConfig(ConfigFactory.load(classLoader, BONTEN_CONFIG));
	}
	
	public BontenConfig(Config config) {
		this.config = config;
	}
	
	public boolean getBoolean(String path) {
		return config.getBoolean(path);
	}

	public int getInt(String path) {
		return config.getInt(path);
	}
	
	public int getInt(String path, int ifUndef) {
		try {
			return getInt(path);
		} catch(ConfigException.Missing ignore) {}
		return ifUndef;
	}

	public String getString(String path) {
		return config.getString(path);
	}
	
	public String getString(String path, String ifUndef) {
		try {
			return getString(path);
		} catch(ConfigException.Missing ignore){}
		return ifUndef;
	}
}
