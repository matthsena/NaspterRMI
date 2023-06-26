package naspter.model;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

public interface ServiceRequest extends Remote {
    // Obj do Peer
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

    public String join(NaspterPeer np, List<String> files) throws Exception;

    public List<NaspterPeer> search(NaspterPeer currentPeer, String fileName) throws Exception;

    public String update(NaspterPeer np, String fileName) throws Exception;
}
