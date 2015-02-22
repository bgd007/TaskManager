package ua.chernov.taskmanager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;
import ua.chernov.taskmanager.helper.XmlHelper;

@SuppressWarnings("serial")
public class MapTaskList extends TaskList implements Cloneable, Serializable,
		Iterable<Task> {

	private LinkedHashMap<Object, Task> mapTasks = new LinkedHashMap<Object, Task>();

	@Override
	public Iterator<Task> iterator() {
		return mapTasks.values().iterator();
	}

	@Override
	public void add(Task task) {
		if (task == null)
			throw new NullPointerException("Error adding task to MapTaskList."
					+ " Task is null.");

		mapTasks.put(task.getId(), task);
	}

	@Override
	public void edit(Task newTask) {
		if (newTask == null)
			throw new NullPointerException("Error editing task in list."
					+ " Task is null.");

		if (!mapTasks.containsKey(newTask.getId()))
			throw new RuntimeException("Task to edit not found.");

		mapTasks.put(newTask.getId(), newTask);
	}

	@Override
	public int size() {
		return mapTasks.size();
	}

	@Override
	public void removeById(Object id) {
		if (mapTasks.containsKey(id)) {
			mapTasks.remove(id);
		} else
			throw new RuntimeException("Task index not found by Id.");
	}

	@Override
	public Task getTaskById(Object id) {
		Task task = mapTasks.get(id);
		if (task == null) {
			String idStr = (id != null) ? id.toString() : "";
			throw new RuntimeException("Task not found by id " + idStr + ".");
		}

		return mapTasks.get(id);
	}

	@Override
	public SortedMap<Date, Task> incoming(Date fromDate, Date toDate) {
		TreeMap<Date, Task> result = new TreeMap<Date, Task>();
		synchronized (mapTasks) {
			for (Task task : mapTasks.values()) {
				Date notifyDate = task.getNotifyDate();
				if (notifyDate == null)
					continue;

				if (notifyDate.equals(fromDate) || notifyDate.after(fromDate)) {
					if (notifyDate.before(toDate)) {
						result.put(task.getNotifyDate(), task);
					}
				}
			}
		}

		return result;
	}

	@Override
	public MapTaskList clone() {
		MapTaskList clone = null;
		try {
			clone = (MapTaskList) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cloning for "
					+ this.getClass().getName()
					+ " parent class not supported.", e);
		}

		clone.mapTasks = (LinkedHashMap<Object, Task>) mapTasks.clone();

		return clone;
	}

	@Override
	public void storeToDisk(String fileName) {
		try (FileOutputStream fos = new FileOutputStream(fileName);
				ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(mapTasks);
			oos.flush();
			/*
			 * oos.close(); fos.close();
			 */
		} catch (IOException e) {
			throw new RuntimeException("Error storing TaskList to file.", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadFromDisk(String fileName) {
		File file = new File(fileName);
		if (!file.exists())
			return;

		try (FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis)) {

			mapTasks = (LinkedHashMap<Object, Task>) ois.readObject();
			ois.close();

		} catch (Exception e) {
			throw new RuntimeException("Error loading TaskList from file.", e);
		}
	}

	@Override
	public String toString() {
		String s = "MapTaskList info: size=" + size() + ";";

		int i = 0;
		for (Task task : this) {
			s += "\n" + "item[" + i + "]: " + task;
			i++;
		}

		return s;
	}

	@Override
	public Element toXML(Document doc) {
		Element nodeTaskList = doc.createElement("taskList");

		synchronized (mapTasks) {
			for (Task task : mapTasks.values()) {
				Element nodeTask = XmlHelper.marshallableToNode(task, doc);
				nodeTaskList.appendChild(nodeTask);
			}
		}

		return nodeTaskList;
	}

	public static TaskList fromXML(Element node) {
		MapTaskList result = new MapTaskList();

		NodeList nodesTask = node.getChildNodes();

		for (int i = 0; i < nodesTask.getLength(); i++) {
			Element nodeTask = (Element) nodesTask.item(i);
			try {
				Task task = (Task) XmlHelper.nodeToMarshallable(nodeTask);
				result.add(task);
			} catch (NoSuchMethodException | SecurityException
					| ClassNotFoundException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		
		return result;
	}

}
