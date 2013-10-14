import java.util.BitSet;

public class BitfieldMsg extends ActualMessages {

	int payLoad;
	byte[] bitfield;
	int type = 5; //Bitfield message is of type 5
	int size;
	private BitSet bs = null;

	public BitfieldMsg(int size, byte[] b) {
		int msgLength = bytetoInt(b, 0);
		payLoad = msgLength - 1;
		bitfield = new byte[payLoad]; 	//Payload is of type bitfield
		System.arraycopy(b, 5, bitfield, 0, payLoad);
		this.size = size;
	}

	public BitfieldMsg(BitField bField) {
		this.bitfield = bField.toBytes();
		this.payLoad = bitfield.length;
	}

	public BitField getBitField() {
		return new BitField(size, this.bitfield);
	}

	/**
	 * Convert int to byte
	 * @param peerId
	 * @return
	 */
	public byte[] intToByte(int peerId) {
		byte[] st2 = new byte[4];
		st2[0] = (byte) ((peerId & 0xff000000) >> 24);
		st2[1] = (byte) ((peerId & 0xff0000) >> 16);
		st2[2] = (byte) ((peerId & 0xff00) >> 8);
		st2[3] = (byte) (peerId & 0xff);
		return st2;
	}

	/**
	 * Convert byte to int
	 * @param bt
	 * @param offset
	 * @return
	 */
	protected int bytetoInt(byte[] bt, int offset) {
		int b = 0;
		for (int i = 0; i < 4; i++)
			b = (b << 8) | (bt[i + offset] & 0xFF);
		return b;
	}

	public byte[] convert() {
		int offset = 0;
		int totalLen = payLoad + 5;
		byte[] bytelength = intToByte(totalLen - 4);
		byte[] byteType = new byte[1];
		byteType[0] = MessageUtils.convertIntToByte(type);
		byte[] output = new byte[totalLen];
		System.arraycopy(bytelength, 0, output, offset, 4);
		offset += 4;
		System.arraycopy(byteType, 0, output, offset, 1);
		offset += 1;
		System.arraycopy(bitfield, 0, output, offset, payLoad);
		return output;
	}

	public void setAllZero(int size) {
		int Size = NoOfBytes(size);
		byte[] set = new byte[Size];
		for (int i = 0; i < Size; i++) {
			set[i] = 0;
		}
	}

	public void setAllOne(int size) {
		int Size = NoOfBytes(size);
		byte[] set = new byte[Size];

		for (int i = 0; i < Size - 1; i++) {
			set[i] = -127;
		}

		if (size % 8 == 0) {
			set[Size - 1] = -127;
		} else {
			int r = size % 8;
			byte x = (byte) r;
			x = (byte) (x << (8 - r));
			set[Size - 1] = x;
		}

	}

	public void setIndex(int size, int Index) {
		int Size = NoOfBytes(size);
		byte[] set = new byte[Size];
		for (int i = 0; i < Size; i++) {
			if (Index / 8 < i) {
				set[i] += pow(2, (Index % 8) - 1);
			}
		}
	}

	private int pow(int i, int j) {
		for (int x = 0; x < j - 1; x++) {
			i *= i;
		}
		return i;
	}

	int NoOfBytes(int size) {
		int num = 0;
		if (size % 8 == 0) {
			num = size / 8;
		} else {
			int r = size % 8;
			size -= r;
			num = size / 8;
			num++;
		}
		return num;
	}

	public synchronized byte[] toBytes() {
		int _size = (int) (Math.ceil((double) size / 8));
		int counter = 0;
		byte[] bytes = new byte[_size];
		for (int i = 0; i != _size; ++i) {
			int lc = 0;
			while (lc != 8 && counter != size) {
				if (!bs.get(counter)) {
					++lc;
					++counter;
					continue;
				}
				byte temp = 1;
				bytes[i] = (byte) (bytes[i] | (temp << (7 - lc)));
				++lc;
				++counter;
			}
		}

		return bytes;
	}
}
