import java.io.*;
import java.util.*;

public class StartRemotePeers {

	private static Vector<RemotePeerInfo> peerInfoVector;
	
	public static void getConfiguration()
	{
		String st;
		setPeerInfoVector(new Vector<RemotePeerInfo>());
		try {
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while((st = in.readLine()) != null) {
				
				 String[] tokens = st.split("\\s+");
			     getPeerInfoVector().addElement(new RemotePeerInfo(Integer.parseInt(tokens[0]), 
			    		 										tokens[1], 
			    		 										Integer.parseInt(tokens[2])));
			
			}
			
			in.close();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
	
	public static void main(String[] args) {

		try {
			StartRemotePeers.getConfiguration();
					
			// get current path
			String path = System.getProperty("user.dir");
			
			// start clients at remote hosts
			for (int i = 0; i < StartRemotePeers.getPeerInfoVector().size(); i++) {
				RemotePeerInfo pInfo = (RemotePeerInfo) StartRemotePeers.getPeerInfoVector().elementAt(i);

				System.out.println("Start remote peer " + pInfo.peerId +  " at " + pInfo.peerAddress );
				
				// *********************** IMPORTANT *************************** //
				// If your program is JAVA, use this line.
				Runtime.getRuntime().exec("ssh " + pInfo.peerAddress + " cd " + path + "; java PeerProcess " + pInfo.peerId);
				
				// If your program is C/C++, use this line instead of the above line. 
				//Runtime.getRuntime().exec("ssh " + pInfo.peerAddress + " cd " + path + "; ./peerProcess " + pInfo.peerId);
			}		
			System.out.println("Starting all remote peers has done." );

		}
		catch (Exception ex) {
			System.out.println(ex);
		}
	}


	public static Vector<RemotePeerInfo> getPeerInfoVector() {
		return peerInfoVector;
	}

	public static void setPeerInfoVector(Vector<RemotePeerInfo> peerInfoVector) {
		StartRemotePeers.peerInfoVector = peerInfoVector;
	}

}
