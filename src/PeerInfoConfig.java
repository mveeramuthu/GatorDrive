import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class PeerInfoConfig {
	private static final String CONFIG_FILENAME = "PeerInfo.cfg";
	private static PeerInfoConfig instance;
	private Map<Integer, PeerInfo> peers = new Hashtable<Integer, PeerInfo>();

	/*
	 * Initialize all attributes with values from the PeerInfo.cfg file
	 */
	private PeerInfoConfig() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					CONFIG_FILENAME));
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] tuple = line.split(" ");
				int peer_id = Integer.parseInt(tuple[0]);
				String hostname = tuple[1].toLowerCase();
				int listen_port = Integer.parseInt(tuple[2]);
				boolean has_file = (Integer.parseInt(tuple[3]) != 0);
				PeerInfo peer_info = new PeerInfo(peer_id, hostname,
						listen_port, has_file);
				peers.put(peer_id, peer_info);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Executes at the beginning to get the required instance
	 */
	static {
		getInstance();
	}

	public static PeerInfoConfig getInstance() {
		if (instance == null) {
			instance = new PeerInfoConfig();
		}
		return instance;
	}

	public PeerInfo getPeerInfo(int peerId) {
		return peers.get(peerId);
	}

	public Collection<PeerInfo> getPeerInfoCollection() {
		return peers.values();
	}
}
