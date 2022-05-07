package io.github.benrkia.redis.parser;

import java.util.Objects;
import java.util.Optional;

import io.github.benrkia.redis.cmd.Cmd;
import io.github.benrkia.redis.cmd.Echo;
import io.github.benrkia.redis.cmd.Get;
import io.github.benrkia.redis.cmd.Ping;
import io.github.benrkia.redis.cmd.Set;
import io.github.benrkia.redis.exception.InvalidCmdArguments;

enum CmdType {
  PING("PING"), ECHO("ECHO"), SET("SET"), GET("GET");

  private final String key;

  CmdType(String key) {
    this.key = key;
  }

  public static CmdType from(final String key) {
    Objects.requireNonNull(key);
    for (CmdType type : CmdType.values()) {
      if (type.key.equalsIgnoreCase(key)) {
        return type;
      }
    }
    return null;
  }

  public static Optional<Cmd> from(final CmdType type, final String[] args)
      throws InvalidCmdArguments {

    Cmd cmd;
    if (type == CmdType.PING)
      cmd = new Ping(args);
    else if (type == CmdType.ECHO)
      cmd = new Echo(args);
    else if (type == CmdType.GET)
      cmd = new Get(args);
    else if (type == CmdType.SET)
      cmd = new Set(args);
    else
      cmd = null;

    return Optional.ofNullable(cmd);
  }
}
