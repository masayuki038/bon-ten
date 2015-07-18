package net.wrap_trap.bonten.message;

public class Lookup extends Message {

  private byte[] key;

  public Lookup(byte[] key) {
    super();
    this.key = key;
  }

  public byte[] getKey() {
    return key;
  }
}
