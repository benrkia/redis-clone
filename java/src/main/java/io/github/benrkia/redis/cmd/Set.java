package io.github.benrkia.redis.cmd;

import io.github.benrkia.redis.datasource.DataSource;
import io.github.benrkia.redis.exception.InvalidCmdArguments;

public class Set extends Cmd {
  private final DataSource dataSource;
  private int px = -1;

  public Set(String... args) throws InvalidCmdArguments {
    super(args);
    initArgs();
    dataSource = DataSource.instance();
  }

  @Override
  public String execute() {
    try {
      if (px != -1)
        dataSource.set(args[0], args[1], px);
      else
        dataSource.set(args[0], args[1]);

      return protocol.simpleString("OK");
    } catch (Exception e) {
      return protocol.nil();
    }
  }

  @Override
  protected String[] ensureValidArgs(String... args)
      throws InvalidCmdArguments {
    if (args == null || (args.length != 2 && args.length != 4))
      throw new InvalidCmdArguments("wrong number of arguments for 'set' command");

    if (args.length == 4) {
      if (!"px".equalsIgnoreCase(args[2]))
        throw new InvalidCmdArguments("invalid argument '" + args[2] + "' for 'set' command");

      try {
        if (Integer.parseInt(args[3]) <= 0)
          throw new InvalidCmdArguments("invalid expire time in 'set' command");
      } catch (NumberFormatException e) {
        throw new InvalidCmdArguments("value is not an integer or out of range");
      }
    }

    return args;
  }

  private void initArgs() {
    if (args.length == 4)
      px = Integer.parseInt(args[3]);
  }

}
