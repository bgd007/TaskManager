package ua.chernov.taskmanager.transport;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;

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
	class Packet implements Serializable {
		
		org.w3c.dom.Document toXML() {
			return null;
		}

	}

	class Register extends Packet {
		String nick;

		public Register(String nick) {
			this.nick = nick;
		}

		@Override
		public org.w3c.dom.Document toXML() {
			org.w3c.dom.Document doc = null;
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				org.w3c.dom.DOMImplementation impl = builder
						.getDOMImplementation();
				doc = impl.createDocument(null, // namespaceURI
						null, // qualifiedName
						null); // doctype

				// org.w3c.dom.Element root =
				// doc.createElement(this.getClass().getName());
				// String className = this.getClass().getName();
				String className = Register.class.getSimpleName();
				org.w3c.dom.Element root = doc.createElement(className);
				doc.appendChild(root);

				org.w3c.dom.Element nodeNick = doc.createElement("nick");
				nodeNick.appendChild(doc.createTextNode(nick));
				root.appendChild(nodeNick);

				// e1.setNodeValue(nick);

				// org.w3c.dom.Element e2 = doc.createElement("java");
				// e2.setAttribute("url", "http://www.quizful.net");
				// e1.appendChild(e2);

				// output DOM XML to console
				Transformer transformer = TransformerFactory.newInstance()
						.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				javax.xml.transform.stream.StreamResult console = new StreamResult(
						System.out);
				transformer.transform(source, console);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return doc;
		}
	}

	/**
	 * Server -> Client: ok result
	 */
	class Ok implements Serializable {
	}

	Ok OK = new Ok();

	class GiveTaskList implements Serializable {
	}

	GiveTaskList giveTaskList = new GiveTaskList();

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

	class GetNewTask implements Serializable {
	}

	GetNewTask getNewTask = new GetNewTask();

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

	class SaveTaskOk implements Serializable {
	}

	SaveTaskOk saveTaskOk = new SaveTaskOk();

	class DeleteTask implements Serializable {
		Task task;

		public DeleteTask(Task task) {
			this.task = task;
		}
	}

	class DeleteTaskOk implements Serializable {
	}

	DeleteTaskOk deleteTaskOk = new DeleteTaskOk();

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

	class NotifyLaterOk implements Serializable {
	}

	NotifyLaterOk NotifyLaterOk = new NotifyLaterOk();

	//
	// class AddNotifyLater implements Serializable {
	// Object taskId;
	// Date notifyLaterDate;
	//
	// public AddNotifyLater(Object taskId, Date notifyLaterDate) {
	// this.taskId = taskId;
	// this.notifyLaterDate = notifyLaterDate;
	// }
	// }

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
	class Part implements Serializable {
		String nick;

		public Part(String nick) {
			this.nick = nick;
		}

		public Part() {
		}
	}

	class XmlConverter {
		public Object fromXML() {
			return null;
		}

	}
}