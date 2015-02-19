package ua.chernov.taskmanager.transport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.core.Logger;
import org.w3c.dom.Document;

import ua.chernov.taskmanager.transport.Packets.Packet;

public class PacketTransporterXML implements PacketTransporter {
	private Socket socket;

	// private ObjectInputStream input;
	// private PrintWriter output;
	XMLOutputStream output;
	XMLInputStream input;

	PacketTransporterXML(Socket socket) throws IOException {
		this.socket = socket;
		// output = new PrintWriter(socket.getOutputStream(), true);
		// BufferedReader input = new BufferedReader(
		// new InputStreamReader(socket.getInputStream()));

		// output = new DataOutputStream(socket.getOutputStream());
		output = new XMLOutputStream(socket.getOutputStream());
		input = new XMLInputStream(socket.getInputStream());
	}

	@Override
	public void send(Serializable obj) throws IOException {
		if (obj instanceof Packet) {
			Packet packet = (Packet) obj;
			StreamResult sr = new StreamResult(output);
			DOMSource ds = new DOMSource(packet.toXML());
			try {
				Transformer tf = TransformerFactory.newInstance()
						.newTransformer();

				tf.transform(ds, sr);

				output.send();
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	@Override
	public Object receive() throws IOException {
		Document request = null;
		try {
			DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();


			input.recive();

			request = docBuilder.parse(input);

		} catch (Exception e) {
			new IOException(e);
		}

		return request;
	}

	@Override
	public InputStream getInputStream() {
		return (InputStream) input;
	}

	@Override
	public OutputStream getOutputStream() {
		return (OutputStream) output;
	}

	@Override
	public void closeOutput() throws IOException {
		output.close();

	}

	@Override
	public void closeInput() throws IOException {
		input.close();
	}

	class XMLOutputStream extends ByteArrayOutputStream {

		private DataOutputStream outchannel;

		public XMLOutputStream(OutputStream outchannel) {
			super();
			this.outchannel = new DataOutputStream(outchannel);
		}

		public void send() throws IOException {
			byte[] data = toByteArray();
			outchannel.writeInt(data.length);
			outchannel.write(data);
			reset();
		}
	}

	class XMLInputStream extends ByteArrayInputStream {

		private DataInputStream inchannel;

		public XMLInputStream(InputStream inchannel) {
			super(new byte[2]);
			this.inchannel = new DataInputStream(inchannel);
		}

		public void recive() throws IOException {
			int i = inchannel.readInt();
			byte[] data = new byte[i];
			inchannel.read(data, 0, i);
			this.buf = data;
			this.count = i;
			this.mark = 0;
			this.pos = 0;
		}
	}

}
