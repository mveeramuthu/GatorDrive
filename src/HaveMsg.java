/**
 * ‘have’ messages have a payload that contains a 4-byte piece index field	
 */
public class HaveMsg extends ActualMessages{
	
	int type=4;
	int index;
	private int defLength;
	private int totalLen=9;
	
	public int getIndex(){
		return index;
	}
	public HaveMsg(int index) {
		this.index=index;
		defLength=5;
		}
	public HaveMsg(byte[] b){
		index=BytetoInt(b, 5);
		defLength=5;
	}
	protected int BytetoInt(byte[] hshake, int offset) {
		int bytes = 0;
		for (int i = 0; i < 4; i++)
			bytes = (bytes << 8) | (hshake[i + offset] & 0xFF);
		return bytes;
	}
	public byte[] intToByte(int peerId)
	{
		byte[] st2= new byte[4];
		st2[0] = (byte) ((peerId & 0xff000000) >> 24);
		st2[1] = (byte) ((peerId & 0xff0000) >> 16);
		st2[2] = (byte) ((peerId & 0xff00) >> 8);
		st2[3] = (byte) (peerId & 0xff);
		return st2;
	}

	public byte[] convert(){
	int offset = 0;
	byte[] bytelength = intToByte(defLength);
	byte[] byteType=new byte[1];
	byteType[0]=MessageUtils.convertIntToByte(type);
	byte[] bIndex=intToByte(index);
	byte[] output=new byte[totalLen];
	System.arraycopy(bytelength, 0, output, offset, 4);
	offset+=4;
	System.arraycopy(byteType, 0, output, offset, 1);
	offset+=1;
	System.arraycopy(bIndex, 0, output, offset, 4);	
	return output;
}
	
}