package ua.chernov.taskmanager.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ua.chernov.taskmanager.Task;

public interface ITaskView extends IView {

	String ACTION_SAVE = "save";

	enum CardState {
		ADD, EDIT, VIEW;

		public static CardState fromString(String token) {
			return CardState.valueOf(token);
		}

		public static String token(CardState cardState) {
			return cardState.name();
		}
		
		public static Node toXML(CardState cardState, Document doc) {
			Element nodeCardState = doc.createElement("cardState");					
			String nodeValue = CardState.token(cardState);
			nodeCardState.appendChild(doc.createTextNode(nodeValue));

			return nodeCardState;
		}
	};

	Task getTask() throws ParseException;

	public void fireCloseAction();

	public CardState getCardState();
}
