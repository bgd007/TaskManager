package ua.chernov.taskmanager.client;


import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;

@SuppressWarnings("serial")
public class TableModelTaskList extends AbstractTableModel {

	private ArrayList<String> columnNames = new ArrayList<String>();

	private ArrayList<Class> columnTypes = new ArrayList<Class>();

	private ArrayList<Task> data = new ArrayList<Task>();

	public static final String CAPTION_TITLE = "Title";
	public static final String CAPTION_DESCRIPTION = "Description";
	public static final String CAPTION_NOTIFYDATE = "NotifyDate";
	public static final String CAPTION_CONTACTS = "Contacts";

	public TableModelTaskList(boolean editable) {
		this.editable = editable;

		columnNames.add(CAPTION_TITLE);
		columnNames.add(CAPTION_DESCRIPTION);
		columnNames.add(CAPTION_NOTIFYDATE);
		columnNames.add(CAPTION_CONTACTS);

		columnTypes.add(String.class);
		columnTypes.add(String.class);
		columnTypes.add(Date.class);
		columnTypes.add(String.class);

	}

	private boolean editable;

	public int getRowCount() {
		synchronized (data) {
			return data.size();
		}
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	public Class getColumnClass(int column) {
		return columnTypes.get(column);
	}

	public String getColumnName(int column) {
		return (String) columnNames.get(column);
	}

	public int getColumnByName(String columnName) {
		int index = columnNames.indexOf(columnName);
		if (index == -1) {
			throw new RuntimeException("Column " + columnName + " not found.");
		}
		return index;
	}

	public Object getValueAt(int row, int column) {
		Object result = null;
		synchronized (data) {
			// Task task = data[i].getTask(row);
			Task task = data.get(row);
			String columnName = getColumnName(column);
			switch (columnName) {
			case CAPTION_TITLE:
				result = task.getTitle();
				break;
			case CAPTION_DESCRIPTION:
				result = task.getDescription();
				break;
			case CAPTION_NOTIFYDATE:
				result = task.getNotifyDate();
				break;
			case CAPTION_CONTACTS:
				result = task.getContacts();
				break;
			}
		}
		return result;

	}

	public boolean isEditable(int row, int column) {
		return editable;
	}

	public void setValueAt(Object value, int row, int column) {
		
	}

	public void setDataSource(TaskList ds) {		
		data.clear();

		if (ds == null)
			return;

		for (Task task : ds) {
			synchronized (data) {
				data.add(task);
				fireTableRowsInserted(data.size() - 1, data.size() - 1);
			}
		}
	}

	public Task getTaskAtRow(int row) {
		Task result = null;
		synchronized (data) {
			result = data.get(row);
		}

		return result;
	}
}
