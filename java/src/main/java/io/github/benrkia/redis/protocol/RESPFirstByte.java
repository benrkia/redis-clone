package io.github.benrkia.redis.protocol;

import io.github.benrkia.redis.utils.RESPUtils;

public enum RESPFirstByte {
  SIMPLE_STRING((byte) 0x2b), // +
  ERROR((byte) 0x2d), // -
  INTEGER((byte) 0x3a), // :
  BULK_STRING((byte) 0x24), // $
  ARRAY((byte) 0x2a); // *

  private final byte charPoint;

  RESPFirstByte(byte charPoint) {
    this.charPoint = charPoint;
  }

  public byte getCharPoint() {
    return charPoint;
  }

  public char getCharValue() {
    return RESPUtils.toChar(charPoint);
  }
}