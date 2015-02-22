package ua.chernov.taskmanager.client;

import java.text.ParseException;
import java.util.Date;

public interface ITaskNotifyView extends IView {

	String ACTION_NOTIFYLATER  = "notifyLater";
		
	public void fireCloseAction();
	public Object getTaskId();
	public Date getNotifyLaterDate() throws ParseException;;

}
