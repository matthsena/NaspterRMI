package naspter.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import naspter.model.ServiceRequest;

public class Peer {

  public static void main(String[] args) throws Exception {

    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
    ServiceRequest serviceRequest = (ServiceRequest) registry.lookup("rmi://127.0.0.1/serviceRequest");

    serviceRequest.search("file.png");

    System.out.println("Peer search");

  }
}
