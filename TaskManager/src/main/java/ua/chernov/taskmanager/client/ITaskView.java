package ua.chernov.taskmanager.client;

import java.text.ParseException;

import ua.chernov.taskmanager.Task;

public interface ITaskView extends IView {

	String ACTION_SAVE  = "save";
	enum CardState {ADD, EDIT, VIEW};
	
	
	
	Task getTask() throws ParseException;
	public void fireCloseAction();
	public CardState getCardState();
}
