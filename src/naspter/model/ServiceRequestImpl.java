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
        private Map<String, List<NaspterPeer>> fileMap;

        public FileStore() {
            this.fileMap = new ConcurrentHashMap<>();
        }

        public List<NaspterPeer> getPeersByFilename(String filename) {
            return this.fileMap.get(filename);
        }

        public void addPeerToFile(String filename, NaspterPeer peer) {
            List<NaspterPeer> peers = this.fileMap.get(filename);
            if (peers == null) {
                peers = new ArrayList<>();
            }
            peers.add(peer);
            this.fileMap.put(filename, peers);
        }
    }

    FileStore fileStore = new FileStore();

    @Override
    public String join(String ip, int port, String folder, List<String> files) {
        try {
            NaspterPeer np = new NaspterPeer(ip, port, folder);

            for (String file : files) {
                fileStore.addPeerToFile(file, np);
            }

            System.out.printf("Peer %s:%s adicionado com arquivos %s\n", ip, port, String.join(", ", files));

            return "JOIN_OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "JOIN_ERROR";
        }
    }

    @Override
    public List<NaspterPeer> search(String fileName) throws RemoteException {
        return fileStore.getPeersByFilename(fileName);
    }

    @Override
    public String update(String ip, int port, String folder, String fileName) throws RemoteException {
        try {
            NaspterPeer np = new NaspterPeer(ip, port, folder);
            fileStore.addPeerToFile(fileName, np);
            return "UPDATE_OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "UPDATE_ERROR";
        }
    }
}
