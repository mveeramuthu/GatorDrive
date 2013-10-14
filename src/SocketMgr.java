import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Manages all socket connections
 */
public class SocketMgr implements Runnable {
	private Client client;
	private ServerSocket server;

	public static final String HANDSHAKE_HEADER = "CEN5501C2008SPRING";

	public SocketMgr(int port, Client client)
			throws IOException {
		server = new ServerSocket(port);
		this.client = client;
	}

	/*
	 * Closing the connection
	 */
	public void requestToStop() {
		try {
			server.close();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public static int toInt(byte[] b) {
		int l = 0;
		l |= b[0] & 0xFF;
		l <<= 8;
		l |= b[1] & 0xFF;
		l <<= 8;
		l |= b[2] & 0xFF;
		l <<= 8;
		l |= b[3] & 0xFF;
		return l;
	}

	public void run() {
		while (true) {
			Socket connSocket = null;
			try {
				connSocket = server.accept();
				DataInputStream inDis = new DataInputStream(connSocket
						.getInputStream());
				int headerLength = 18;
				byte[] headerBytes = new byte[18];
				inDis.read(headerBytes, 0, headerLength);
				String header = new String(headerBytes, "US-ASCII");
				
				if (header.equals(HANDSHAKE_HEADER)) {
					inDis.skip(10);
					int peerId = inDis.readInt();
					PeerMgr pm = client.getPeerMgr();
					
					if (pm.getPeer(peerId) == null) {
						PeerConn pconn = new PeerConn(
								connSocket);
						String logMsg = "Peer " + client.getPeerId()
								+ " is connected from Peer " + peerId;
						client.getLogger().log(logMsg);
						Peer peer = new Peer(peerId, pconn);
						pm.PeerJoin(peer);
						HandshakeMsg myHandshake = new HandshakeMsg(
								this.client.getPeerId());
						pconn.sendToPeer(myHandshake.convert());
						Thread peerConnectionThread = new Thread(
								new PeerConnMgr(peer, client));
						String threadName = "Peer" + peerId;
						peerConnectionThread.setName(threadName);
						peerConnectionThread.start();
						BitfieldMsg bFieldMsg = new BitfieldMsg(
								this.client.getFileMgr()
										.getBitField());
						pconn.sendToPeer(bFieldMsg.convert());
					}
				}
			} catch (IOException ex) {
				client.getLogger().log("Terminating SocketMgr thread");
				return;
			}
		}
	}
}
