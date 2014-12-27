package net.wrap_trap.bonten;

public class Bonten {

  public static final int TOP_LEVEL = 8;

  public static final int SIZE_OF_ENTRY_TYPE = 1;
  public static final int SIZE_OF_KEYSIZE = 4;
  public static final int SIZE_OF_TIMESTAMP = 4;
  public static final int SIZE_OF_POS = 8;
  public static final int SIZE_OF_LEN = 4;

  public static final byte TAG_KV_DATA = (byte) 0x80;
  public static final byte TAG_DELETED = (byte) 0x81;
  public static final byte TAG_POSLEN = (byte) 0x82;
  public static final byte TAG_TRANSACT = (byte) 0x83;
  public static final byte TAG_KV_DATA2 = (byte) 0x84;
  public static final byte TAG_DELETED2 = (byte) 0x85;

  private static BontenConfig bontenConfig;

  public static synchronized void init() {
    bontenConfig = BontenConfig.loadConfig();
    Utils.ensureExpiry();
  }

  public static BontenConfig getConfig() {
    return bontenConfig;
  }
}
