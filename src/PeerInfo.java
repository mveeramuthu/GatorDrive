/**
 * This class defines the structure of a peer
 */
public class PeerInfo {
	private int peerID;
	private String hostname;
	private int listeningPort;
	private boolean hasFile;

	public PeerInfo(int peerID, String hostname, int listeningPort,
			boolean hasFile) {
		this.peerID = peerID;
		this.hostname = hostname;
		this.listeningPort = listeningPort;
		this.hasFile = hasFile;
	}
	
	public int getPeerID() {
		return peerID;
	}

	public String getHostname() {
		return hostname;
	}

	public int getListeningPort() {
		return listeningPort;
	}

	public boolean hasFile() {
		return hasFile;
	}

}
