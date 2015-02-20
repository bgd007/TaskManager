package ua.chernov.taskmanager.client;

import java.text.ParseException;

import ua.chernov.taskmanager.Task;

public interface ITaskView extends IView {

	String ACTION_SAVE = "save";

	enum CardState {
		ADD, EDIT, VIEW;

		public static CardState type(String token) {
			return CardState.valueOf(token);
		}

		public static String token(CardState t) {
			return t.name();
		}
	};

	Task getTask() throws ParseException;

	public void fireCloseAction();

	public CardState getCardState();
}
