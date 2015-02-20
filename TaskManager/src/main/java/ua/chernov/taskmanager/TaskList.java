package ua.chernov.taskmanager;

import java.util.Date;
import java.util.SortedMap;

public abstract class TaskList implements Iterable<Task> {
	/**
	 * Add task
	 * 
	 * @param task
	 *            task to add
	 */
	public abstract void add(Task task);

	/**
	 * Change task in list
	 * 
	 * @param newTask
	 *            new Task replace old Task by id
	 */
	public abstract void edit(Task newTask);

	/**
	 * find task with id=id and remove it from list
	 * 
	 * @param id
	 *            id to remove
	 */
	public abstract void removeById(Object id);

	/**
	 * 
	 * @return size of list
	 */
	public abstract int size();

	/**
	 * 
	 * @param id
	 *            Task id
	 * @return return Task by id
	 */
	public abstract Task getTaskById(Object id);

	/**
	 * 
	 * @param fromDate
	 *            start date
	 * @param toDate
	 *            end date
	 * @return return task with NotifyDate between fromDate and toDate
	 */
	public abstract SortedMap<Date, Task> incoming(Date fromDate, Date toDate);

	/**
	 * Save tasks to file
	 * 
	 * @param fileName
	 *            path to the file in which the data should be stored
	 */
	public abstract void storeToDisk(String fileName);

	/**
	 * load saved tasks from file to TaskList
	 * 
	 * @param fileName
	 *            path to the file in which is stored
	 */
	public abstract void loadFromDisk(String fileName);

}
