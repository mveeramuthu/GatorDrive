import java.io.IOException;
import java.net.UnknownHostException;

/**
 * This class is the entry-point to the project
 * StartRemotePeers module will run this java file to start each peer
 */

public class PeerProcess {
	
	/**
	 * The main method is the entry point to the project.
	 * It gets the peerID of the peer to be started first as the argument
	 * as command line argument (args[0])
	 * @param args
	 */
	public static void main(String[] args) {
		int peerId = Integer.parseInt(args[0]);
		Client client = new Client(peerId);
		client.start();
	}
}
