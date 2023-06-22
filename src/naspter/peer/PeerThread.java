package naspter.peer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class PeerThread extends Thread {
  private Socket node = null;

  public PeerThread(Socket node) {
    this.node = node;
  }

  public void run() {
    try {
      // Get input and output streams
      InputStream in = node.getInputStream();
      BufferedReader strReader = new BufferedReader(new InputStreamReader(in));

      // Read the requested file path
      String filePath = strReader.readLine();

      System.out.println("File path: " + filePath);

      File file = new File("files/teste/file.png");
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
      System.out.println(e);
    }
  }
}