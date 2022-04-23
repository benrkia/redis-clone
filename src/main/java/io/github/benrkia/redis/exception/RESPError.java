package io.github.benrkia.redis.exception;

public class RESPError extends Exception {

  public RESPError(String prefix) {
    super(prefix);
  }

  public RESPError(String prefix, String msg) {
    super(String.format("%s %s", prefix, msg));
  }

}