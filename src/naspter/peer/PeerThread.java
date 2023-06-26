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

// THREAD destinada para enviar arquivos solicitados pelos peers
public class PeerThread extends Thread {
  private int port;
  private String path;

  public PeerThread(int port, String path) {
    // porta e path que o peer irá servir os arquivos
    this.port = port;
    this.path = path;
  }

  public void run() {
    try (ServerSocket server = new ServerSocket(port)) {
      while (true) {
        try (Socket node = server.accept()) {
          // Obtém o input stream do socket
          // usado para receber os dados do cliente
          InputStream in = node.getInputStream();
          BufferedReader strReader = new BufferedReader(new InputStreamReader(in));

          // Lê o nome do arquivo solicitado a partir do input stream
          String fileName = strReader.readLine();

          // Cria um objeto File para o arquivo solicitado
          // sendo this.path = diretório onde o arquivo solicitado esta
          File file = new File(this.path + "/" + fileName);
          // Lê o conteúdo do arquivo
          FileInputStream fis = new FileInputStream(file);
          // Cria um BufferedInputStream para ler o arquivo em blocos
          BufferedInputStream reader = new BufferedInputStream(fis);

          // Obtém o OutputStream do socket
          // usado para enviar o arquivo para o cliente
          OutputStream output = node.getOutputStream();

          // array de bytes que armazenará o conteúdo do arquivo
          byte[] contents;
          // tamanho do arquivo
          long fileLength = file.length();
          // posição atual de leitura do arquivo
          long current = 0L;

          // Lê o arquivo em blocos de 1MB e envia cada bloco para o cliente
          while (current != fileLength) {
            int size = 1024 * 1024; // 1MB
            // Se o tamanho restante for menor que 1MB
            if (fileLength - current >= size)
              current += size;
            else {
              size = (int) (fileLength - current);
              current = fileLength;
            }
            // Lê o arquivo em blocos de 1MB ou menos (dependendo do tamanho restante)
            contents = new byte[size];
            // Lê o arquivo e armazena o conteúdo no array de bytes
            reader.read(contents, 0, size);
            // Envia o bloco para o cliente de acordo com o tamanho do bloco
            output.write(contents);
          }

          // Limpa o buffer de saída e fecha o socket
          output.flush();
          node.close();

          // Fecha o fluxo de entrada e o arquivo
          reader.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}