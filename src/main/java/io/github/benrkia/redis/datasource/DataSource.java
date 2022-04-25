package io.github.benrkia.redis.datasource;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class DataSource {
  private final Map<String, String> store;

  private DataSource() {
    store = new ConcurrentHashMap<>();
  }

  private static class BillPughSingleton {
    private static final DataSource INSTANCE = new DataSource();
  }

  public static DataSource instance() {
    return BillPughSingleton.INSTANCE;
  }

  public void set(final String key, final String value) {
    Objects.requireNonNull(key);
    store.put(key, value);
  }

  public String get(final String key) {
    Objects.requireNonNull(key);
    return store.get(key);
  }

}
