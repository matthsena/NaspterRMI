package naspter.model;

import java.rmi.Remote;

public interface ServiceRequest extends Remote {
    public void join(NaspterPeer peer) throws Exception;

    public void search(String fileName) throws Exception;

    public void update(String fileName) throws Exception;
}
