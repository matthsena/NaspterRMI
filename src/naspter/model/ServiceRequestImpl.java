package naspter.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRequestImpl extends UnicastRemoteObject implements ServiceRequest {
    private static final long serialVersionUID = 1L;

    public ServiceRequestImpl() throws RemoteException {
        super();
    }

    public class FileStore {
        // HashMap que armazena os pares de nome de arquivo e
        // lista de peers que possuem aquele arquivo
        private Map<String, List<NaspterPeer>> fileMap;

        public FileStore() {
            this.fileMap = new ConcurrentHashMap<>();
        }

        public List<NaspterPeer> getPeersByFilename(String filename) {
            // Lista de peers que possuem o arquivo com o nome especificado
            List<NaspterPeer> peers = this.fileMap.get(filename);

            // Se a lista for numa (arquivo nao existe) retorna null
            if (peers == null) {
                return null;
            }
            // senao retorna todos os peers que possuem aquele arquivo
            return peers;
        }

        public void addPeerToFile(String filename, NaspterPeer peer) {
            // Obtém a lista de peers que possuem o arquivo com o nome especificado
            List<NaspterPeer> peers = this.fileMap.get(filename);

            // Se a lista de peers for nula (arquivo ainda nao inserido)
            // cria uma nova lista vazia e adiciona o peer a lista
            if (peers == null) {
                peers = new ArrayList<>();
            }

            peers.add(peer);

            // Atualiza o hashmap de arquivos
            this.fileMap.put(filename, peers);
        }
    }

    FileStore fileStore = new FileStore();

    @Override
    public String join(NaspterPeer np, List<String> files) {
        try {
            // Adiciona o peer atual à lista de peers que possuem
            // cada um dos arquivos desse peer
            for (String file : files) {
                fileStore.addPeerToFile(file, np);
            }

            System.out.printf("Peer %s:%s adicionado com arquivos %s\n", np.ip, np.port, String.join(", ", files));

            return "JOIN_OK";
        } catch (Exception e) {
            // Exibe uma mensagem de erro e retorna uma mensagem de erro
            e.printStackTrace();
            return "JOIN_ERROR";
        }
    }

    @Override
    public List<NaspterPeer> search(NaspterPeer currentPeer, String fileName) throws RemoteException {
        System.out.printf("Peer %s:%s solicitou arquivo %s\n", currentPeer.ip, currentPeer.port, fileName);

        // Retorna a lista de peers que possuem o arquivo com o nome especificado
        // ou null caso ele nao encontre o arquivo
        return fileStore.getPeersByFilename(fileName);
    }

    @Override
    public String update(NaspterPeer np, String fileName) throws RemoteException {
        try {
            // Adiciona o peer especificado à lista de peers
            // que possuem o arquivo com o nome especificado
            fileStore.addPeerToFile(fileName, np);

            return "UPDATE_OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "UPDATE_ERROR";
        }
    }
}
