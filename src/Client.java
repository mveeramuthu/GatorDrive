import java.io.IOException;
/*
 * Peers created over here. 
 */

public class Client {

	private int peerId;
	private FileMgr fileMgr;
	private ConnMgr conMgr;
	private PeerMgr peerMgr;
	private Logger logger;
	private Timer timer = null;
	private SocketMgr socketMgr = null;

	/*
	 * Initialization of all values
	 */
	public Client(int peerId) {
		this.peerId = peerId;
		this.conMgr = new ConnMgr(this);
		this.peerMgr = new PeerMgr(this);
		this.fileMgr = new FileMgr(peerId, PeerInfoConfig.getInstance()
				.getPeerInfo(peerId).hasFile());
		timer = new Timer(this);
		timer = new Timer(this);
	}

	/*
	 * Creating log
	 */
	public Logger getLogger() {
		return logger;
	}

	/*
	 * Starting the thread
	 */
	public void start() {

		this.logger = new Logger(this);

		try {
			socketMgr = new SocketMgr(PeerInfoConfig.getInstance()
					.getPeerInfo(peerId).getListeningPort(), this);
			Thread t = new Thread(socketMgr);
			t.setName("SocketListeningThread");
			t.start();
			timer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.conMgr.tryConnectAll();
	}

	/*
	 * Getting PeerId
	 */
	public int getPeerId() {
		return peerId;
	}

	/*
	 * Getting all time intervals
	 */
	public Timer getTimer() {
		return timer;
	}

	/*
	 * Getting instance of FileMgr
	 */
	public FileMgr getFileMgr() {
		return fileMgr;
	}

	/*
	 * Getting instance of ConnMgr
	 */
	public ConnMgr getConnMgr() {
		return conMgr;
	}

	/*
	 * Getting instance of PeerMgr
	 */
	public PeerMgr getPeerMgr() {
		return peerMgr;
	}
	
	/*
	 * Getting instance of SocketMgr
	 */
	public SocketMgr getSocketMgr() {
		return socketMgr;
	}
}
