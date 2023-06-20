package naspter.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServiceRequestImpl extends UnicastRemoteObject implements ServiceRequest {
    private static final long serialVersionUID = 1L;
    
    public ServiceRequestImpl() throws RemoteException {
        super();
    }

    @Override
    public void join(NaspterPeer peer) throws RemoteException {

    }

    @Override
    public void search(String fileName) throws RemoteException {
        
    }

    @Override
    public void update(String fileName) throws RemoteException {
        
    }
}
