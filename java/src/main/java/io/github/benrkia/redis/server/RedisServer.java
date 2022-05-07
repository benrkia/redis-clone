package io.github.benrkia.redis.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

import io.github.benrkia.redis.cmd.Cmd;
import io.github.benrkia.redis.exception.RESPError;
import io.github.benrkia.redis.parser.RESPParser;
import io.github.benrkia.redis.protocol.RESPProtocol;

public class RedisServer extends TCPServer {
  private static final int DEFAULT_PORT = 6379;

  public RedisServer() throws IOException {
    super(DEFAULT_PORT);
  }

  @Override
  void handle(Socket socket) throws IOException {
    new RequestHandler(socket).start();
  }

  private static class RequestHandler extends Thread {
    private final Socket socket;
    private final RESPProtocol protocol;

    public RequestHandler(Socket socket) {
      Objects.requireNonNull(socket);
      this.socket = socket;
      protocol = new RESPProtocol();
    }

    @Override
    public void run() {

      try (
          PrintWriter out = new PrintWriter(socket.getOutputStream());
          InputStream is = socket.getInputStream();
          RESPParser parser = new RESPParser(is);) {

        run(parser, out);

        socket.close();
      } catch (IOException ignored) {
      }
    }

    private void run(final RESPParser parser, final PrintWriter out) {
      while (parser.hasCmd()) {
        String result;
        try {
          Cmd cmd = parser.nextCmd();
          result = cmd.execute();
        } catch (RESPError error) {
          result = protocol.error(error);
        }
        out.print(result);
        out.flush();
      }
    }
  }

}