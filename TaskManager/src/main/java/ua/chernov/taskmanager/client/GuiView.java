package ua.chernov.taskmanager.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class GuiView implements IView {
	protected static final Logger log = LogManager.getLogger(GuiView.class);
	
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();

	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	protected void fireAction(String command) {
		ActionEvent event = new ActionEvent(this, 0, command);
		for (Iterator<ActionListener> listener = listeners.iterator(); listener
				.hasNext();) {
			((ActionListener) listener.next()).actionPerformed(event);
		}
	}

	protected abstract void createFrame();

	public GuiView() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// Just print stacktrace, it isn't critical
			log.error("Error setting LookAndFeel.", ex);
			//ex.printStackTrace();
		}
	}
}
