public class RequestMsg extends ActualMessages {
	/*
	 * ‘request’ messages have a payload which consists of a 4-byte piece index field. Note 
	 * that ‘request’ message payload defined here is different from that of BitTorrent. We
	 * don’t divide a piece into smaller subpieces
	 */
	int type = 6;
	int defLength;
	private int index;

	public int getIndex() {
		return index;
	}

	public RequestMsg(int ind) {
		index = ind;
		defLength = 5;
	}

	public RequestMsg(byte[] b) {
		defLength = 5;
		index = bytetoInt(b, 5);
	}

	public byte[] intToByte(int peerId) {
		byte[] st2 = new byte[4];
		st2[0] = (byte) ((peerId & 0xff000000) >> 24);
		st2[1] = (byte) ((peerId & 0xff0000) >> 16);
		st2[2] = (byte) ((peerId & 0xff00) >> 8);
		st2[3] = (byte) (peerId & 0xff);
		return st2;
	}

	protected int bytetoInt(byte[] byt, int offset) {
		int b = 0;
		for (int i = 0; i < 4; i++)
			b = (b << 8) | (byt[i + offset] & 0xFF);
		return b;
	}

	public byte[] convert() {
		int offset = 0;
		int totalLen = 9;
		byte[] bytelength = intToByte(defLength);
		byte[] byteType = new byte[1];
		byteType[0] = MessageUtils.convertIntToByte(type);
		byte[] byteIndex = intToByte(index);
		byte[] output = new byte[totalLen];
		System.arraycopy(bytelength, 0, output, offset, 4);
		offset += 4;
		System.arraycopy(byteType, 0, output, offset, 1);
		offset += 1;
		System.arraycopy(byteIndex, 0, output, offset, 4);
		return output;
	}
}