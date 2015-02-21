package ua.chernov.taskmanager.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import ua.chernov.taskmanager.Task;

public class TaskNotifyView extends GuiView implements ITaskNotifyView {
	// Room.Subscriber subscriber;
	// Room currentRoom;

	private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
	private static final int INCREMENT_NOTIFYLATER_MINUTE = 1;

	JDialog frame;
	// JFrame frame;
	ITaskListView parentView;

	private JPanel contentPane;
	private JTextField tfTitle;
	private JTextArea taDescription;
	private JTextField tfNotifyLater;
  

	Task task;

	// public static TaskView createView(Task task, CardState cardState,
	// ITaskListView parentView) {
	// return new TaskView(task, cardState, parentView);
	// }

	TaskNotifyView(Task task, ITaskListView parentView) {
		super();

		this.task = task;
		this.parentView = parentView;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createFrame();
				update(null);
			}
		});

	}

	protected void createFrame() {
		frame = new JDialog(parentView.getFrame(), "Task notify", true);

		frame.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {
				fireCloseAction();
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}
		});

		frame.setBounds(100, 100, 450, 366);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(contentPane);

		JPanel panelData = new JPanel();
		panelData.setBackground(Color.GRAY);
		contentPane.add(panelData, BorderLayout.CENTER);
		panelData.setLayout(new BorderLayout(0, 0));

		JPanel panelNorth = new JPanel();
		panelData.add(panelNorth, BorderLayout.NORTH);

		panelNorth.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel panelNotifyLater = new JPanel();
		panelNorth.add(panelNotifyLater);
		panelNotifyLater.setLayout(null);

		JLabel lbNotifyLater = new JLabel("Notify Later");
		lbNotifyLater.setBounds(5, 8, 90, 16);
		panelNotifyLater.add(lbNotifyLater);

		tfNotifyLater = new JTextField();
		tfNotifyLater.setBackground(Color.YELLOW);
		tfNotifyLater.setBounds(90, 5, 201, 22);
		tfNotifyLater.setColumns(10);
		panelNotifyLater.add(tfNotifyLater);

		JPanel panelContacts = new JPanel();
		panelNorth.add(panelContacts);
		panelContacts.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JLabel lbTitle = new JLabel("Title");
		panelContacts.add(lbTitle);

		tfTitle = new JTextField();
		tfTitle.setEditable(false);
		panelContacts.add(tfTitle);
		tfTitle.setColumns(20);

		JPanel panelDescription = new JPanel();
		panelData.add(panelDescription, BorderLayout.CENTER);
		panelDescription.setLayout(new BoxLayout(panelDescription,
				BoxLayout.X_AXIS));

		JPanel panelDescriptionLabel = new JPanel();
		panelDescription.add(panelDescriptionLabel);

		JLabel lbDescription = new JLabel("Description");
		panelDescriptionLabel.add(lbDescription);

		taDescription = new JTextArea();
		taDescription.setFont(new Font("Tahoma", Font.PLAIN, 13));
		taDescription.setEditable(false);
		panelDescription.add(taDescription);

		JPanel panelActionButton = new JPanel();
		contentPane.add(panelActionButton, BorderLayout.SOUTH);

		JPanel panelActionButtonGrid = new JPanel();
		panelActionButton.add(panelActionButtonGrid);
		panelActionButtonGrid.setLayout(new GridLayout(0, 2, 5, 0));

		JButton btLater = new JButton("Notify later");
		panelActionButtonGrid.add(btLater);

		btLater.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btLaterClick();
			}
		});

		JButton btCancel = new JButton("Cancel");
		panelActionButtonGrid.add(btCancel);
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireCloseAction();
				;
			}
		});

		frame.setLocationRelativeTo(null); // *** this will center your app ***
		frame.setMinimumSize(frame.getSize());
	}

	public Task getTask() throws ParseException {
		// String title = tfTitle.getText();
		//
		// Date NotifyDate = null;
		// String textNotifyDate = tfNotifyDate.getText();
		// if (!textNotifyDate.equals("")) {
		// SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		// NotifyDate = df.parse(tfNotifyDate.getText());
		// }
		//
		// if (cardState == CardState.ADD) {
		// task = new ScheduledTask(title);
		// }
		//
		// if (cardState == CardState.EDIT) {
		// task.setTitle(title);
		// }
		//
		// task.setDescription(taDescription.getText());
		// task.setContacts(tfTitle.getText());
		// task.setNotifyDate(NotifyDate);

		return task;
	}

	private void btLaterClick() {
		fireAction(ACTION_NOTIFYLATER);		
	}

	@Override
	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// update(task);wer wer wer
				frame.setVisible(true);
			}
		});
	}

	@Override
	public void update(Object model) {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		Calendar c = Calendar.getInstance();

		if (task != null) {
			c.setTime(new Date());
			c.add(Calendar.MINUTE, INCREMENT_NOTIFYLATER_MINUTE);		
			tfNotifyLater.setText(df.format(c.getTime()));

			tfTitle.setText(task.getTitle());
			taDescription.setText(task.getDescription());
		} else {
			throw new NullPointerException("Task is not defined");
		}
	}

	@Override
	public void close() {
		frame.dispose();
	}

	@Override
	public void fireCloseAction() {
		fireAction(ACTION_CLOSE);
	}

	@Override
	public Object getTaskId() {
		return task.getId();
	}

	@Override
	public Date getNotifyLaterDate() throws ParseException {
		Date NotifyLaterDate = null;
		String textNotifyLaterDate = tfNotifyLater.getText();
		if (!textNotifyLaterDate.equals("")) {
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
			try {
			NotifyLaterDate = df.parse(textNotifyLaterDate);
			} catch (ParseException e) {
				throw new ParseException("Unable to parse notify later date.", e.getErrorOffset());
			}
		}
		else {			
			throw new ParseException("Notify later date is empty.", 0);
		}
		
		return NotifyLaterDate;
	}

}
