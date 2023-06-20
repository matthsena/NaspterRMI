package naspter.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import naspter.model.ServiceRequest;
import naspter.model.ServiceRequestImpl;

public class Server {
    public static void main(String[] args) throws Exception {
        ServiceRequest serviceRequest = new ServiceRequestImpl();

        LocateRegistry.createRegistry(1099);

        Registry registry = LocateRegistry.getRegistry();

        registry.rebind("rmi://127.0.0.1/serviceRequest", serviceRequest);

    }
}
