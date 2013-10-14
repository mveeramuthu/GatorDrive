import java.util.BitSet;

public class BitField {
	private BitSet bs = null;
	private int size = 0;

	public BitField(int size) {
		bs = new BitSet(size);
		this.size = size;
	}

	public BitField(String str) {
		bs = new BitSet(str.length());
		for (int i = 0; i != str.length(); ++i) {
			if (str.charAt(i) == '1')
				bs.set(i);
		}
	}

	public BitField(int size, byte[] arr) {
		this.size = size;
		bs = new BitSet(size);
		int counter = 0;
		while (counter != size) {
			int lc = 0;
			while (lc != 8 && counter != size) {
				int index = counter / 8;
				if ((arr[index] & (byte) (1 << (7 - lc))) != 0) {
					bs.set(counter);
				}
				++lc;
				++counter;
			}
		}
	}

	public BitField(BitField bf) {
		bs = new BitSet(bf.getSize());
		bs.or(bf.bs);
		this.size = bf.getSize();
	}

	public synchronized void set(int index) {
		bs.set(index);
	}

	public synchronized void setAll() {
		for (int i = 0; i != size; ++i) {
			bs.set(i);
		}
	}

	public synchronized boolean has(int index) {
		return bs.get(index);
	}

	public synchronized boolean hasAll() {
		for (int i = 0; i != size; ++i) {
			if (!bs.get(i))
				return false;
		}
		return true;
	}

	public int getSize() {
		return size;
	}

	public synchronized int numOfOnes() {
		int ret = 0;
		for (int i = 0; i != size; i++) {
			if (!bs.get(i)) {
				ret++;
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i != size; ++i) {
			if (bs.get(i))
				sb.append("1");
			else
				sb.append(0);
		}
		return sb.toString();
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

	/*
	 *  Test whether bit field has some bits as "1"
	 */
	public synchronized boolean hasBitNotOnLocally(BitField bf) {
		BitField target = new BitField(bf);
		BitField local = new BitField(this);
		local.bs.or(target.bs);
		local.bs.xor(this.bs);
		return !local.bs.isEmpty();
	}

	public synchronized int randomSelectToRequest(BitField bf) {
		BitSet lbs = new BitSet();
		lbs.or(this.bs);
		BitSet obs = new BitSet();
		obs.or(bf.bs);
		lbs.or(obs);
		lbs.xor(this.bs);

		if (!lbs.isEmpty()) {
			for (int i = 0; i != lbs.size(); ++i) {
				if (lbs.get(i))
					return i;
			}
		}
		return -1;
	}
}
