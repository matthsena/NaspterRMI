package naspter.model;

import java.rmi.Remote;
import java.util.List;

public interface ServiceRequest extends Remote {
    public String join(String ip, String port, String folderPath, List<String> files) throws Exception;

    public void search(String fileName) throws Exception;

    public void update(String fileName) throws Exception;
}
