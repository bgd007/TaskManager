package ua.chernov.taskmanager.transport;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Abstract transport with event loop and serialized packets.
 */
public abstract class Transport {
	private Socket socket;
	// private ObjectInputStream input;
	// private ObjectOutputStream output;

	// private InputStream input;
	// private OutputStream output;

	private IOException ioError;

	private static final Logger log = LogManager.getLogger(Transport.class);
	private PacketTransporter packetTransporter;

	// SimpleDateFormat ft = new SimpleDateFormat("mm:ss:SSS");

	/**
	 * Reading/dispatching event loop
	 */
	protected final Thread eventLoop = new Thread() {
		public void run() {
			while (!interrupted())
				// System.out.println("["+ft.format(new
				// Date())+"] Transport Before dispatchEvent");
				dispatchEvent(receive());
		}
	};

	public Transport(Socket socket) throws IOException {
		this.socket = socket;
		// so reading will interrupt each 1/2 second
		socket.setSoTimeout(1000);
		// output = new ObjectOutputStream(socket.getOutputStream());
		// input = new ObjectInputStream(socket.getInputStream());

		//packetTransporter = new PacketTransporterObject(socket);
		packetTransporter = new PacketTransporterXML(socket);
		// output = packetTransporter.getOutputStream();
		// input = packetTransporter.getInputStream();
	}

	public void close() throws IOException {
		stopEventLoop();
		try {
			// output.close();
			packetTransporter.closeOutput();
		} finally {
			try {
				// input.close();
				packetTransporter.closeInput();
			} finally {
				socket.close();
			}
		}
	}

	public void shutdown() {
		try {
			close();
		} catch (IOException e) {
			handleError(e);
		}
	}

	protected void send(Serializable obj) {
		try {
			// output.reset();
			// output.writeObject(obj);
			// output.flush();

			packetTransporter.send(obj);

			log.info("send " + ((obj != null) ? obj.getClass() : "null"));

		} catch (IOException e) {
			reportError(e);
		}
	}

	protected Object receive() {
		try {
			// this will check interruption flag each 1/2 second
			while (!Thread.interrupted()) {
				try {
					// Object inObject = input.readObject();
//					log.info("receive before process by packetTransporter.receive()");

					Object inObject = packetTransporter.receive();
					log.info("receive "
							+ ((inObject != null) ? inObject.getClass() : null));

					return inObject;
				} catch (SocketTimeoutException e) {
					// just to check interruption flag
					Thread.yield();
				}
			}
			// reset interruption flag
			Thread.currentThread().interrupt();
			return null;
		} catch (ClassNotFoundException | RuntimeException e) {
			// should not happen
			throw new RuntimeException(e.getMessage(), e);
			// } catch (Exception|IOException e) {
		} catch (IOException e) {
			reportError(e);
			return null;
		}
	}

	protected void reportError(IOException e) {
		shutdown();
		handleError(e);
	}

	protected void handleError(IOException e) {
		ioError = e;
	}

	public void throwErrors() throws IOException {
		if (ioError != null) {
			IOException e = ioError;
			ioError = null;
			throw e;
		}
	}

	protected abstract void dispatchEvent(Object event);

	protected void startEventLoop() {
		eventLoop.start();
	}

	protected void stopEventLoop() {
		try {
			eventLoop.interrupt();
			// waiting until it stops:
			eventLoop.join();
		} catch (InterruptedException e) {
			// if we are interrupted while waiting,
			// then reinterrupting current thread:
			Thread.currentThread().interrupt();
		}
	}
}
