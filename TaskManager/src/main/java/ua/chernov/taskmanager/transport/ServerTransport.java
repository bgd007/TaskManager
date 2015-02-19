package ua.chernov.taskmanager.transport;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.chernov.taskmanager.Manager;
import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;
import ua.chernov.taskmanager.transport.Packets.Register;

/**
 * Server-side transport.
 * 
 * Subscriber methods -> Transport packets (current thread) Transport packets ->
 * Room methods (separate thread)
 */
public class ServerTransport extends Transport implements Manager.Subscriber {
	private String nick;
	private Manager room;
	private static final Logger log = LogManager
			.getLogger(ServerTransport.class);

	public ServerTransport(Manager room, Socket socket) throws IOException {
		super(socket);
		this.room = room;
		startEventLoop();
	}

	public String getNick() {
		return nick;
	}

	@Override
	public void shutdown() {
		if (room != null) {
			room.unsubscribe(this);
			room = null;
		}
		super.shutdown();
	}

	/**
	 * Client event dispatching: transforming network packet events to Room
	 * method calls.
	 */
	@Override
	protected void dispatchEvent(Object event) {
		log.info("dispatchEvent " + ((event != null) ? event : ""));

		Manager.Subscriber thisSubscriber = ServerTransport.this;

		try {
			if (event instanceof Packets.Register) {
				Register reg = (Register) event;
				this.nick = reg.nick;
				room.subscribe(thisSubscriber);
				send(Packets.OK);
			}

			if (event.equals(Packets.GIVE_TASK_LIST)) {
				room.giveTaskList(thisSubscriber);
			}

			if (event.equals(Packets.GET_NEW_TASK)) {
				room.getNewTask(thisSubscriber);
			}

			if (event instanceof Packets.AddTask) {
				Packets.AddTask packet = (Packets.AddTask) event;
				room.addTask(thisSubscriber, packet.task);
			}

			if (event instanceof Packets.EditTask) {
				Packets.EditTask packet = (Packets.EditTask) event;
				room.editTask(thisSubscriber, packet.task);
			}

			if (event instanceof Packets.GetTaskById) {
				Packets.GetTaskById packet = (Packets.GetTaskById) event;
				room.getTaskById(thisSubscriber, packet.id, packet.cardState);
			}

			if (event instanceof Packets.DeleteTask) {
				Packets.DeleteTask packet = (Packets.DeleteTask) event;
				room.deleteTask(thisSubscriber, packet.task);
			}

			if (event instanceof Packets.NotifyLater) {
				Packets.NotifyLater packet = (Packets.NotifyLater) event;
				room.notifyLater(thisSubscriber, packet.taskId,
						packet.notifyLaterDate);
			}

			if (event instanceof Packets.Part) {
				room.unsubscribe(thisSubscriber);
				room = null;
				send(Packets.OK);
				// unsubscribe is the end
				shutdown();
			}
		} catch (RuntimeException e) {
			// on error send it as packet
			send(e);
		}
	}

	/*
	 * Server event dispatching: transforming Subscriber method calls to network
	 * packets.
	 */

	@Override
	public void receiveTaskList(TaskList taskList) {
		send(new Packets.SendTaskList(taskList));
		log.info("send Packets.SendTaskList");
	}

	@Override
	public void receiveTaskById(Task task, Object cardState) {
		Packets.SendTaskById packet = new Packets.SendTaskById(task, cardState);
//		try {
//			File file = new File("book.xml");
//			JAXBContext context = JAXBContext
//					.newInstance(Packets.SendTaskById.class);
//			Marshaller marshaller = context.createMarshaller();
//
//			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//			marshaller.marshal(packet, file);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		send(packet);
		log.info("send Packets.SendTaskById");
	}

	// @Override
	// public void addTaskResponse(Object response) {
	// send(Packets.OK);
	// log.info("send addTaskResponse OK");
	// }

	@Override
	public void receiveSaveTaskOk() {
		send(Packets.SAVE_TASK_OK);
	}

	@Override
	public void receiveDeleteTaskOk() {
		send(Packets.deleteTaskOk);
	}

	@Override
	public void receiveNotify(Task task) {
		send(new Packets.Notify(task));
		log.info("send Packets.Notify");
	}

	@Override
	public void receiveNotifyLaterOk() {
		send(Packets.NotifyLaterOk);
	}

	@Override
	public void receiveNewTask(Task task) {
		send(new Packets.SendNewTask(task));
		log.info("send Packets.SendNewTask");
	}

}
