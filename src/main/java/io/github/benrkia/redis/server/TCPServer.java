package io.github.benrkia.redis.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class TCPServer implements Closeable {

  private final int port;
  private ServerSocket socket;
  private boolean running = false;

  protected TCPServer(int port) {
    this.port = port;
  }

  public void start() {
    try {
      socket = new ServerSocket(port);
      socket.setReuseAddress(true);
      listen();
    } catch (IOException ignored) {
    }
  }

  protected void stop() throws IOException {
    running = false;
    this.close();
  }

  private void listen() throws IOException {
    running = true;
    while (running) {
      handle(socket.accept());
    }
  }

  abstract void handle(final Socket socket) throws IOException;

  @Override
  public void close() throws IOException {
    if (socket != null) {
      socket.close();
    }
  }

}