package naspter.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRequestImpl extends UnicastRemoteObject implements ServiceRequest {
    private static final long serialVersionUID = 1L;

    public ServiceRequestImpl() throws RemoteException {
        super();
    }

    public class NaspterPeer {
        public String ip;
        public int port;
        public String folderPath;

        public NaspterPeer(String ip, int port, String folderPath) {
            this.ip = ip;
            this.port = port;
            this.folderPath = folderPath;
        }
    }

    public class FileStore {
        private Map<String, List<NaspterPeer>> fileMap;

        public FileStore() {
            this.fileMap = new ConcurrentHashMap<>();
        }

        public List<NaspterPeer> getPeersByFilename(String filename) {
            return this.fileMap.get(filename);
        }

        public Map<String, List<NaspterPeer>> getFileMap() {
            return this.fileMap;
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
    public Map<String, String> search(String fileName) throws RemoteException {
        List<NaspterPeer> peers = fileStore.getPeersByFilename(fileName);
        Map<String, String> result = new ConcurrentHashMap<>();

        if (!peers.isEmpty()) {
            NaspterPeer randomPeer = peers.get(new Random().nextInt(peers.size()));
            result.put(fileName, randomPeer.ip + ":" + randomPeer.port);
        }

        return result;
    }

    @Override
    public void update(String ip, int port, String folder, String fileName) throws RemoteException {
        NaspterPeer np = new NaspterPeer(ip, port, folder);
        fileStore.addPeerToFile(fileName, np);

        Map<String, List<NaspterPeer>> fileMap = fileStore.getFileMap();

        System.out.println("Files associated with " + np + ":");
        for (Map.Entry<String, List<NaspterPeer>> entry : fileMap.entrySet()) {
            String file = entry.getKey();
            List<NaspterPeer> peers = entry.getValue();
            if (peers.contains(np)) {
                System.out.println(file);
            }
        }
    }
}
