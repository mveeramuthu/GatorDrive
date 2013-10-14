public class RemotePeerInfo {
	public int peerId;
	public String peerAddress;
	public int peerPort;
	
	public RemotePeerInfo(int pId, String pAddress, int pPort) {
		peerId = pId;
		peerAddress = pAddress;
		peerPort = pPort;		
	}
}