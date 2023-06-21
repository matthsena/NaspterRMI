package naspter.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRequestImpl extends UnicastRemoteObject implements ServiceRequest {
    public class NaspterPeer {
        public String ip;
        public String port;
        public String folderPath;

        public NaspterPeer(String ip, String port, String folderPath) {
            this.ip = ip;
            this.port = port;
            this.folderPath = folderPath;
        }
    }

    public class FileStore {
        private Map<String, List<NaspterPeer>> fileMap;

        public FileStore() {
            this.fileMap = new HashMap<>();
        }

        public List<NaspterPeer> getPeers(String filename) {
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

    private static final long serialVersionUID = 1L;

    public ServiceRequestImpl() throws RemoteException {
        super();
    }

    @Override
    public String join(String ip, String port, String folder, List<String> files) {
        try {
            NaspterPeer np = new NaspterPeer(ip, port, folder);

            for (String file : files) {
                fileStore.addPeerToFile(file, np);
            }

            String filesString = String.join(", ", files);
            System.out.printf("Peer %s:%s adicionado com arquivos %s\n", ip, port, filesString);

            return "JOIN_OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "JOIN_ERROR";
        }
    }

    @Override
    public void search(String fileName) throws RemoteException {
        fileStore.getPeers(fileName).forEach(peer -> {
            System.out.println(peer.ip + ":" + peer.port);
        });
    }

    @Override
    public void update(String fileName) throws RemoteException {

    }
}
