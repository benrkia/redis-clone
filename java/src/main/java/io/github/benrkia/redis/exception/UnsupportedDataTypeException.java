package io.github.benrkia.redis.exception;

public class UnsupportedDataTypeException extends RuntimeException {

  public UnsupportedDataTypeException() {
    super("Unsupported data type");
  }

  public UnsupportedDataTypeException(String msg) {
    super(msg);
  }
}
