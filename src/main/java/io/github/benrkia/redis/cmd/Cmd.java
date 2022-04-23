package io.github.benrkia.redis.cmd;

import io.github.benrkia.redis.exception.InvalidCmdArguments;

public abstract class Cmd {
  protected final String[] args;

  protected Cmd(String... args) throws InvalidCmdArguments {
    this.args = ensureValidArgs(args);
  }

  public abstract String execute();

  protected abstract String[] ensureValidArgs(String... args) throws InvalidCmdArguments;
}
