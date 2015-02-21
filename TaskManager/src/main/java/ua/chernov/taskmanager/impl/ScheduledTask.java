package ua.chernov.taskmanager.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.helper.DateHelper;
 
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
					"Title must be not empty.");

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
		String textNotifyDate = (notifyDate != null) ? DateHelper.formatDate(notifyDate)
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

	@Override
	public Node toXML(Document doc) {
		Element nodeTask = doc.createElement("task");
		nodeTask.setAttribute("id", id.toString());
		
		org.w3c.dom.Element nodeTitle = doc.createElement("title");
		nodeTitle.appendChild(doc.createTextNode(title));
		nodeTask.appendChild(nodeTitle);

		org.w3c.dom.Element nodeDescription = doc.createElement("description");
		nodeDescription.appendChild(doc.createTextNode(description));
		nodeTask.appendChild(nodeDescription);
		
		org.w3c.dom.Element nodeContacts = doc.createElement("contacts");
		nodeContacts.appendChild(doc.createTextNode(contacts));
		nodeTask.appendChild(nodeContacts);

		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		String textNotifyDate = (notifyDate != null) ? df.format(notifyDate)
				: "";
		org.w3c.dom.Element nodeNotifyDate = doc.createElement("notifyDate");
		nodeNotifyDate.appendChild(doc.createTextNode(textNotifyDate));
		nodeTask.appendChild(nodeNotifyDate);
		
		return nodeTask;
	}

}
