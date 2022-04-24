package io.github.benrkia.redis.parser;

import static io.github.benrkia.redis.protocol.RESPFirstByte.ARRAY;
import static io.github.benrkia.redis.protocol.RESPFirstByte.BULK_STRING;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.benrkia.redis.cmd.Cmd;
import io.github.benrkia.redis.cmd.Ping;
import io.github.benrkia.redis.exception.RESPError;
import io.github.benrkia.redis.exception.SyntaxError;
import io.github.benrkia.redis.exception.UnsupportedCmdError;
import io.github.benrkia.redis.protocol.RESPFirstByte;
import io.github.benrkia.redis.utils.Constants;
import io.github.benrkia.redis.utils.RESPUtils;

public final class RESPParser implements Closeable {
  private static final int BUFFER_LENGTH = 4096;
  private static final int END_OF_STREAM = -1;
  private final BufferedInputStream is;
  private byte[] raw;
  private int current = 0;
  private int availableLength = 0;

  public RESPParser(final InputStream is) {
    Objects.requireNonNull(is);
    this.is = new BufferedInputStream(is);
  }

  public boolean hasCmd() {
    return !isAtEnd();
  }

  public Cmd nextCmd() throws RESPError {
    try {
      return parse(array());
    } catch (RESPError e) {
      synchronize();
      throw e;
    }
  }

  private Cmd parse(final List<String> tokens) throws RESPError {
    if (tokens.isEmpty())
      throwUnsupportedCmd();

    CmdType type = CmdType.from(tokens.get(0));

    if (type == CmdType.PING)
      return new Ping(readCmdArgs(tokens));

    throwUnsupportedCmd();
    return null;
  }

  private List<String> array() throws SyntaxError {
    matchOrThrow(ARRAY);

    int length = length();
    List<String> cmdParts = new ArrayList<>(length);
    while (length-- > 0) {
      cmdParts.add(bulkString());
    }

    return cmdParts;
  }

  private String bulkString() throws SyntaxError {
    matchOrThrow(BULK_STRING);

    int length = length();
    String value = readStringOfLength(length);
    eatCRLF();

    return value;
  }

  private int length() throws SyntaxError {
    if (!isDigit(peek()))
      throwInvalidCmdFormat();

    int value = 0;
    while (isDigit(peek())) {
      value *= 10;
      value += advance() - Constants.ZERO;
    }
    eatCRLF();

    return value;
  }

  private void eatCRLF() throws SyntaxError {
    if (peek() != Constants.CR)
      throwInvalidCmdFormat();
    advance();

    if (peek() != Constants.LF)
      throwInvalidCmdFormat();
    advance();
  }

  private String readStringOfLength(final int length) throws SyntaxError {
    byte[] stringBuffer = new byte[length];

    for (int i = 0; i < length; i++) {
      if (isAtEnd())
        throwInvalidCmdFormat();
      stringBuffer[i] = advance();
    }

    return RESPUtils.toString(stringBuffer);
  }

  private String[] readCmdArgs(final List<String> tokens) {
    return tokens.stream()
        .skip(1)
        .toArray(String[]::new);
  }

  private byte advance() {
    return raw[current++];
  }

  private byte peek() {
    if (isAtEnd())
      return '\0';
    return raw[current];
  }

  private boolean isDigit(byte b) {
    return b >= Constants.ZERO && b <= Constants.NINE;
  }

  private void matchOrThrow(
      RESPFirstByte type) throws SyntaxError {
    if (!match(type))
      throwInvalidCmdFormat();
  }

  private boolean match(RESPFirstByte type) {
    if (!check(type))
      return false;
    advance();
    return true;
  }

  private boolean check(RESPFirstByte type) {
    if (isAtEnd())
      return false;
    return peek() == type.getCharPoint();
  }

  private boolean isAtEnd() {
    if (availableLength < 0)
      return false;
    if (current >= availableLength) {
      try {
        readRaw();
      } catch (IOException e) {
        logError("Error while reading from input stream", e);
        return true;
      }
    }
    return current >= availableLength;
  }

  private void readRaw() throws IOException {
    raw = new byte[BUFFER_LENGTH];
    try {
      availableLength = is.read(raw);
      if (availableLength == 0)
        availableLength = END_OF_STREAM;
      current = 0;
    } catch (IOException e) {
      availableLength = END_OF_STREAM;
      throw e;
    }
  }

  private void synchronize() {
    while (current < availableLength) {
      if (check(ARRAY))
        return;
      current++;
    }

    raw = null;
    current = availableLength = 0;
  }

  private void throwInvalidCmdFormat() throws SyntaxError {
    throw new SyntaxError("invalid command format");
  }

  private void throwUnsupportedCmd() throws UnsupportedCmdError {
    throw new UnsupportedCmdError("unsupported command");
  }

  private void logError(final String msg, final Exception e) {
    System.err.println(msg + ":: " + e.getMessage());
  }

  @Override
  public void close() throws IOException {
    is.close();
  }

}
