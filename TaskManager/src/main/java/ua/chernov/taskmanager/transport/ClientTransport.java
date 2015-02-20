package ua.chernov.taskmanager.transport;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.chernov.taskmanager.Manager;
import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;
import ua.chernov.taskmanager.transport.Packets.Part;
import ua.chernov.taskmanager.transport.Packets.Register;

/**
 * Client-side transport. Room methods processes in current thread, processes
 * server events in separate event loop.
 * 
 * Room methods -> Transport packets (current thread) Transport packets ->
 * Subscriber methods (separate thread)
 */
public class ClientTransport extends Transport implements Manager {
	private Subscriber subscriber;
	private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
	private static final Logger log = LogManager
			.getLogger(ClientTransport.class);

	public ClientTransport(Socket socket) throws IOException {
		super(socket);
	}

	/**
	 * Server event dispatching: transforming network packet events to
	 * Subscriber method calls.
	 */
	@Override
	protected void dispatchEvent(Object event) {

		log.info("dispatchEvent " + ((event != null) ? event : ""));
		if (event == null)
			return;

		if (event instanceof Packets.SendTaskList) {
			Packets.SendTaskList packet = (Packets.SendTaskList) event;
			subscriber.receiveTaskList(packet.taskList);
		}

		if (event instanceof Packets.SendNewTask) {
			Packets.SendNewTask packet = (Packets.SendNewTask) event;
			subscriber.receiveNewTask(packet.task);
		}

		if (event instanceof Packets.SendTaskById) {
			Packets.SendTaskById packet = (Packets.SendTaskById) event;
			subscriber.receiveTaskById(packet.task, packet.cardState);
		}

		if (event.equals(Packets.SAVE_TASK_OK)) {
			subscriber.receiveSaveTaskOk();
		}

		if (event.equals(Packets.DELETE_TASK_OK)) {
			subscriber.receiveDeleteTaskOk();
		}

		if (event instanceof Packets.Notify) {
			Packets.Notify packet = (Packets.Notify) event;
			subscriber.receiveNotify(packet.task);
		}

		if (event.equals(Packets.NOTIFY_LATER_OK)) { 
			subscriber.receiveNotifyLaterOk();
		}

	}

	/*
	 * Client event dispatching: transforming Room method calls to network
	 * packets.
	 */

	public void subscribe(Subscriber subscriber) {
		if (this.subscriber != null)
			throw new IllegalStateException(
					"Transport supports only one subscriber");

		send(new Register(subscriber.getNick()));

		Object res = receive();

//		if (!(res.equals(Packets.OK))) {
		if (!(res instanceof Packets.Ok)) {
			throw new RuntimeException("Unable to subscribe.", (Throwable) res);
		}

		this.subscriber = subscriber;
		startEventLoop();
	}

	public void unsubscribe(Subscriber subscriber) {
		if (subscriber != this.subscriber)
			throw new IllegalArgumentException();

		stopEventLoop();
		subscriber = null;

		send(new Part());

		Object res = receive();
		if (res instanceof RuntimeException)
			throw (RuntimeException) res;

		// unsubscribe is the end. Closing.
		shutdown();
	}

	@Override
	public TaskList getTaskList() {
		return null;
	}

	@Override
	public void giveTaskList(Subscriber subscriber) {
		send(Packets.GIVE_TASK_LIST);
	}

	@Override
	public void addTask(Subscriber subscriber, Task task) {
		send(new Packets.AddTask(task));
	}

	@Override
	public void notifyAll(Task task) {
		// TODO
	}

	@Override
	public void editTask(Subscriber subscriber, Task task) {
		send(new Packets.EditTask(task));
	}

	@Override
	public void getTaskById(Subscriber subscriber, Object id, Object cardState) {
		send(new Packets.GetTaskById(id, cardState));
	}

	@Override
	public void deleteTask(Subscriber subscriber, Task task) {
		send(new Packets.DeleteTask(task));
	}

	@Override
	public String getTaskListStorageFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeTaskListToDisk() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadTaskListFromDisk() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notify(Subscriber subscriber, Task task) {

	}

	@Override
	public void notifyLater(Subscriber subscriber, Object taskId,
			Date notifyLaterDate) {
		send(new Packets.NotifyLater(taskId, notifyLaterDate));
	}

	@Override
	public void getNewTask(Subscriber subscriber) {
		send(Packets.GET_NEW_TASK);
	}

	@Override
	public String getDefaultDateFormat() {
		return DATE_FORMAT;
	}

	@Override
	public void sendXML() {
		// Packets.Register p = new Packets.Register("nick1");
		// p.toXML();
		//
		// send(new Packets.DeleteTask(task));

	}

}
