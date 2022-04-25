package io.github.benrkia.redis.cmd;

import io.github.benrkia.redis.datasource.DataSource;
import io.github.benrkia.redis.exception.InvalidCmdArguments;

public class Get extends Cmd {
  private final DataSource dataSource;

  public Get(String... args) throws InvalidCmdArguments {
    super(args);
    dataSource = DataSource.instance();
  }

  @Override
  public String execute() {
    String value = dataSource.get(args[0]);
    return value == null ? protocol.nil() : protocol.bulkString(value);
  }

  @Override
  protected String[] ensureValidArgs(String... args)
      throws InvalidCmdArguments {
    if (args == null || args.length != 1)
      throw new InvalidCmdArguments("wrong number of arguments for 'get' command");
    return args;
  }

}
