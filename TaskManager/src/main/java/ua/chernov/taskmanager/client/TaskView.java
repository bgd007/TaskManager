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


public class TaskView extends GuiView implements ITaskView {
	// Room.Subscriber subscriber;
	// Room currentRoom;

	private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
	private static final int INCREMENT_NOTIFYDATE_MINUTE = 3;

	JDialog frame;
	// JFrame frame;
	ITaskListView parentView;

	private JPanel contentPane;
	private JTextField tfTitle;
	private JTextField tfContacts;
	private JTextField tfNotifyDate;
	private JTextArea taDescription;

	Task task;
	private CardState cardState;

	// public static TaskView createView(Task task, CardState cardState,
	// ITaskListView parentView) {
	// return new TaskView(task, cardState, parentView);
	// }

	TaskView(Task task, CardState cardState, ITaskListView parentView) {
		super();

		this.task = task;
		this.parentView = parentView;
		this.cardState = cardState;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createFrame();
				update(null);
			}
		});

		// super(); //super РІС‹Р·С‹РІР°РµС‚ createframe, РєРѕС‚РѕСЂС‹ РІС‹Р·С‹РІР°РµС‚ new
		// JDialog(parentView.getFrame(), "Р—Р°РґР°С‡Р°", true);,
		// Р° parentView РµС‰Рµ СЂР°РІРЅРѕ null

		// createFrame();
	}

	protected void createFrame() {
		frame = new JDialog(parentView.getFrame(), "Task", true);


		// frame.addWindowListener(new WindowAdapter() {
		// @Override
		// public void windowClosing(WindowEvent arg0) {
		// fireCloseAction();
		// }
		// });

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
		panelNorth.setLayout(new GridLayout(3, 1, 0, 0));

		JPanel panelTitle = new JPanel();
		panelNorth.add(panelTitle);
		panelTitle.setLayout(null);

		JLabel lbTitle = new JLabel("Title");
		lbTitle.setBounds(5, 8, 56, 16);
		panelTitle.add(lbTitle);

		tfTitle = new JTextField();
		tfTitle.setColumns(10);
		tfTitle.setBounds(125, 5, 288, 22);
		panelTitle.add(tfTitle);

		JPanel panelNotifyDate = new JPanel();
		panelNorth.add(panelNotifyDate);
		panelNotifyDate.setLayout(null);

		JLabel lbNotifyDate = new JLabel("Notify date");
		lbNotifyDate.setBounds(5, 8, 107, 16);
		panelNotifyDate.add(lbNotifyDate);

		tfNotifyDate = new JTextField();
		tfNotifyDate.setBounds(125, 5, 183, 22);
		tfNotifyDate.setColumns(10);
		panelNotifyDate.add(tfNotifyDate);

		JPanel panelContacts = new JPanel();
		panelNorth.add(panelContacts);
		panelContacts.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JLabel lbContacts = new JLabel("Contacts");
		panelContacts.add(lbContacts);

		tfContacts = new JTextField();
		panelContacts.add(tfContacts);
		tfContacts.setColumns(20);

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
		panelDescription.add(taDescription);

		JPanel panelActionButton = new JPanel();
		contentPane.add(panelActionButton, BorderLayout.SOUTH);

		JPanel panelActionButtonGrid = new JPanel();
		panelActionButton.add(panelActionButtonGrid);

		//panelActionButtonGrid.setLayout(new GridLayout(0, 2, 5, 0));

		if ((cardState == CardState.EDIT) || (cardState == CardState.ADD)) {
			panelActionButtonGrid.setLayout(new GridLayout(0, 2, 5, 0));

			JButton btSave = new JButton("Save");
			panelActionButtonGrid.add(btSave);

			btSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btSaveClick();
				}
			});
		}

		if (cardState == CardState.VIEW) {
			panelActionButtonGrid.setLayout(new GridLayout(0, 1, 5, 0));
		}

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
		String title = tfTitle.getText();

		Date NotifyDate = null;
		String textNotifyDate = tfNotifyDate.getText();
		if (!textNotifyDate.equals("")) {
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
			try {
			NotifyDate = df.parse(tfNotifyDate.getText());
			} catch (ParseException e) {
				throw new ParseException("Unable to parse notify date.", e.getErrorOffset());
			}
		}

		if (cardState == CardState.ADD) {
			// task = new ScheduledTask(title);
			task.setTitle(title);
		}

		if (cardState == CardState.EDIT) {
			// task = ((ScheduledTask)task).clone();
			// task = new ScheduledTask(title);
			task.setTitle(title);
		}

		task.setDescription(taDescription.getText());
		task.setContacts(tfContacts.getText());
		task.setNotifyDate(NotifyDate);

		return task;
	}

	private void btSaveClick() {

		fireAction(ACTION_SAVE);
		// veiwTaskList.getRoom().addTask(veiwTaskList, task);

		// veiwTaskList.giveTaskListClick();
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

		if ((cardState == CardState.EDIT) || (cardState == CardState.VIEW)) {
			tfTitle.setText(task.getTitle());

			Date NotifyDate = task.getNotifyDate();
			String textNotifyDate = "";

			if (NotifyDate != null) {
				textNotifyDate = df.format(task.getNotifyDate());
			}

			tfNotifyDate.setText(textNotifyDate);

			tfContacts.setText(task.getContacts());
			taDescription.setText(task.getDescription());
		}

		if (cardState == CardState.ADD) {
			c.setTime(new Date());
			c.add(Calendar.MINUTE, INCREMENT_NOTIFYDATE_MINUTE);

			tfNotifyDate.setText(df.format(c.getTime()));
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
	public CardState getCardState() {
		return cardState;
	}
}
