package io.github.benrkia.redis.exception;

public class InvalidCmdArguments extends RESPError {
  private static final String PREFIX = "INVALID_CMD_ARGUMENTS";

  public InvalidCmdArguments() {
    super(PREFIX);
  }

  public InvalidCmdArguments(String msg) {
    super(PREFIX, msg);
  }
}