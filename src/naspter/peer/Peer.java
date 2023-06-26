package naspter.peer;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import naspter.model.ServiceRequest;

public class Peer {
  private static List<String> getFileNames(String directoryPath) throws IOException {
    // TRECHO BASEADO EM:
    // https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java
    List<String> fileNames = new ArrayList<>();

    Path directory = Paths.get(directoryPath);
    /**
     * Primeiro percorre todos os arquivos e subdiretórios do path fornecido.
     * Após isso, filtra apenas arquivos regulares (não diretórios).
     * Por fim, adiciona o nome de cada arquivo na List<String>
     */
    Files.walk(directory)
        .filter(Files::isRegularFile)
        .forEach(path -> fileNames.add(path.getFileName().toString()));
    return fileNames;
  }

  public static void main(String[] args) throws Exception {
    // Referencia do registro RMI e do objeto remoto ServiceRequest
    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
    ServiceRequest serviceRequest = (ServiceRequest) registry.lookup("rmi://127.0.0.1/serviceRequest");

    Scanner scanner = new Scanner(System.in);

    String option;

    ServiceRequest.NaspterPeer currentPeer = null;

    // Lista de peers retornados na ultima pesquisa do peer
    List<ServiceRequest.NaspterPeer> searchResults = new ArrayList<>();
    // Guarda a última pesquisa realizada pelo peer
    String lastSearchFile = "";

    do {
      System.out.println("\nEscolha uma opção:");
      System.out.println("1 - JOIN");
      System.out.println("2 - SEARCH");
      System.out.println("3 - DOWNLOAD\n");

      option = scanner.nextLine();

      switch (option) {
        case "1":
          // IP do peer
          System.out.println("\nDigite seu IP:");
          String ip = scanner.nextLine();

          // Número da porta
          System.out.println("\nDigite o numero da porta:");
          int port = Integer.parseInt(scanner.nextLine());

          // Nome da pasta que contenha os arquivos do peer
          System.out.println("\nDigite o nome da sua pasta:");
          String folderName = scanner.nextLine();

          // Cria um obj NaspterPeer
          currentPeer = new ServiceRequest.NaspterPeer(ip, port, folderName);

          // Caso a pasta do peer não exista, cria a pasta
          String path = Paths.get("files").resolve(folderName).toString();
          Files.createDirectories(Paths.get(path));

          // Obtém a lista de arquivos na pasta do peer
          List<String> files = getFileNames(path);

          // Faz o JOIN desse peer no servidor
          String joinStatus = serviceRequest.join(currentPeer, files);

          // Verifica se o peer foi registrado com sucesso
          if (joinStatus.equals("JOIN_OK")) {
            String filesString = String.join(", ", files);
            System.out.printf("\nSou peer %s:%s com arquivos %s\n", ip, port, filesString);

            // Thread para lidar com as req de download de arquivos
            // quando um arquivo do peer for solicitado
            PeerThread peerThread = new PeerThread(port, path);
            peerThread.start();
          } else {
            System.out.println("\nJOIN FAIL");
          }

          break;
        case "2":
          // Verifica se o peer que esta pesquisando ja fez o JOIN
          if (currentPeer != null) {
            // Solicita o nome do arquivo que o usuário deseja pesquisar
            System.out.println("\nDigite o nome do arquivo que deseja pesquisar:");
            lastSearchFile = scanner.nextLine();

            // Invoca o método remoto SEARCH
            searchResults = serviceRequest.search(currentPeer, lastSearchFile);

            System.out.println("\nPeers com o arquivo solicitado:");

            // Verifica se a lista de resultados de pesquisa é nula ou vazia
            if (searchResults != null && !searchResults.isEmpty()) {
              // Se a lista não for nula nem vazia
              // exibe os peers (IP:PORTA) que possuem o arquivo solicitado
              for (ServiceRequest.NaspterPeer np : searchResults) {
                System.out.println(np.ip + ":" + np.port);
              }
            }
          }
          break;
        case "3":
          // Verifica se o historico de resultados de pesquisa não está vazio
          // e se o peer que esta pesquisando ja fez o JOIN
          if (searchResults != null && !searchResults.isEmpty() && currentPeer != null) {
            // Seleciona um peer aleatório da lista de peers que possuem o arquivo
            ServiceRequest.NaspterPeer np = searchResults.get(new Random().nextInt(searchResults.size()));

            // Verifica se o nome do arquivo da última pesquisa não está vazio
            if (!lastSearchFile.isEmpty()) {
              // Socket para se conectar ao peer selecionado e os objetos de saida
              Socket socket = new Socket(np.ip, np.port);

              OutputStream out = socket.getOutputStream();
              DataOutputStream writter = new DataOutputStream(out);

              // Envia o nome do arquivo da última pesquisa para o peer selecionado
              writter.writeBytes(lastSearchFile + '\n');

              // Obtém o arquivo do peer selecionado
              InputStream input = socket.getInputStream();
              byte[] buffer = new byte[1024 * 1024]; // blocos de 1MB

              // Cria o arquivo no diretório do peer, com o nome do arquivo da última pesquisa
              FileOutputStream fileOutputStream = new FileOutputStream(
                  "files/" + currentPeer.folderPath + "/" + lastSearchFile);

              // para melhorar a performance, essa classe lê blocos de bytes do arquivo
              // ao invés de ler byte por byte
              BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

              // quantidade de bytes lidos
              int bytesRead;

              // enquanto houver bytes para ler, lê os bytes do arquivo e escreve no arquivo
              while ((bytesRead = input.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
              }

              // Fecha os fluxos de saída e entrada e limpa o buffer
              bufferedOutputStream.flush();
              bufferedOutputStream.close();
              socket.close();

              // Chama o método remoto UPDATE para atualizar o servidor
              // que esse peer possui o arquivo
              serviceRequest.update(currentPeer, lastSearchFile);

              System.out.printf("\nArquivo %s baixado com sucesso na pasta %s\n", lastSearchFile,
                  currentPeer.folderPath);
            }
          }

          break;
        default:
          System.out.println("Invalid option");
          break;
      }

    } while (!option.equals("-1"));

    scanner.close();
  }
}
