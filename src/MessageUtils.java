public class MessageUtils {
	
	public static byte convertIntToByte(int id){
		return ((Integer)id).byteValue();		
	}
	
	public int convertByteToInt(byte byt){
		Byte b=byt;
		return b.intValue();		
	}	
}