import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * NumberOfPreferredNeighbors 2
 * UnchokingInterval 5
 * OptimisticUnchokingInterval 15
 * FileName TheFile.dat
 * FileSize <value provided>
 * PieceSize <value provided> 
 */

public class CommonConfig {

	private static final String CONFIG_FILENAME = "Common.cfg";
	private static CommonConfig instance;
	private int numberOfPreferredNeighbors;
	private int unchokingInterval;
	private int optimisticUnchokingInterval;
	private String fileName;
	private int fileSize;
	private int pieceSize;

	private CommonConfig(int numberOfPreferredNeighbors, int unchokingInterval,
			int optimisticUnchokingInterval, String fileName, int fileSize,
			int pieceSize) {
		this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
		this.unchokingInterval = unchokingInterval;
		this.optimisticUnchokingInterval = optimisticUnchokingInterval;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.pieceSize = pieceSize;
	}

	private CommonConfig() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					CONFIG_FILENAME));
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] pair = line.split(" ");
				String param_name = pair[0].toLowerCase();
				String param_val = pair[1];
				if ("NumberOfPreferredNeighbors".toLowerCase().equals(
						param_name)) {
					this.numberOfPreferredNeighbors = Integer
							.parseInt(param_val);
				} else if ("UnchokingInterval".toLowerCase().equals(param_name)) {
					this.unchokingInterval = Integer.parseInt(param_val);
				} else if ("OptimisticUnchokingInterval".toLowerCase().equals(
						param_name)) {
					this.optimisticUnchokingInterval = Integer
							.parseInt(param_val);
				} else if ("FileName".toLowerCase().equals(param_name)) {
					this.fileName = param_val;
				} else if ("FileSize".toLowerCase().equals(param_name)) {
					this.fileSize = Integer.parseInt(param_val);
				} else if ("PieceSize".toLowerCase().equals(param_name)) {
					this.pieceSize = Integer.parseInt(param_val);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * To be executed at the beginning
	 */
	static {
		getInstance();
	}

	public static CommonConfig getInstance() {
		if (instance == null) {
			instance = new CommonConfig();
		}
		return instance;
	}

	/*
	 * Following functions return data got from Common.cfg file
	 */
	public int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}

	public int getUnchokingInterval() {
		return unchokingInterval;
	}

	public int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	public String getFileName() {
		return fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getPieceSize() {
		return pieceSize;
	}
}
