import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Provides the logging functionality for dumping necessary execution messages
 */
public class Logger {
	private String logFileName = null;
	private PrintWriter out = null;
	private boolean isOpen = false;

	/*
	 * Creating separate log files for each peer
	 */
	public Logger(Client client) {
		logFileName = "log_peer_" + client.getPeerId() + ".log";
		try {
			out = new PrintWriter(new FileOutputStream(logFileName), true);
			isOpen = true;
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/*
	 * Method that performs the actual logging
	 */
	public synchronized void log(String msg) {
		if (!isOpen) {
			System.out.println("Error: Cannot open log file");
		}
		out.println("[" + new Date() + "]: " + msg);
	}

	/*
	 * Closing the log file
	 */
	public void close() {
		if (!isOpen)
			return;
		out.close();
		isOpen = false;
	}
}
