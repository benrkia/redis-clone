package io.github.benrkia.redis.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

import io.github.benrkia.redis.cmd.Cmd;
import io.github.benrkia.redis.cmd.Ping;
import io.github.benrkia.redis.exception.RESPError;
import io.github.benrkia.redis.parser.RESPParser;
import io.github.benrkia.redis.protocol.RESPProtocol;
import io.github.benrkia.redis.utils.RESPUtils;

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
          BufferedReader in = new BufferedReader(new InputStreamReader(is));) {

        String line = in.readLine();
        while (line != null && line.length() > 0) {
          out.println(getPong());
          out.flush();
          line = in.readLine();
        }

        socket.close();
      } catch (IOException ignored) {
      }
    }

    private byte[] readInputStream(final InputStream is) throws IOException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      int len;

      while ((len = is.read(buffer)) != -1) {
        outputStream.write(buffer, 0, len);
      }

      byte[] data = outputStream.toByteArray();

      outputStream.close();
      return data;
    }

    private String getPong() {
      try {
        return new Ping().execute();
      } catch (RESPError e) {
        return protocol.error(e);
      }
    }

    private String run(final byte[] data) {
      try {
        String input = RESPUtils.toString(data);
        Cmd cmd = new RESPParser(input).parse();
        return cmd.execute();
      } catch (RESPError error) {
        return protocol.error(error);
      }
    }
  }

}