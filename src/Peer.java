/**
 * Handles specific peer related functionalities
 */
public class Peer {	
	private PeerInfo peerInfo;
	private int noPrevPiecesRcvd;
	private BitField bitField;	
	private PeerConn connection;
	private boolean isChokingMe;
	private boolean isChokedbyMe;
	private boolean isInterested;
	private boolean amInterested;


	public Peer(int peerId, PeerConn peerConnection) {
		this.peerInfo = PeerInfoConfig.getInstance().getPeerInfo(peerId);
		this.connection = peerConnection;
	}

	public synchronized boolean isChokingMe() {
		return isChokingMe;
	}

	public synchronized void setChokingMe(boolean isChokingMe) {
		this.isChokingMe = isChokingMe;
	}

	public synchronized boolean amInterested() {
		return amInterested;
	}

	public synchronized void setAmInterested(boolean amInterested) {
		this.amInterested = amInterested;
	}

	public synchronized void setIsInterested(boolean isInterested) {
		this.isInterested = isInterested;
	}
	
	public PeerInfo getPeerInfo() {
		return peerInfo;
	}

	public synchronized int getPrevPieces() {
		return noPrevPiecesRcvd;
	}

	public PeerConn getConnection() {
			return connection;
	}

	public synchronized void addRecvPieces() {
		++noPrevPiecesRcvd;
	}

	public synchronized void resetPieces() {
		noPrevPiecesRcvd = 0;
	}

	public BitField getBitField() {
		return bitField;
	}

	public void choke() {
		ChokeMsg cm = new ChokeMsg();
		this.isChokedbyMe = true;
		getConnection().sendToPeer(cm.convert());
	}

	public void unChoke() {
		this.isChokedbyMe = false;
		UnchokeMsg ucm = new UnchokeMsg();
		getConnection().sendToPeer(ucm.convert());
	}

	public void setBitField(BitField bitField) {
		this.bitField = new BitField(bitField);
	}

	public void sendAmInterested(boolean amInterested) {

		if (amInterested) {
			InterestedMsg im = new InterestedMsg();
			getConnection().sendToPeer(im.convert());
		} else {
			NotInterestedMsg nim = new NotInterestedMsg();
			getConnection().sendToPeer(nim.convert());
		}
	}

}