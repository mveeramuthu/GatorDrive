import java.io.IOException;

public class PeerConnMgr implements Runnable {
	private Client client;
	private Peer peer;
	private int peerId;
	private MessageMgr msgMgr;
	private volatile boolean running;
	
	public PeerConnMgr(Peer peer, Client client) {
		this.client = client;
		this.peer = peer;
		this.running = true;
		this.msgMgr = new MessageMgr(client);
		this.peerId = peer.getPeerInfo().getPeerID();
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void run() {
		while (running) {
			byte[] message = null;
			try {
					message = peer.getConnection().readMessage();					
			} catch (IOException e1) {
				client.getLogger().log("Closing connection with peer");
				System.exit(0);		
				return;
			}
			
			ActualMessages amsg = ActualMessages.getActualMsg(message);
			
			/* Handling the various types of messages */
			
			if (amsg instanceof BitfieldMsg) {
				msgMgr.handleBitfield((BitfieldMsg) amsg, peerId);
			}
			else if (amsg instanceof ChokeMsg) 
			{
				msgMgr.handleChoke((ChokeMsg) amsg, peerId);
			} 
			else if (amsg instanceof HaveMsg) {
				msgMgr.handleHave((HaveMsg) amsg, peerId);
			} 
			else if (amsg instanceof InterestedMsg) {
				msgMgr.handleInterested((InterestedMsg) amsg, peerId);
			} 
			else if (amsg instanceof NotInterestedMsg) {
				msgMgr.handleNotinterested((NotInterestedMsg) amsg,
						peerId);
			} 
			else if (amsg instanceof PieceMsg) {
				msgMgr.handlePieceMessage((PieceMsg) amsg, peerId);
				msgMgr.sendPieceRequest(peerId);
				if (this.client.getFileMgr().getBitField().hasAll()) {
					String msg = "Peer " + this.client.getPeerId()
							+ " has downloaded the complete file";
					this.client.getLogger().log(msg);
				}
			} 
			else if (amsg instanceof RequestMsg) {
				try {
					msgMgr.handleRequestPieceMessage((RequestMsg) amsg,
							peerId);
				} catch (Exception e) {
				}
			} 
			else if (amsg instanceof UnchokeMsg) {
				msgMgr.handleUnChoke((UnchokeMsg) amsg, peerId);
			}
		}
	}
}
