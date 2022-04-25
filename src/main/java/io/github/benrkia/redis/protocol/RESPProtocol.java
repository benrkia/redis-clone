package io.github.benrkia.redis.protocol;

import static io.github.benrkia.redis.protocol.RESPFirstByte.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.benrkia.redis.exception.UnsupportedDataTypeException;
import io.github.benrkia.redis.exception.RESPError;
import io.github.benrkia.redis.utils.RESPUtils;

public final class RESPProtocol {
  private static final String CRLF = "\r\n";

  public String simpleString(final String str) {
    RESPUtils.ensureValidSimpleString(str);
    return SIMPLE_STRING.getCharValue() + str + CRLF;
  }

  public String error(final RESPError error) {
    RESPUtils.ensureNotNull(error);
    return ERROR.getCharValue() + error.getMessage() + CRLF;
  }

  public String integer(final long num) {
    return INTEGER.getCharValue() + num + CRLF;
  }

  public String bool(final boolean value) {
    return value ? integer(1) : integer(0);
  }

  public String bulkString(final String str) {
    RESPUtils.ensureNotNull(str);

    byte[] data = RESPUtils.toBytes(str);
    StringBuilder sb = new StringBuilder();

    sb.append(BULK_STRING.getCharValue());
    sb.append(data.length);
    sb.append(CRLF);
    sb.append(RESPUtils.toString(data));
    sb.append(CRLF);

    return sb.toString();
  }

  public String nil() {
    return BULK_STRING.getCharValue() + "-1" + CRLF;
  }

  public String array(final Object[] objects) {
    RESPUtils.ensureNotNull(objects);
    int length = objects.length;
    return ARRAY.getCharValue() + length + CRLF + arrayToString(objects);
  }

  private String arrayToString(final Object[] objects) {
    return Stream.of(objects)
        .map(this::serializeObject)
        .collect(Collectors.joining());
  }

  private String serializeObject(final Object o) {
    if (o == null) {
      return nil();
    }
    if (o instanceof Boolean) {
      return bool((Boolean) o);
    }
    if (o instanceof Long || o instanceof Integer) {
      return integer((long) o);
    }
    if (o instanceof String) {
      return bulkString((String) o);
    }
    throw new UnsupportedDataTypeException("Unsupported type: " + o.getClass().getName());
  }

}
