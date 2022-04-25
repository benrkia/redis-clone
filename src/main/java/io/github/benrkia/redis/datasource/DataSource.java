package io.github.benrkia.redis.datasource;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public final class DataSource {
  private final Map<String, DataHolder> store;
  private Queue<DataHolder> queue;

  private DataSource() {
    store = new ConcurrentHashMap<>();
    queue = new PriorityBlockingQueue<>();
  }

  private static class BillPughSingleton {
    private static final DataSource INSTANCE = new DataSource();
  }

  public static DataSource instance() {
    return BillPughSingleton.INSTANCE;
  }

  public void set(final String key, final String value) {
    Objects.requireNonNull(key);
    put(key, new DataHolder(key, value));
  }

  public void set(final String key, final String value, int expiryTime) {
    long expiresAt = now() + expiryTime;
    put(key, new DataHolder(key, value, expiresAt));
  }

  public String get(final String key) {
    Objects.requireNonNull(key);
    return getIfStillValid(store.get(key));
  }

  private void put(final String key, final DataHolder dataHolder) {
    long now = now();

    while (!queue.isEmpty()) {
      DataHolder d = queue.peek();
      if (!d.willExpiredBy(now))
        break;
      queue.poll();
      store.remove(d.key);
    }

    store.put(key, dataHolder);
    if (dataHolder.expiresAt != -1)
      queue.add(dataHolder);
  }

  private String getIfStillValid(final DataHolder dataHolder) {
    if (dataHolder == null)
      return null;
    if (!dataHolder.willExpiredBy(now()))
      return dataHolder.value;

    store.remove(dataHolder.key);
    return null;
  }

  private long now() {
    return System.currentTimeMillis();
  }

  private class DataHolder implements Comparable<DataHolder> {
    private final String key;
    private final String value;
    private final long expiresAt;

    DataHolder(String key, String value, long expiresAt) {
      this.key = key;
      this.value = value;
      this.expiresAt = expiresAt;
    }

    DataHolder(String key, String value) {
      this(key, value, -1);
    }

    boolean willExpiredBy(final long millis) {
      return expiresAt != -1 && expiresAt <= millis;
    }

    @Override
    public int hashCode() {
      return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof DataHolder) {
        DataHolder other = (DataHolder) obj;
        return key.equals(other.key);
      }
      return false;
    }

    @Override
    public int compareTo(DataHolder o) {
      return Long.compare(expiresAt, o.expiresAt);
    }
  }

}
