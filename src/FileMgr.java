import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * Managing the file chunks
 */
public class FileMgr {
	private int pieceSize = 0;
	private String directory = null;
	private String fileName = null;
	private int fileSize = 0;
	private File file = null;
	private BitField bitField = null;
	
	public BitField getBitField() {
		return bitField;
	}

	public FileMgr(int peerid, boolean has) {
		directory = "peer_" + peerid + "/";
		pieceSize = CommonConfig.getInstance().getPieceSize();
		fileName = CommonConfig.getInstance().getFileName();
		fileSize = CommonConfig.getInstance().getFileSize();
		File parent = new File(directory);

		/*
		 * Creating the peer_<peerId> folders
		 */
		if (!parent.exists()) {
			parent.mkdirs();
		}

		file = new File(directory + fileName);
		if (!file.exists()) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				byte[] initContent = new byte[fileSize];
				fos.write(initContent);
				fos.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
		bitField = new BitField((int) (Math.ceil((float) fileSize / pieceSize)));
		if (has) {
			bitField.setAll();
		}
	}
	
	/*
	 * get the file chunks
	 */
	public FilePiece get(int index) {
		try {
			FileInputStream fis = new FileInputStream(file);
			int skip = pieceSize * index;
			fis.skip(skip);
			int contentSize = pieceSize;
			if (fileSize - skip < pieceSize)
				contentSize = fileSize - skip;
			byte[] content = new byte[contentSize];
			fis.read(content);
			fis.close();
			return new FilePiece(content, index);
		} catch (FileNotFoundException fnfe) {
			return null;
		} catch (IOException ie) {
			return null;
		}
	}

	/*
	 * Storing the file chunks
	 */
	public void store(FilePiece fp) {
		int skip = pieceSize * fp.getIndex();
		RandomAccessFile fos = null;
		try {
			fos = new RandomAccessFile(file, "rw");
			fos.seek(skip);
			fos.write(fp.getContent());
			fos.close();
			bitField.set(fp.getIndex());
			if (bitField.hasAll()) {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
