import java.io.IOException;

/**
 * The handshake consists of three parts: handshake header, zero bits, and peer ID. The
 * length of the handshake message is 32 bytes. The handshake header is 18-byte string
 * ‘CEN5501C2008SPRING’, which is followed by 10-byte zero bits, which is followed by
 * 4-byte peer ID which is the integer representation of the peer ID.
 */
public class HandshakeMsg {

	int peerID;
	final String message = "CEN5501C2008SPRING";

	public HandshakeMsg(int peerID) {
		this.peerID = peerID;

	}

	protected int BytetoInt(byte[] hshake, int offset) {
		int bytes = 0;
		for (int i = 0; i < 4; i++)
			bytes = (bytes << 8) | (hshake[i + offset] & 0xFF);
		return bytes;
	}

	public byte[] intToByte(int peerId) {
		byte[] st2 = new byte[4];

		st2[0] = (byte) ((peerId & 0xff000000) >> 24);
		st2[1] = (byte) ((peerId & 0xff0000) >> 16);
		st2[2] = (byte) ((peerId & 0xff00) >> 8);
		st2[3] = (byte) (peerId & 0xff);
		return st2;
	}
	
	public HandshakeMsg(byte[] buffer) {
		peerID = BytetoInt(buffer, 28);
	}

	public byte[] convert() throws IOException {
		byte[] result = new byte[32];
		byte[] header = message.getBytes("US-ASCII");
		byte[] bpeer = intToByte(peerID);
		System.arraycopy(header, 0, result, 0, 18);
		System.arraycopy(bpeer, 0, result, 28, 4);
		return result;
	}

}
