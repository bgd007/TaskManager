package ua.chernov.taskmanager.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.chernov.taskmanager.Manager;
import ua.chernov.taskmanager.Notifier;
import ua.chernov.taskmanager.Task;

public class SimpleNotifier implements Notifier {
	final static private long SLEEP_INTERVAL = 2000000;
	Manager manager;
	Date prevTimeNotifyLoop = new Date();

	private static final Logger log = LogManager.getLogger(SimpleNotifier.class);

	// private class NotifyLaterItem {
	// Manager.Subscriber subscriber;
	// Object taskId;
	//
	// NotifyLaterItem(Manager.Subscriber subscriber, Object taskId) {
	// this.subscriber = subscriber;
	// this.taskId = taskId;
	// }
	// }

	// TreeMap<Date, NotifyLaterItem> mapNotifyLater = new TreeMap<Date,
	// NotifyLaterItem>();

	SimpleNotifier(Manager manager) {
		this.manager = manager;
		startNotifyLoop();
	}

	protected final Thread NotifyLoop = new Thread() {
		public void run() {
			while (!interrupted()) {
				try {
					Thread.sleep(SLEEP_INTERVAL);

					dispatchNotifyLoop();
				} catch (Exception e) {
					throw new RuntimeException("Error in dispatchNotifyLoop.",
							e);
				}
			}
		}
	};

	protected void dispatchNotifyLoop() throws InterruptedException {
		Date fromDate = prevTimeNotifyLoop;
		Date toDate = new Date();

		SimpleDateFormat df = new SimpleDateFormat(
				manager.getDefaultDateFormat());
		log.info("dispatchNotifyLoop from [" + df.format(fromDate) + "] to ["
				+ df.format(toDate) + "]");
		// System.out.printf("dispatchNotifyLoop from [%s] to [%s]%n", fromDate,
		// toDate);

		SortedMap<Date, Task> incomingTask = manager.getTaskList().incoming(
				fromDate, toDate);
		for (Task task : incomingTask.values()) {
			notifyAll(task);
		}

		// synchronized (mapNotifyLater) {
		// Iterator iterator = mapNotifyLater.entrySet().iterator();
		//
		// while (iterator.hasNext()) {
		// Map.Entry<Date, NotifyLaterItem> entry = (Map.Entry<Date,
		// NotifyLaterItem>) iterator
		// .next();
		// Date notifyDate = entry.getKey();
		// NotifyLaterItem item = entry.getValue();
		// if (notifyDate.before(toDate)) {
		// Task task = manager.getTaskList().getTaskById(item.taskId);
		// if (task != null)
		// manager.notify(item.subscriber, task);
		// iterator.remove();
		// } else {
		// break;
		// }
		// }
		//
		// }

		// notify(Subscriber sub, Task task)

		prevTimeNotifyLoop = toDate;
	}

	@Override
	public void notifyAll(Task task) {
		manager.notifyAll(task);
	}

	protected void startNotifyLoop() {
		NotifyLoop.start();
	}

	protected void stopNotifyLoop() {
		NotifyLoop.interrupt();
	}

	// public void addNotifyLater(Manager.Subscriber subscriber, Object taskId,
	// Date notifyLaterDate) {
	// if (notifyLaterDate == null)
	// throw new NullPointerException(
	// "Unable to add notify. NotifyLaterDate is null.");
	// if (taskId == null)
	// throw new NullPointerException(
	// "Unable to add notify. TaskId is null.");
	//
	// synchronized (mapNotifyLater) {
	// mapNotifyLater.put(notifyLaterDate, new NotifyLaterItem(subscriber,
	// taskId));
	// }
	// }

}
