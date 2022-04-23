package io.github.benrkia.redis.cmd;

import io.github.benrkia.redis.exception.InvalidCmdArguments;
import io.github.benrkia.redis.protocol.RESPProtocol;

public class Ping extends Cmd {
  private final RESPProtocol protocol;

  public Ping(String... args) throws InvalidCmdArguments {
    super(args);
    this.protocol = new RESPProtocol();
  }

  @Override
  public String execute() {
    String result = args.length == 1 ? args[0] : "PONG";
    return protocol.bulkString(result);
  }

  @Override
  protected String[] ensureValidArgs(String... args)
      throws InvalidCmdArguments {
    if (args == null || args.length > 1)
      throw new InvalidCmdArguments("Excpected at most 1 argument");
    return args;
  }

}
