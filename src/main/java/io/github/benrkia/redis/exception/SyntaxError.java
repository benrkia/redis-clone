package io.github.benrkia.redis.exception;

public class SyntaxError extends RESPError {
  private static final String PREFIX = "SYNTAX_ERROR";

  public SyntaxError() {
    super(PREFIX);
  }

  public SyntaxError(String msg) {
    super(PREFIX, msg);
  }
}