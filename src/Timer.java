import java.util.Iterator;
import java.util.Set;

/**
 * Manages timing and delay attributes with respect to choking 
 * and unchoking of peers read from common.cfg 
 */
public class Timer {

	private boolean stop = false;
	private int unchokingTime;
	private int optimisticUnchokingTime;
	private volatile long lastUnchokingTime;
	private volatile long lastOptimisticUnchokingTime;
	private PeerMgr peerMgr = null;
	private Client client;

	public Timer(Client client) {
		this.client = client;
	}

	public synchronized void stopRequest() {
		stop = true;
	}

	public synchronized boolean isStopRequested() {
		return stop;
	}
	
	public void start() {
		this.peerMgr = client.getPeerMgr();
		this.unchokingTime = CommonConfig.getInstance()
				.getUnchokingInterval();
		this.optimisticUnchokingTime = CommonConfig.getInstance()
				.getOptimisticUnchokingInterval();
		this.lastOptimisticUnchokingTime = System.currentTimeMillis();
		this.lastUnchokingTime = lastOptimisticUnchokingTime;
		Thread unchokingThread = new Thread(new UnchokingThread());
		unchokingThread.setName("UnchokingThread");
		Thread optimisticUnchockingThread = new Thread(new OptimisticUnchokingThread());
		optimisticUnchockingThread.setName("OptimisticUnchokingThread");
		unchokingThread.start();
		optimisticUnchockingThread.start();
	}
	
	class UnchokingThread implements Runnable {
		
		public void run() {
			
			while (!isStopRequested()) {
				
				if (System.currentTimeMillis() - lastUnchokingTime >= unchokingTime * 1000) {
					
					Set<Peer> uns = peerMgr.calculatePeersToUnchoke();
					Iterator<Peer> it = uns.iterator();
					
					while (it.hasNext()) {
						Peer p = it.next();
						peerMgr.unchokePeer(p.getPeerInfo().getPeerID());
					}
					
					for (int pid : peerMgr.getPeers()) {
						boolean iscontained = false;
						
						for (Peer p : uns) {
							if (p.getPeerInfo().getPeerID() == peerMgr.getPeer(pid)
									.getPeerInfo().getPeerID()) {
								iscontained = true;
								break;
							}
						}
						
						if (!iscontained) {
							peerMgr.chokePeer(peerMgr.getPeer(pid).getPeerInfo()
									.getPeerID());
						}
					}
					
					lastUnchokingTime = System.currentTimeMillis();
					
				} else {
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ite) {
						ite.printStackTrace();
					}
				}
			}
		}
	}

	class OptimisticUnchokingThread implements Runnable {
		
		public void run() {
			
			while (!isStopRequested()) {
				
				if (System.currentTimeMillis() - lastOptimisticUnchokingTime >= optimisticUnchokingTime * 1000) {
					
					Peer p = peerMgr.selectOptimisticUnchokingPeer();
					
					if (p != null)
						peerMgr.unchokePeer(p.getPeerInfo().getPeerID());
					
					lastOptimisticUnchokingTime = System.currentTimeMillis();
					
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
