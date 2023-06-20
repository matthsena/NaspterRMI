package naspter.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ServiceRequestImpl extends UnicastRemoteObject implements ServiceRequest {
    public static FileStore fileStore = new FileStore();

    private static final long serialVersionUID = 1L;

    public ServiceRequestImpl() throws RemoteException {
        super();
    }

    @Override
    public void join(String ip, String port, String folderPath) throws IOException {
        NaspterPeer np = new NaspterPeer(ip, port, folderPath);
        String path = Paths.get("files").resolve(folderPath).toString();

        // create the folder if it doesn't exist
        Files.createDirectories(Paths.get(path));

        List<String> files = FileUtil.getAllFileNamesInDirectory(path);

        for (String file : files) {
            fileStore.addPeerToFile(file, np);
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
