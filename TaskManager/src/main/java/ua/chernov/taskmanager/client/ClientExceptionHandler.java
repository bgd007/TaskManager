package ua.chernov.taskmanager.client;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientExceptionHandler implements Thread.UncaughtExceptionHandler {
	private static final Logger log = LogManager.getLogger(ClientExceptionHandler.class);
	
	public void uncaughtException(Thread t, Throwable e) {
		printConsole(t, e);
		showException(t, e);
	}

	static void printConsole(Thread t, Throwable e) {
//		String eType = e.getClass().getName();
//		System.out.println("Client " + eType + ": " + e.getMessage());
//		System.out.println(getStackTrace(e));
		log.error("UncaughtExceptionHandler error", e);
	}

	static void showException(Throwable e) {		
		showException(Thread.currentThread(), e);	
	}
	
	static void showException(Thread t, Throwable e) {
		String eType = e.getClass().getName();
		String msg = eType + "\n" + e.getMessage();
		JOptionPane.showMessageDialog(null, msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	static String getStackTrace(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		return stringWriter.toString();
	}

}
