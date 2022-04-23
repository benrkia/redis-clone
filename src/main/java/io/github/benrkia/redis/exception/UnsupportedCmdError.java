package io.github.benrkia.redis.exception;

public class UnsupportedCmdError extends RESPError {
  private static final String PREFIX = "UNSUPPORTED_COMMAND";

  public UnsupportedCmdError() {
    super(PREFIX);
  }

  public UnsupportedCmdError(String msg) {
    super(PREFIX, msg);
  }
}