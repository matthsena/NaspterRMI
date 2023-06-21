package naspter.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
          } else {
            System.out.println("\nJOIN FAIL");
          }

          break;
        case "2":
          System.out.println("\nDigite o nome do arquivo que deseja pesquisar:");
          String fileName = scanner.nextLine();
          serviceRequest.search(fileName);
          break;
        case "3":
          System.out.println("DOWNLOAD");
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
