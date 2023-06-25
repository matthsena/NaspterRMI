package naspter.peer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerThread extends Thread {
  private int port;
  private String path;

  public PeerThread(int port, String path) {
    this.port = port;
    this.path = path;
  }

  public void run() {
    try (ServerSocket server = new ServerSocket(port)) {
      while (true) {
        try (Socket node = server.accept()) {
          // Get input and output streams
          InputStream in = node.getInputStream();
          BufferedReader strReader = new BufferedReader(new InputStreamReader(in));

          // Read the requested file path
          String fileName = strReader.readLine();

          File file = new File(this.path + "/" + fileName);
          FileInputStream fis = new FileInputStream(file);
          BufferedInputStream reader = new BufferedInputStream(fis);

          OutputStream output = node.getOutputStream();

          byte[] contents;
          long fileLength = file.length();
          long current = 0L;

          while (current != fileLength) {
            int size = 1024 * 1024;
            if (fileLength - current >= size)
              current += size;
            else {
              size = (int) (fileLength - current);
              current = fileLength;
            }
            contents = new byte[size];
            reader.read(contents, 0, size);
            output.write(contents);
            System.out.print("Sending file ... " + (current * 100) / fileLength + "% complete!\n");
          }

          output.flush();
          node.close();

          System.out.println("File sent successfully!");

          reader.close();

        } catch (Exception e) {
          // TODO: handle exception
          System.out.println(e.getMessage());
        }
      }
    } catch (IOException e) {
      System.out.println("An error occurred while opening the ServerSocket: " + e.getMessage());
    }
  }
}