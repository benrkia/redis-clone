package io.github.benrkia.redis.parser;

import static io.github.benrkia.redis.protocol.RESPFirstByte.ARRAY;
import static io.github.benrkia.redis.protocol.RESPFirstByte.BULK_STRING;

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

public final class RESPParser {
  private final byte[] raw;
  private int current = 0;

  public RESPParser(final String input) {
    Objects.requireNonNull(input);
    this.raw = RESPUtils.toBytes(input);
  }

  public Cmd parse() throws RESPError {
    return parse(array());
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
    if (raw.length < current + length)
      throwInvalidCmdFormat();

    int offset = current;
    current += length;

    return RESPUtils.toString(raw, offset, length);
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
    return current >= raw.length;
  }

  private void throwInvalidCmdFormat() throws SyntaxError {
    throw new SyntaxError("invalid command format");
  }

  private void throwUnsupportedCmd() throws UnsupportedCmdError {
    throw new UnsupportedCmdError("unsupported command");
  }

}
