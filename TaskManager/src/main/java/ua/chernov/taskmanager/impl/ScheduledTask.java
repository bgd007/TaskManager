package ua.chernov.taskmanager.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import ua.chernov.taskmanager.Task;

@SuppressWarnings("serial")
public class ScheduledTask implements Task, Serializable, Cloneable {
	private UUID id;
	private String title;
	private String description;
	private Date notifyDate;
	private String contacts;

	public ScheduledTask() {
		this.id = UUID.randomUUID();
	}

	public ScheduledTask(String title) {
		this.id = UUID.randomUUID();
		setTitle(title);
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		if ((title == null) || (title.equals("")))
			throw new IllegalArgumentException(
					"Название задачи не может быть пустым.");

		this.title = title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Date getNotifyDate() {
		return notifyDate;
	}

	@Override
	public void setNotifyDate(Date notifyDate) {
		this.notifyDate = notifyDate;
	}

	@Override
	public String getContacts() {
		return contacts;
	}

	@Override
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);

		String textNotifyDate = (notifyDate != null) ? df.format(notifyDate)
				: "";

		StringBuffer str = new StringBuffer("title="
				+ ((title != null) ? title : ""));
		str.append("description=" + ((description != null) ? description : ""));
		str.append("contacts=" + ((contacts != null) ? contacts : ""));
		str.append("notifyDate=[" + textNotifyDate + "]");

		return str.toString();
	}

	@Override
	public ScheduledTask clone() {
		ScheduledTask clone = null;
		try {
			clone = (ScheduledTask) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cloning for "
					+ this.getClass().getName()
					+ " parent class not supported.", e);
		}

		return clone;
	}

	@Override
	public Task getClone() {
		return this.clone();
	}

}
