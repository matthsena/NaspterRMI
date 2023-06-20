package naspter.model;

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
