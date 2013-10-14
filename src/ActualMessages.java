public class ActualMessages extends MessageUtils {	

	int length; //4 byte
	byte type; //1 byte
	String payload; //Any size

	public static ActualMessages getActualMsg(byte[] actualMsg) {

		/*
		 * since message is length 4, type will be in the actualMsg[4]
		 */
		int type = actualMsg[4]; 

		switch (type) {
		
		case 0:
			return new ChokeMsg(actualMsg); //returns ChokeMsg

		case 1:
			return new UnchokeMsg(actualMsg); //returns UnchokeMsg
			
		case 2:
			return new InterestedMsg(actualMsg);  //returns InterestedMsg

		case 3:
			return new NotInterestedMsg(actualMsg);  //returns NotInterestedMsg
			
		case 4:
			return new HaveMsg(actualMsg); //returns HaveMsg

		case 5:
			int fileSize = CommonConfig.getInstance().getFileSize();
			int pieceSize = CommonConfig.getInstance().getPieceSize();
			int size;
			size = (int) (Math.ceil((float) fileSize / pieceSize)); //number of chunks
			return new BitfieldMsg(size, actualMsg); //returns Bit field according to file size and piece size from CommonConfig

		case 6:
			return new RequestMsg(actualMsg); //returns RequestMsg

		case 7:
			return new PieceMsg(actualMsg); //returns ActualMsg

		default:
			System.out.println("Invalid Message");
			return null;
		}
	}

}
