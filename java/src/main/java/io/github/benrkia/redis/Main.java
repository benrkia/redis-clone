package io.github.benrkia.redis;

import java.io.IOException;
import io.github.benrkia.redis.server.RedisServer;

public class Main {

  public static void main(String[] args) {
    try (RedisServer server = new RedisServer()) {
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
