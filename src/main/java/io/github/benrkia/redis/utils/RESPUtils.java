package io.github.benrkia.redis.utils;

import java.nio.charset.StandardCharsets;

import io.github.benrkia.redis.exception.UnsupportedDataTypeException;

public interface RESPUtils {
  static void ensureValidSimpleString(final String s) {
    ensureNotNull(s);
    for (byte b : toBytes(s)) {
      if (b == Constants.CR || b == Constants.LF) {
        throw new UnsupportedDataTypeException();
      }
    }
  }

  static void ensureNotNull(final Object o) {
    if (o == null)
      throw new UnsupportedDataTypeException();
  }

  static char toChar(final byte b) {
    return (char) (b & 0xFF);
  }

  static byte[] toBytes(final String str) {
    return str.getBytes(StandardCharsets.UTF_8);
  }

  static String toString(final byte[] bytes) {
    return toString(bytes, 0, bytes.length);
  }

  static String toString(final byte[] bytes, final int offset, final int length) {
    return new String(bytes, offset, length, StandardCharsets.UTF_8);
  }
}
