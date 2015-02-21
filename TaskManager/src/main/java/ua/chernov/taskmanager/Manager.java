package ua.chernov.taskmanager;

import java.util.Date;

public interface Manager {

	interface Subscriber {
		String getNick();

		/**
		 * is performed after the response to the request task list
		 * 
		 * @param taskList
		 *            result of request
		 */
		void receiveTaskList(TaskList taskList);

		/**
		 * is performed after the response to the request task by id
		 * 
		 * @param task
		 *            result of request
		 * @param cardState
		 *            state of task form before request
		 */
		void receiveTaskById(Task task, Object cardState);

		// void addTaskResponse(Object response);

		/**
		 * performed after a successful request to add or edit task
		 */
		void receiveSaveTaskOk();

		/**
		 * performed after a successful request to delete tasks
		 */
		void receiveDeleteTaskOk();

		/**
		 * is performed after a request for a new task
		 * 
		 * @param task
		 *            created task with initialize field id
		 */
		void receiveNewTask(Task task);

		/**
		 * occurs during receiving notification
		 * 
		 * @param task
		 *            task of notification
		 */
		void receiveNotify(Task task);

		void receiveNotifyLaterOk();
	}

	/**
	 * Subscribes subscriber.
	 * 
	 * @param subscriber
	 * @throws DuplicatedNickException
	 */
	void subscribe(Subscriber subscriber);

	/**
	 * @param subscriber
	 * @throws IllegalArgumentException
	 *             if no such subscriber
	 */
	void unsubscribe(Subscriber subscriber);

	/**
	 * 
	 * @return return TaskList
	 */
	TaskList getTaskList();

	/**
	 * processing a request for a list of tasks
	 * 
	 * @param subscriber
	 */
	void giveTaskList(Subscriber subscriber);

	/**
	 * processing request to add task
	 * 
	 * @param subscriber
	 * @param task
	 *            task to add
	 */
	void addTask(Subscriber subscriber, Task task);

	/**
	 * processing request to edit task
	 * 
	 * @param subscriber
	 * @param task
	 *            task to edit
	 */
	void editTask(Subscriber subscriber, Task task);

	/**
	 * processing request to get task by task id
	 * 
	 * @param subscriber
	 * @param id
	 *            task id value
	 * @param cardSate
	 *            ITaskView cardState
	 * 
	 */

	void getTaskById(Subscriber subscriber, Object id, Object cardSate);

	/**
	 * processing request to delete task
	 * 
	 * @param subscriber
	 * @param task
	 *            task to delete
	 */
	void deleteTask(Subscriber subscriber, Task task);

	/**
	 * processing request to get new task
	 * 
	 * @param subscriber
	 */

	void getNewTask(Subscriber subscriber);

	/**
	 * @return return path to file of tasks storage
	 */
	String getTaskListStorageFileName();

	void storeTaskListToDisk();

	void loadTaskListFromDisk();

	/**
	 * notify all subscribers
	 * 
	 * @param task
	 *            task to notify
	 */
	public void notifyAll(Task task);

	/**
	 * notify one subscriber
	 * 
	 * @param subscriber
	 * @param task
	 *            task to notify
	 */
	public void notify(Subscriber subscriber, Task task);

	/**
	 * processes the request "Remind me later"
	 * 
	 * @param subscriber
	 * @param taskId
	 * @param notifyLaterDate
	 *            date to notify
	 */
	public void notifyLater(Subscriber subscriber, Object taskId,
			Date notifyLaterDate);

	public String getDefaultDateFormat();

	public void sendXML();

//	/**
//	 * convert xml node to task instance
//	 * 
//	 * @param node
//	 *            xml node of task
//	 * @return return task instance
//	 * @throws ParseException
//	 */
//	public Task taskFromXML(Element node) throws ParseException;
}
