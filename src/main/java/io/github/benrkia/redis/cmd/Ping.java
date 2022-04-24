package io.github.benrkia.redis.cmd;

import io.github.benrkia.redis.exception.InvalidCmdArguments;

public class Ping extends Cmd {

  public Ping(String... args) throws InvalidCmdArguments {
    super(args);
  }

  @Override
  public String execute() {
    return args.length == 1
        ? protocol.bulkString(args[0])
        : protocol.simpleString("PONG");
  }

  @Override
  protected String[] ensureValidArgs(String... args)
      throws InvalidCmdArguments {
    if (args == null || args.length > 1)
      throw new InvalidCmdArguments("wrong number of arguments for 'ping' command");
    return args;
  }

}
