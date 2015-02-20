package ua.chernov.taskmanager.client;

import javax.swing.JFrame;

import ua.chernov.taskmanager.Task;

public interface ITaskListView extends IView {
    String ACTION_LIST  = "list";
	String ACTION_ADD = "add";
	String ACTION_EDIT = "edit";
	String ACTION_VIEW = "view";
	String ACTION_DELETE = "delete";
	String ACTION_SENDXML = "sendxml";
	
	public JFrame getFrame();
	public void fireListAction();
	public Task getSelectedTask();
	
}
