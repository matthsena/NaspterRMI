package naspter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStore {
  private Map<String, List<NaspterPeer>> fileMap;

  public FileStore() {
    this.fileMap = new HashMap<>();
  }

  // public void addFile(String filename, List<NaspterPeer> peers) {
  // this.fileMap.put(filename, peers);
  // }

  public List<NaspterPeer> getPeers(String filename) {
    return this.fileMap.get(filename);
  }

  public void addPeerToFile(String filename, NaspterPeer peer) {
    List<NaspterPeer> peers = this.fileMap.get(filename);
    if (peers == null) {
      peers = new ArrayList<>();
    }
    peers.add(peer);
    this.fileMap.put(filename, peers);
  }
}