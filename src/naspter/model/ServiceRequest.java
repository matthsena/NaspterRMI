package naspter.model;

import java.rmi.Remote;
import java.util.List;
import java.util.Map;

public interface ServiceRequest extends Remote {
    public String join(String ip, String port, String folderPath, List<String> files) throws Exception;

    public Map<String, String> search(String fileName) throws Exception;

    public void update(String fileName) throws Exception;
}
