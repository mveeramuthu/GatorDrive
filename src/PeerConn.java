import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Manages each peer's socket connection
 */

public class PeerConn {
	private Socket socket;
	private SocketAddress endpoint;
	private OutputStream output = null;
	private InputStream in;

	public PeerConn(Socket peerSocket) {
		this.socket = peerSocket;
		this.endpoint = peerSocket.getRemoteSocketAddress();
		try {
			output = socket.getOutputStream();
			in = socket.getInputStream();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public byte[] intToByte(int peerId) {
		byte[] st2 = new byte[4];
		st2[0] = (byte) ((peerId & 0xff000000) >> 24);
		st2[1] = (byte) ((peerId & 0xff0000) >> 16);
		st2[2] = (byte) ((peerId & 0xff00) >> 8);
		st2[3] = (byte) (peerId & 0xff);
		return st2;
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public byte[] readMessage(Socket socket) throws IOException {
		byte[] ret = null;
		byte[] blen = new byte[4];
		in.read(blen, 0, 4);
		int msgLength = 0;
		for (int j = 0; j < 4; j++)
			msgLength = (msgLength << 8) | (blen[j] & 0xFF);
		ret = new byte[msgLength + 4];
		byte[] length_bytes = intToByte(msgLength);
		System.arraycopy(length_bytes, 0, ret, 0, 4);
		int counter = msgLength;
		int off = 4;
		while (counter != 0) {
			int len = in.read(ret, off, msgLength - off + 4);
			off += len;
			counter -= len;
		}
		return ret;
	}

	public byte[] readMessage() throws IOException {
		return readMessage(this.socket);
	}

	public synchronized void sendToPeer(byte[] message) {
		try {
			output.write(message, 0, message.length);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
