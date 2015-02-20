package ua.chernov.taskmanager.transport;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;
import ua.chernov.taskmanager.helper.XmlHelper;

/**
 * Transport packets, sent using serialization.
 * 
 * Protocol: #handshake: Client -> Server: Register(username) Server -> Client:
 * Ok | RuntimeException #main loop: Client -> Server: Message(user,body) Server
 * -> Client: Message(user,body) | Join(user) | Part(user) #end: Client ->
 * Server: Part Serve -> Client: Ok | RuntimeException
 */
@SuppressWarnings("serial")
public interface Packets {
	/**
	 * Client -> Server: registering event
	 */

	// public static final String OK = "Ok";
	public static final String GIVE_TASK_LIST = "Give_task_list";
	public static final String GET_NEW_TASK = "Get_new_task";
	public static final String SAVE_TASK_OK = "Save_task_Ok";
	public static final String DELETE_TASK_OK = "Delete_task_Ok";
	public static final String NOTIFY_LATER_OK = "Notify_later_Ok";

	abstract class Packet implements Serializable {
		// public Packet() {
		// }

		protected static org.w3c.dom.Document createPacketDocument(
				String className) throws ParserConfigurationException {
			org.w3c.dom.Document doc = null;

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.DOMImplementation impl = builder.getDOMImplementation();
			doc = impl.createDocument(null, // namespaceURI
					null, // qualifiedName
					null); // doctype

			org.w3c.dom.Element root = doc.createElement(className);
			doc.appendChild(root);

			return doc;
		}

	}

	class Register extends Packet {
		String nick;

		public Register(String nick) {
			this.nick = nick;
		}

		public static Register fromXML(org.w3c.dom.Document doc)
				throws Exception {

			Node nodePacket = doc.getChildNodes().item(0);
			Node nodeNick = nodePacket.getFirstChild();
			String nodeValue = XmlHelper.getNodeValue(nodeNick);

			Register result = new Register(nodeValue);
			return result;
		}

		public org.w3c.dom.Document toXML() throws ParserConfigurationException {
			String rootName = Register.class.getSimpleName();
			org.w3c.dom.Document doc = createPacketDocument(rootName);

			org.w3c.dom.Node root = XmlHelper.getNode(rootName,
					doc.getChildNodes());

			org.w3c.dom.Element nodeNick = doc.createElement("nick");
			nodeNick.appendChild(doc.createTextNode(nick));
			root.appendChild(nodeNick);

			return doc;
		}

	}

	/**
	 * Server -> Client: ok result
	 */
	class Ok extends Packet {
	}

	Ok OK = new Ok();

	// class GiveTaskList implements Serializable {
	// }
	//
	// GiveTaskList giveTaskList = new GiveTaskList();

	class SendTaskList implements Serializable {
		TaskList taskList;

		public SendTaskList(TaskList taskList) {
			this.taskList = taskList;
		}
	}

	@XmlRootElement
	class SendTaskById implements Serializable {
		Task task;
		Object cardState;

		public SendTaskById(Task task, Object cardState) {
			setTask(task);
			setCardState(cardState);
		}

		@XmlElement
		void setTask(Task task) {
			this.task = task;
		}

		@XmlElement
		void setCardState(Object cardState) {
			this.cardState = cardState;
		}

	}

	// class GetNewTask implements Serializable {
	// }
	//
	// GetNewTask getNewTask = new GetNewTask();

	class SendNewTask implements Serializable {
		Task task;

		public SendNewTask(Task task) {
			this.task = task;
		}
	}

	class AddTask implements Serializable {
		Task task;

		public AddTask(Task task) {
			this.task = task;
		}
	}

	class EditTask implements Serializable {
		Task task;

		public EditTask(Task task) {
			this.task = task;
		}
	}

	class GetTaskById implements Serializable {
		Object id;
		Object cardState;

		public GetTaskById(Object id, Object cardState) {
			this.id = id;
			this.cardState = cardState;
		}
	}

	// class SaveTaskOk implements Serializable {
	// }
	//
	// SaveTaskOk saveTaskOk = new SaveTaskOk();

	class DeleteTask implements Serializable {
		Task task;

		public DeleteTask(Task task) {
			this.task = task;
		}
	}

	// class DeleteTaskOk implements Serializable {
	// }
	//
	// DeleteTaskOk deleteTaskOk = new DeleteTaskOk();

	class Notify implements Serializable {
		Task task;

		public Notify(Task task) {
			this.task = task;
		}
	}

	class NotifyLater implements Serializable {
		Object taskId;
		Date notifyLaterDate;

		public NotifyLater(Object taskId, Date notifyLaterDate) {
			this.taskId = taskId;
			this.notifyLaterDate = notifyLaterDate;
		}
	}

	// class NotifyLaterOk implements Serializable {
	// }
	//
	// NotifyLaterOk NotifyLaterOk = new NotifyLaterOk();

	/**
	 * Server -> Client: some user joined event
	 */
	class Join implements Serializable {
		String nick;

		public Join(String nick) {
			this.nick = nick;
		}
	}

	/**
	 * Server -> Client: some user parted event Client -> Server: current user
	 * parted
	 */
	class Part extends Packet {
	}

}
