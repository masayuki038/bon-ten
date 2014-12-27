package net.wrap_trap.bonten;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

public class BontenConfig {

  private static final String BONTEN_CONFIG = "bonten.conf";

  private Config config;

  public static BontenConfig loadConfig() {
    final ClassLoader classLoader = BontenConfig.class.getClassLoader();
    return new BontenConfig(ConfigFactory.load(classLoader, BONTEN_CONFIG));
  }

  public BontenConfig(final Config config) {
    this.config = config;
  }

  public boolean getBoolean(final String path) {
    return this.config.getBoolean(path);
  }

  public int getInt(final String path) {
    return this.config.getInt(path);
  }

  public int getInt(final String path, final int ifUndef) {
    try {
      return getInt(path);
    } catch (final ConfigException.Missing ignore) {}
    return ifUndef;
  }

  public String getString(final String path) {
    return this.config.getString(path);
  }

  public String getString(final String path, final String ifUndef) {
    try {
      return getString(path);
    } catch (final ConfigException.Missing ignore) {}
    return ifUndef;
  }
}
