package ua.chernov.taskmanager.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ua.chernov.taskmanager.helper.XmlHelper;
import ua.chernov.taskmanager.transport.Packets.Packet;

public class PacketTransporterXML implements PacketTransporter {

	private static final Logger log = LogManager
			.getLogger(PacketTransporterXML.class);
	// private ObjectInputStream input;
	// private PrintWriter output;
	XMLOutputStream output;
	XMLInputStream input;

	PacketTransporterXML(Socket socket) throws IOException {
		// this.socket = socket;
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

			try {

				Document doc = null;
				try {
					Method methodToXML = packet.getClass().getMethod("toXML");
					doc = (Document)methodToXML.invoke(obj);
				} catch (NoSuchMethodException e) {
					doc = Packet.createPacketDocument(packet.getClass().getSimpleName());
				}
								

				DOMSource ds = new DOMSource(doc);

				Transformer tf = TransformerFactory.newInstance()
						.newTransformer();

				tf.transform(ds, sr);
			} catch (TransformerException | ParserConfigurationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(e);
			}

			log.info("send XMLPacket \n" + output.toString());

			output.send();

		}
	}

	@Override
	public Object receive() throws IOException {

		Object result = null;
		try {
			DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;

			docBuilder = docBuilderFact.newDocumentBuilder();

			input.recive();

			Document doc;
			doc = docBuilder.parse(input);

			log.info("receive XMLPacket \n"
					+ XmlHelper.convertDocumentToString(doc));

			if (doc != null) {
				NodeList nodeList = doc.getChildNodes();
				if ((nodeList != null) && (nodeList.getLength() == 1)) {
					Node node = nodeList.item(0);
					String nodeName = node.getNodeName();
					log.info("XMLPacket root node " + nodeName);

					log.info("XMLPacket expected class "
							+ Packets.Register.class.getName());

					String packetClassName = Packets.class.getName() + "$"
							+ nodeName;
					Class<?> cls = Class.forName(packetClassName);
					log.info("XMLPacket class " + cls.getName());

					try {
						Method methodFromXML = cls.getMethod("fromXML",
								org.w3c.dom.Document.class);
						result = methodFromXML.invoke(null, doc);
					} catch (NoSuchMethodException e) {
						// there is no method of conversion from XML
						result = cls.newInstance();
					}

					// Constructor<?> constructor = cls
					// .getConstructor(org.w3c.dom.Document.class);
					// if (constructor != null) {
					// log.info("XMLPacket class constructor is not null.");
					// result = constructor.newInstance(doc);
					// }
				}
			}

		} catch (SAXException | ParserConfigurationException
				| SecurityException | ClassNotFoundException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			throw new RuntimeException(e);
		}

		return result;
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
