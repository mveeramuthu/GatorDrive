import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Manages the list of all interacting peers for choking, unchoking, interested and not interested
 */
public class PeerMgr {

	private Client client;
	private int numberOfPreferredNeighbors;
	private Map<Integer, Peer> peers = null;
	private List<Integer> peerIds = null;
	private Set<Integer> interestedPeers = null;
	private Set<Integer> amInterestedPeers = null;
	private Set<Integer> chokedByMe = null;
	private Set<Integer> chokingMe = null;

	/*
	 * Populates peer info
	 */
	public PeerMgr(Client client) {
		this.client = client;
		peers = new HashMap<Integer, Peer>();
		peerIds = new ArrayList<Integer>();
		chokedByMe = new HashSet<Integer>();
		chokingMe = new HashSet<Integer>();
		interestedPeers = new HashSet<Integer>();
		amInterestedPeers = new HashSet<Integer>();
		this.numberOfPreferredNeighbors = CommonConfig.getInstance()
				.getNumberOfPreferredNeighbors();
	}

	/*
	 * Peer joins the network
	 */
	public void PeerJoin(Peer p) {
		peerIds.add(p.getPeerInfo().getPeerID());
		peers.put(p.getPeerInfo().getPeerID(), p);
		chokedByMe.add(p.getPeerInfo().getPeerID());
		chokingMe.add(p.getPeerInfo().getPeerID());
		client.getLogger().log("Peer " + p.getPeerInfo().getPeerID() + " added");
	}

	/*
	 * Get all peer IDs
	 */
	public List<Integer> getPeers() {
		return peerIds;
	}

	/*
	 * Information of the peer being choked by current peer
	 */
	public void chokePeer(int peerId) {
		client.getLogger().log("Choke peer" + peerId);
		chokedByMe.add(peerId);
		getPeer(peerId).choke();
	}

	/*
	 * Information of the peer being unchoked by current peer
	 */
	public void unchokePeer(int peerId) {
		client.getLogger().log("Unchoke peer" + peerId);
		chokedByMe.remove(peerId);
		getPeer(peerId).unChoke();
	}

	/*
	 * Information of the peer choking the current peer
	 */
	public void setChokingMe(int peerid, boolean isChoking) {
		if (isChoking) {
			chokingMe.add(peerid);
		} else {
			chokingMe.remove(peerid);
		}
		getPeer(peerid).setChokingMe(isChoking);
		
		if(isChoking)
			client.getLogger().log("Choked by " + peerid);
		else		
			client.getLogger().log("Unchoked by " + peerid);
	}

	/*
	 * Information of the peer interested to communicate with current peer
	 */
	public void setPeerInterestedToMe(int pid, boolean interested) {
		if (interested) {
			interestedPeers.add(getPeer(pid).getPeerInfo().getPeerID());
		} else {
			interestedPeers.remove(getPeer(pid).getPeerInfo().getPeerID());
		}
		getPeer(pid).setIsInterested(interested);
	}

	/*
	 * Information of the peer to which current peer is interested in communicating
	 */
	public void setAmInterested(int pid, boolean amInterested) {
		if (amInterested) {
			amInterestedPeers.add(getPeer(pid).getPeerInfo().getPeerID());
		} else {
			amInterestedPeers.remove(getPeer(pid).getPeerInfo().getPeerID());
		}
		getPeer(pid).setAmInterested(amInterested);
	}

	/*
	 * Returns peer ID
	 */
	public Peer getPeer(int peerId) {
		return peers.get(peerId);
	}

	/*
	 * Returns peers interested in current peer
	 */
	public Set<Integer> getPeersInterestedToMe() {
		return interestedPeers;
	}

	public void resetPeersPieces() {
		for (int id : getPeers()) {
			getPeer(id).resetPieces();
		}
	}

	/*
	 * Determines the peers to unchoke
	 */
	public Set<Peer> calculatePeersToUnchoke() {
		Set<Integer> intrestedPeers = getPeersInterestedToMe();
		TreeSet<Peer> rs = new TreeSet<Peer>(new Comparator<Peer>() {
			public int compare(Peer p1, Peer p2) {
				if (p1.getPrevPieces() < p2
						.getPrevPieces())
					return -1;
				else
					return 1;
			}
		});
		client.getLogger().log("Number of peers interested: "
				+ interestedPeers.size());
		for (int pid : intrestedPeers) {
			rs.add(getPeer(pid));
		}
		Iterator<Peer> it = rs.iterator();
		int skip = rs.size() - numberOfPreferredNeighbors;
		while (skip > 0) {
			--skip;
			rs.remove(it.next());
		}

		StringBuilder logMsg = new StringBuilder("Peer "
				+ client.getPeerId() + " has the preferred neighbors ");
		resetPeersPieces();
		int num = 0;
		for (Peer p : rs) {
			if (num < CommonConfig.getInstance()
					.getNumberOfPreferredNeighbors()) {
				logMsg.append(p.getPeerInfo().getPeerID());
				logMsg.append(',');
				num++;
			}
		}
		logMsg.deleteCharAt(logMsg.length() - 1);
		client.getLogger().log(logMsg.toString());
		return rs;
	}

	/*
	 * Returns the peer which has to be optimistically unchoked
	 */
	public Peer selectOptimisticUnchokingPeer() {
		int size = getPeers().size();
		if (size > 0) {
			int index = (int) (Math.random() * size);
			Peer peer = getPeer(getPeers().get(index));

			String logMsg = "Peer " + client.getPeerId()
					+ " has the optimistically unchoked neighbor "
					+ peer.getPeerInfo().getPeerID();
			client.getLogger().log(logMsg);
			return peer;
		} else {
			return null;
		}
	}

	/*
	 *  Sends a "Have" message to particular peer
	 */
	public void sendHave(HaveMsg hm, int peerId) {
		getPeer(peerId).getConnection().sendToPeer(hm.convert());
	}

	/*
	 *  Sends "Have" message to all peers
	 */
	public void sendHaveToAll(HaveMsg hm) {
		for (int peerId : peerIds) {
			client.getLogger().log("Send 'have' to peer:" + peerId);
			getPeer(peerId).getConnection().sendToPeer(hm.convert());
		}
	}

	/*
	 * Updating bit field
	 */
	public void setBitfield(BitField bitfield, int peerId) {
		Peer peer = this.getPeer(peerId);
		peer.setBitField(bitfield);
	}
}
