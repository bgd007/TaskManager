package ua.chernov.taskmanager;

import java.util.Date;

import ua.chernov.taskmanager.transport.Marshallable;


public interface Task extends Marshallable {

	public Object getId();

	public String getTitle();

	public void setTitle(String title) throws Exception;

	public String getDescription();

	public void setDescription(String Description);

	public Date getNotifyDate();

	public void setNotifyDate(Date NotifyDate);

	public String getContacts();

	public void setContacts(String Contacts);	
	
	public Task getClone();
}
