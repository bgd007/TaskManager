package ua.chernov.taskmanager;

import java.util.Date;


public interface Task {
//	public static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
	
	public Object getId();

	public String getTitle();

	public void setTitle(String title);

	public String getDescription();

	public void setDescription(String Description);

	public Date getNotifyDate();

	public void setNotifyDate(Date NotifyDate);

	public String getContacts();

	public void setContacts(String Contacts);	
	
	public Task getClone();
	
	public org.w3c.dom.Node toXML(org.w3c.dom.Document doc); 
}
