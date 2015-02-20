package ua.chernov.taskmanager.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.chernov.taskmanager.Manager;
import ua.chernov.taskmanager.impl.SimpleManager;
import ua.chernov.taskmanager.transport.ServerTransport;

public class Server {
	private static final int PORT = 1024;
	static Manager manager;

	private static final Logger log = LogManager.getLogger(Server.class);

	public static void main(String[] args) throws IOException {
		manager = new SimpleManager();

		attachShutDownHook();

		ServerSocket server = null;
		// new ServerSocket(1024);

		// create server socket
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			log.error("Couldn't create ServerSocket at port " + PORT);
			System.exit(-1);
		}

		try {
			log.info("Start listening...");

			while (!Thread.interrupted()) {
				Socket client = server.accept();
				log.info("Connection from " + client.getRemoteSocketAddress());

				new ServerTransport(manager, client) {
					@Override
					protected void handleError(IOException e) {
						log.error("Server side error.", e);
					}
				};
			}
		} finally {
			server.close();
		}

	}

	public static void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// log.info("Server exit. Store data to disk.");
				manager.storeTaskListToDisk();
			}
		});
		log.info("Shut Down Hook Attached.");
	}
}
