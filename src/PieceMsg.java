public class PieceMsg extends ActualMessages {
/*
 * ‘piece’ messages have a payload which consists of a 4-byte piece index field and the content of the piece.
 */
	int index;
	int pieceLength;
	byte[] piece;
	final int type = 7;

	public PieceMsg(int index, int pieceLength, byte[] piece) {
		this.index = index;
		this.pieceLength = pieceLength;
		this.piece = new byte[this.pieceLength];
		System.arraycopy(piece, 0, this.piece, 0, pieceLength);
	}

	public PieceMsg(FilePiece fp) {
		this.index = fp.getIndex();
		piece = fp.getContent();
		pieceLength = piece.length;
	}

	public PieceMsg(byte[] buffer) {
		int msgLength = bytetoInt(buffer, 0);
		pieceLength = msgLength - 5;
		piece = new byte[pieceLength];
		int offset = 5;
		index = bytetoInt(buffer, offset);
		offset += 4;
		System.arraycopy(buffer, offset, piece, 0, pieceLength);
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
	
	public FilePiece getPiece() {
		return new FilePiece(piece, index);
	}

	public byte[] convert() {
		int offset = 0;
		int TotalLen = pieceLength + 9;
		byte[] result = new byte[TotalLen];
		byte[] blen = intToByte(pieceLength + 5);
		byte[] bindex = intToByte(index);
		
		for (int i = 0; i != bindex.length; ++i) {
			System.out.println(bindex[i]);
		}
		
		byte[] btype = new byte[1];
		btype[0] = MessageUtils.convertIntToByte(type);
		System.arraycopy(blen, 0, result, offset, 4);
		offset += 4;

		System.arraycopy(btype, 0, result, offset, 1);
		offset += 1;

		System.arraycopy(bindex, 0, result, offset, 4);
		offset += 4;

		System.arraycopy(piece, 0, result, offset, pieceLength);
		
		return result;
	}

}