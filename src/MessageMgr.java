/**
 * This class handles all types of messages
 */
public class MessageMgr {
	private Client client = null;

	public MessageMgr(Client client) {
		this.client = client;
	}

	/*
	 * Handling have message
	 */
	public void handleHave(HaveMsg hm, int peerid) {
		int index = hm.getIndex();
		client.getPeerMgr().getPeer(peerid).getBitField().set(index);
		client.getLogger().log("Peer" + peerid + " bitfield: "
				+ client.getPeerMgr().getPeer(peerid).getBitField().getSize());
		client.getLogger().log("Peer" + peerid + " bitfield: "
				+ client.getPeerMgr().getPeer(peerid).getBitField().toString());
		if (!client.getFileMgr().getBitField().has(index)) {
			InterestedMsg im = new InterestedMsg();
			client.getPeerMgr().getPeer(peerid).getConnection().sendToPeer(
					im.convert());
			if (!client.getPeerMgr().getPeer(peerid).isChokingMe()) {
				this.sendPieceRequest(peerid);
			}
		}
		client.getLogger().log(
				"Peer " + client.getPeerId() + " received the 'have' message from "
						+ peerid + " for the piece " + index + ".");
		if (client.getPeerMgr().getPeer(peerid).getBitField().hasAll()) {
			checkComplete();
		}
	}

	/*
	 * Handling Bit field message
	 */
	public void handleBitfield(BitfieldMsg bm, int peerId) {
		client.getPeerMgr().setBitfield(bm.getBitField(), peerId);
		boolean interested = client.getFileMgr().getBitField()
				.hasBitNotOnLocally(bm.getBitField());
		client.getLogger().log("set bitfield of peer " + peerId + ": size="
				+ client.getPeerMgr().getPeer(peerId).getBitField().getSize()
				+ ";"
				+ client.getPeerMgr().getPeer(peerId).getBitField().toString());
		if (interested) {
			client.getPeerMgr().getPeer(peerId).setAmInterested(interested);
			client.getPeerMgr().getPeer(peerId).sendAmInterested(true);
		} else {
			client.getPeerMgr().getPeer(peerId).sendAmInterested(false);
		}
	}

	/*
	 * Handling choke message
	 */
	public void handleChoke(ChokeMsg chokeMsg, int peerId) {
		client.getPeerMgr().setChokingMe(peerId, true);
		client.getLogger().log(
				"Peer " + client.getPeerId() + " is choked by " + peerId + ".");
	}

	/*
	 * Handling unchoke message
	 */
	public void handleUnChoke(UnchokeMsg ucm, int peerid) {
		client.getPeerMgr().setChokingMe(peerid, false);
		client.getLogger().log("am Interested:"
				+ client.getPeerMgr().getPeer(peerid).amInterested());
		if (client.getPeerMgr().getPeer(peerid).amInterested()) {
			this.sendPieceRequest(peerid);
		}
		client.getLogger().log(
				"Peer " + client.getPeerId() + " is unchoked by " + peerid + ".");
	}
	
	/*
	 * Handling interested message
	 */
	public void handleInterested(InterestedMsg im, int peerid) {
		client.getPeerMgr().setPeerInterestedToMe(peerid, true);
		client.getLogger().log(
				"Peer " + client.getPeerId()
						+ " received the 'interested' message from " + peerid
						+ ".");
	}

	/*
	 * Handling not interested message
	 */
	public void handleNotinterested(NotInterestedMsg nim, int peerid) {
		client.getPeerMgr().setPeerInterestedToMe(peerid, false);
		client.getLogger().log(
				"Peer " + client.getPeerId()
						+ " received the 'not interested' message from "
						+ peerid + ".");

	}

	/*
	 * Handling piece message
	 */
	public void handlePieceMessage(PieceMsg pm, int peerid) {
		FilePiece fp = pm.getPiece();
		client.getFileMgr().store(fp);
		String logMsg = "Peer " + this.client.getPeerId()
				+ " has downloaded the piece " + fp.getIndex() + " from "
				+ peerid + " Now the number of pieces it has is "
				+ this.client.getFileMgr().getBitField().numOfOnes();
		client.getLogger().log(logMsg);
		HaveMsg hm = new HaveMsg(fp.getIndex());
		client.getPeerMgr().sendHaveToAll(hm);
		client.getPeerMgr().getPeer(peerid).addRecvPieces();
		for (int pid : client.getPeerMgr().getPeers()) {
			client.getLogger().log("Bitfield: local:"
					+ client.getFileMgr().getBitField().toString());
			client.getLogger().log("remote: "
							+ client.getPeerMgr().getPeer(pid).getBitField()
									.toString());
			if (client.getFileMgr().getBitField().hasBitNotOnLocally(
					client.getPeerMgr().getPeer(pid).getBitField())) {
				client.getPeerMgr().setAmInterested(pid, true);
			} else {
				client.getPeerMgr().setAmInterested(pid, false);
				client.getPeerMgr().getPeer(peerid).sendAmInterested(false);
			}
		}
		if (client.getFileMgr().getBitField().hasAll()) {
			checkComplete();
		}
	}

	/*
	 * Handling request piece message
	 */
	public void handleRequestPieceMessage(RequestMsg rm, int peerid) {
		int index = rm.getIndex();
		if (!client.getFileMgr().getBitField().has(index))
			return;
		FilePiece fp = client.getFileMgr().get(index);
		PieceMsg pm = new PieceMsg(fp);
		client.getPeerMgr().getPeer(peerid).getConnection().sendToPeer(
				pm.convert());
		client.getLogger().log("Piece msg has been sent");
	}

	/*
	 * Send a piece request
	 */
	public void sendPieceRequest(int peerid) {
		if (client.getPeerMgr().getPeer(peerid).isChokingMe())
			return;
		BitField bf = new BitField(client.getPeerMgr().getPeer(peerid)
				.getBitField());
		BitField lbf = new BitField(client.getFileMgr().getBitField());
		int index = lbf.randomSelectToRequest(bf);
		if (index != -1) {
			RequestMsg rm = new RequestMsg(index);
			client.getPeerMgr().getPeer(peerid).getConnection().sendToPeer(
					rm.convert());
		}
	}
	
	/*
	 * Checks whether the whole file is obtained or not
	 */
	public void checkComplete() {
		client.getLogger().log("Check complete");

		if (!client.getFileMgr().getBitField().hasAll()) {
			client.getLogger().log("Incomplete file");
			return;
		}
		else
		{
			client.getLogger().log("Complete file available");
		}

		for (int pid : client.getPeerMgr().getPeers()) {
			if (!client.getPeerMgr().getPeer(pid).getBitField().hasAll()) {
				client.getLogger().log("Peer" + pid
						+ "doesn't have the complete file");
				return;
			}
			else
			{
				client.getLogger().log("Peer" + pid
						+ "has the complete file");				
			}
		}
		client.getTimer().stopRequest();
		client.getSocketMgr().requestToStop();
		
		for (int pid : client.getPeerMgr().getPeers()) {
			client.getLogger().log(client.getPeerMgr().getPeer(pid) + "Closing connection");
			try{
				client.getPeerMgr().getPeer(pid).getConnection().close();
			}catch(Exception e) {
			}			
		}
	}
}
