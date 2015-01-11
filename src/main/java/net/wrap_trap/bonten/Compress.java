package net.wrap_trap.bonten;

public enum Compress {
  none((byte)0x00);
  
  private byte method;
  
  Compress(byte b) {
    this.method = b;
  }
  
  byte getMethod() {
    return this.method;
  }
  
  byte[] compress(byte[] plain) {
    byte[] body;
    
    switch(method) {
    case (byte)0x00:
      body = plain;
      break;
    default:
      throw new IllegalArgumentException("Unsupported compress type: " + method);
    }
    
    byte[] packed = new byte[body.length + 1];
    packed[0] = Compress.none.getMethod();
    System.arraycopy(plain, 0, packed, 1, body.length);
    return packed;
  }
  
  static byte[] uncompress(byte[] packed) {
    byte compressedBy = packed[0];
    byte[] body = new byte[packed.length - 1];
    System.arraycopy(packed, 1, body, 0, packed.length - 1);
    switch(compressedBy) {
    case (byte)0x00:
      return body;
    default:
      throw new IllegalArgumentException("Unsupported compress type: " + compressedBy);
    }
  }
}
