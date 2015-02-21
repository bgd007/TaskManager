package ua.chernov.taskmanager.impl;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.helper.DateHelper;
import ua.chernov.taskmanager.helper.XmlHelper;

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

	public ScheduledTask(UUID id) {
		this.id = id;
	}

	public ScheduledTask(String title) throws Exception {
		this();
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
	public void setTitle(String title) throws Exception {
		if ((title == null) || (title.equals("")))
			throw new Exception("Title must be not empty.");

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
		String textNotifyDate = (notifyDate != null) ? DateHelper
				.formatDate(notifyDate) : "";

		StringBuffer str = new StringBuffer("title="
				+ ((title != null) ? title : ""));
		str.append(" description=" + ((description != null) ? description : ""));
		str.append(" contacts=" + ((contacts != null) ? contacts : ""));
		str.append(" notifyDate=[" + textNotifyDate + "]");
		str.append(" id=" + ((id != null) ? id.toString() : ""));

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
	public Element toXML(Document doc) {
		Element nodeTask = doc.createElement("task");
		nodeTask.setAttribute("id", id.toString());

		Element nodeTitle = doc.createElement("title");
		nodeTitle.appendChild(doc.createTextNode((title != null) ? title : ""));
		nodeTask.appendChild(nodeTitle);

		Element nodeDescription = doc.createElement("description");
		nodeDescription.appendChild(doc
				.createTextNode((description != null) ? description : ""));
		nodeTask.appendChild(nodeDescription);

		Element nodeContacts = doc.createElement("contacts");
		nodeContacts.appendChild(doc
				.createTextNode((contacts != null) ? contacts : ""));
		nodeTask.appendChild(nodeContacts);

		String textNotifyDate = (notifyDate != null) ? DateHelper
				.formatDate(notifyDate) : "";
		Element nodeNotifyDate = doc.createElement("notifyDate");
		nodeNotifyDate.appendChild(doc.createTextNode(textNotifyDate));
		nodeTask.appendChild(nodeNotifyDate);

		return nodeTask;
	}

	public static Task fromXML(Element node) throws ParseException {
		UUID id = UUID.fromString(node.getAttribute("id"));

		String title = XmlHelper.getNodeValue(XmlHelper.getNode("title", node));

		String description = XmlHelper.getNodeValue(XmlHelper.getNode(
				"description", node));

		String contacts = XmlHelper.getNodeValue(XmlHelper.getNode("contacts",
				node));

		String notifyDateText = XmlHelper.getNodeValue(XmlHelper.getNode(
				"notifyDate", node));
		Date notifyDate = null;
		if (!notifyDateText.equals("")) {
			notifyDate = DateHelper.parse(notifyDateText);
		}

		ScheduledTask result = new ScheduledTask(id);
		result.title = title;
		result.setDescription(description);
		result.setContacts(contacts);
		result.setNotifyDate(notifyDate);

		return result;
	}

}
