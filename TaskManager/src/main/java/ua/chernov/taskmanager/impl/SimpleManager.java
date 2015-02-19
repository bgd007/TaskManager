package ua.chernov.taskmanager.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.chernov.taskmanager.Manager;
import ua.chernov.taskmanager.Notifier;
import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;
import ua.chernov.taskmanager.transport.Packets;


public class SimpleManager implements Manager {
	public static final String TASKLIST_STOREGEFILENAME = "TaskList.txt";
	private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

	private static final Logger log = LogManager.getLogger(SimpleManager.class);

	private Map<Subscriber, String> subscribers = new HashMap<Subscriber, String>();

	private TaskList taskList = new MapTaskList();
	private Notifier notifier = new SimpleNotifier(this);

	public SimpleManager() {
		loadTaskListFromDisk();
	}

	public void subscribe(Subscriber subscriber) {
		synchronized (subscribers) {
			subscribers.put(subscriber, subscriber.getNick());
			log.info("Add subscriber. Subscribers.size=" + subscribers.size());
		}
	}

	public void unsubscribe(Subscriber subscriber) {
		synchronized (subscribers) {
			subscribers.remove(subscriber);
			log.info("Remove subscriber. Subscribers.size="
					+ subscribers.size());
		}
	}

	@Override
	public TaskList getTaskList() {
		return taskList;
	}

	@Override
	public void giveTaskList(Subscriber subscriber) {
		TaskList list = getTaskList();
		subscriber.receiveTaskList(list);
	}

	@Override
	public void addTask(Subscriber subscriber, Task task) {
		synchronized (taskList) {
			taskList.add(task);
		}

		subscriber.receiveSaveTaskOk();
	}

	@Override
	public void editTask(Subscriber subscriber, Task task) {
		synchronized (taskList) {
			taskList.edit(task);
		}
		
		subscriber.receiveSaveTaskOk();
	}

	@Override
	public void getTaskById(Subscriber subscriber, Object id, Object cardState) {
		synchronized (taskList) {
			Task task = taskList.getTaskById(id);
			subscriber.receiveTaskById(task, cardState);
		}
	}

	@Override
	public void deleteTask(Subscriber subscriber, Task task) {
		synchronized (taskList) {
			taskList.removeById(task.getId());
		}
		
		subscriber.receiveDeleteTaskOk();
	}

	@Override
	public String getTaskListStorageFileName() {
		return TASKLIST_STOREGEFILENAME;
	}

	@Override
	public void storeTaskListToDisk() {
		String fileName = getTaskListStorageFileName();
		synchronized (taskList) {
			taskList.storeToDisk(fileName);
		}
	}

	@Override
	public void loadTaskListFromDisk() {
		String fileName = getTaskListStorageFileName();
		synchronized (taskList) {
			taskList.loadFromDisk(fileName);
		}
	}

	@Override
	public void notifyLater(Subscriber subscriber, Object taskId,
			Date notifyLaterDate) {
		synchronized (taskList) {
			Task task = taskList.getTaskById(taskId);
			if (task == null)
				throw new RuntimeException("Task not found.");
			task.setNotifyDate(notifyLaterDate);
		}
		
		subscriber.receiveNotifyLaterOk();
	}

	@Override
	public void getNewTask(Subscriber subscriber) {
		Task task = new ScheduledTask();
		subscriber.receiveNewTask(task);
	}

	@Override
	public void notify(Subscriber sub, Task task) {
		sub.receiveNotify(task);
	}

	@Override
	public void notifyAll(Task task) {
		for (Subscriber sub : subscribers.keySet())
			notify(sub, task);
	}

	@Override
	public String getDefaultDateFormat() {
		return DATE_FORMAT;
	}

	@Override
	public void sendXML() {
	}

}
