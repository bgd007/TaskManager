package ua.chernov.taskmanager;


public interface Notifier {

	/**
	 * notify all subscribers
	 * 
	 * @param task
	 *            task to notify
	 */
	public void notifyAll(Task task);

}