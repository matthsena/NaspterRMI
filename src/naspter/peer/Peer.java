package naspter.peer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import naspter.model.ServiceRequest;

public class Peer {
  private static List<String> getFileNames(String directoryPath) throws IOException {
    List<String> fileNames = new ArrayList<>();
    Path directory = Paths.get(directoryPath);
    Files.walk(directory)
        .filter(Files::isRegularFile)
        .forEach(path -> fileNames.add(path.getFileName().toString()));
    return fileNames;
  }

  public static void main(String[] args) throws Exception {

    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
    ServiceRequest serviceRequest = (ServiceRequest) registry.lookup("rmi://127.0.0.1/serviceRequest");

    Scanner scanner = new Scanner(System.in);
    String option;

    Map<String, String> searchResults = new HashMap<>();

    do {
      System.out.println("\nEscolha uma opção:");
      System.out.println("1 - JOIN");
      System.out.println("2 - SEARCH");
      System.out.println("3 - DOWNLOAD");
      System.out.println("0 - SAIR\n");

      option = scanner.nextLine();

      switch (option) {
        case "1":
          System.out.println("\nDigite seu IP:");
          String ip = scanner.nextLine();

          System.out.println("\nDigite o numero da porta:");
          String port = scanner.nextLine();

          System.out.println("\nDigite o nome da sua pasta:");
          String folderName = scanner.nextLine();

          String path = Paths.get("files").resolve(folderName).toString();
          Files.createDirectories(Paths.get(path));

          List<String> files = getFileNames(path);

          String joinStatus = serviceRequest.join(ip, port, folderName, files);

          if (joinStatus.equals("JOIN_OK")) {
            String filesString = String.join(", ", files);

            System.out.printf("Sou peer %s:%s com arquivos %s\n", ip, port, filesString);

            try {
              ServerSocket server = new ServerSocket(Integer.parseInt(port));

              while (true) {
                Socket node = server.accept();

                PeerThread peerThread = new PeerThread(node);
                peerThread.start();
              }

            } catch (Exception e) {
              System.out.println(e);
            }
          } else {
            System.out.println("\nJOIN FAIL");
          }

          break;
        case "2":
          System.out.println("\nDigite o nome do arquivo que deseja pesquisar:");
          String fileName = scanner.nextLine();

          searchResults = serviceRequest.search(fileName);
          break;
        case "3":
          if (!searchResults.isEmpty()) {
            Set<String> keySet = searchResults.keySet();
            Iterator<String> iterator = keySet.iterator();

            if (iterator.hasNext()) {
              String file = iterator.next();

              String[] fileData = searchResults.get(file).split(":");
              String peerIp = fileData[0];
              String peerPort = fileData[1];

              Socket socket = new Socket(peerIp, Integer.parseInt(peerPort));

              InputStream input = socket.getInputStream();
              byte[] buffer = new byte[1024 * 1024];
              FileOutputStream fileOutputStream = new FileOutputStream("files/download.png");
              BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

              int bytesRead;

              while ((bytesRead = input.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
              }

              bufferedOutputStream.flush();
              bufferedOutputStream.close();

              System.out.println("\nArquivo baixado com sucesso");
            }
          } else {
            System.out.println("\nNenhum arquivo encontrado");
          }

          break;
        case "0":
          System.out.println("SAIR");
          break;
        default:
          System.out.println("Invalid option");
          break;
      }

    } while (!option.equals("0"));

    scanner.close();
  }
}
