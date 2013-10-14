import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;

/*
 * Establishes connections to the peers, fields of peers are set
 */
public class ConnMgr {

	private Client client;

	public ConnMgr(Client client) {
		this.client = client;
	}

	public void ConnectPeer(PeerInfo peerInfo) {
		Socket socket;
		try {
			socket = createSocket(peerInfo);
			OutputStream op = socket.getOutputStream();
			HandshakeMsg msg = new HandshakeMsg(this.client.getPeerId());
			op.write(msg.convert());

			byte[] response = new byte[32];
			socket.getInputStream().read(response);

			if (this.client.getPeerMgr().getPeer(peerInfo.getPeerID()) == null) {
				PeerConn connection = new PeerConn(socket);
				String logMsg = "Peer " + client.getPeerId()
						+ " makes a connection to Peer " + peerInfo.getPeerID();
				client.getLogger().log(logMsg);
				Peer peer = new Peer(peerInfo.getPeerID(), connection);
				client.getPeerMgr().PeerJoin(peer);
				Thread peerConnectionThread = new Thread(new PeerConnMgr(peer,
						client));
				String threadName = "Peer" + peerInfo.getPeerID();
				peerConnectionThread.setName(threadName);
				peerConnectionThread.start();
				BitfieldMsg bm = new BitfieldMsg(this.client.getFileMgr()
						.getBitField());
				connection.sendToPeer(bm.convert());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			client.getLogger().log(
					"Peer " + peerInfo.getPeerID() + " is down");
		}
	}

	private Socket createSocket(PeerInfo peerInfo) throws UnknownHostException,
			IOException {
		return new Socket(peerInfo.getHostname(), peerInfo.getListeningPort());
	}

	public void tryConnectAll() {
		Collection<PeerInfo> peerInfo = PeerInfoConfig.getInstance()
				.getPeerInfoCollection();
		for (PeerInfo info : peerInfo) {
			if (info.getPeerID() != this.client.getPeerId()) {
				ConnectPeer(info);
			}
		}
	}

}
