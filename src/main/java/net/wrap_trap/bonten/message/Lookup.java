package net.wrap_trap.bonten.message;

import java.util.function.Function;

public class Lookup extends Message {

  private byte[] key;
  private Function<byte[], Void> callback;

  public Lookup(byte[] key) {
    this(key, null);
  }

  public Lookup(byte[] key, Function<byte[], Void> callback) {
    super();
    this.key = key;
    this.callback = callback;
  }

  public byte[] getKey() {
    return key;
  }

  public Function<byte[], Void> getCallback() {
    return callback;
  }
  
  public boolean hasCallback() {
    return (this.callback != null);
  }
}
