package naspter.model;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

public interface ServiceRequest extends Remote {
    public class NaspterPeer implements Serializable {
        public String ip;
        public int port;
        public String folderPath;

        public NaspterPeer(String ip, int port, String folderPath) {
            this.ip = ip;
            this.port = port;
            this.folderPath = folderPath;
        }
    }

    public String join(String ip, int port, String folderPath, List<String> files) throws Exception;

    public List<NaspterPeer> search(NaspterPeer currentPeer, String fileName) throws Exception;

    public String update(String ip, int port, String folder, String fileName) throws Exception;
}
