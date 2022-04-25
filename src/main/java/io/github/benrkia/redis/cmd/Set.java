package io.github.benrkia.redis.cmd;

import io.github.benrkia.redis.datasource.DataSource;
import io.github.benrkia.redis.exception.InvalidCmdArguments;

public class Set extends Cmd {
  private final DataSource dataSource;

  public Set(String... args) throws InvalidCmdArguments {
    super(args);
    dataSource = DataSource.instance();
  }

  @Override
  public String execute() {
    dataSource.set(args[0], args[1]);
    return protocol.simpleString("OK");
  }

  @Override
  protected String[] ensureValidArgs(String... args)
      throws InvalidCmdArguments {
    if (args == null || args.length != 2)
      throw new InvalidCmdArguments("wrong number of arguments for 'set' command");
    return args;
  }

}
