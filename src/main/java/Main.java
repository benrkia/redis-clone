import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static final int DEFAULT_PORT = 6379;

  public static void main(String[] args) {
    try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
      serverSocket.setReuseAddress(true);
      // Wait for connection from client.
      try (Socket clientSocket = serverSocket.accept()) {
        // Do something with the client socket.
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
