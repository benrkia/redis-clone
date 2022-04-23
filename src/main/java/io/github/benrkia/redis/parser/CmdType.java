package io.github.benrkia.redis.parser;

import java.util.Objects;

enum CmdType {
  PING("PING");

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
}
