package io.github.benrkia.redis.cmd;

import io.github.benrkia.redis.exception.InvalidCmdArguments;

public class Echo extends Cmd {

  public Echo(String... args) throws InvalidCmdArguments {
    super(args);
  }

  @Override
  public String execute() {
    return protocol.bulkString(args[0]);
  }

  @Override
  protected String[] ensureValidArgs(String... args)
      throws InvalidCmdArguments {
    if (args == null || args.length != 1)
      throw new InvalidCmdArguments("wrong number of arguments for 'echo' command");
    return args;
  }

}
