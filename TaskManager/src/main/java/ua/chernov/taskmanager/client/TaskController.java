package ua.chernov.taskmanager.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.chernov.taskmanager.Manager;
import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;
import ua.chernov.taskmanager.client.ITaskView.CardState;
import ua.chernov.taskmanager.transport.ClientTransport;

public class TaskController implements Manager.Subscriber, ActionListener {
	private static final String DEFAULT_HOSTNAME = "localhost";
	private static final Logger log = LogManager
			.getLogger(TaskController.class);

	private Manager model;
	private String nick;
	private ITaskListView taskListView;
	private ITaskView taskView;
	private ITaskNotifyView taskNotifyView;
	private List<GuiView> views = new ArrayList<GuiView>();

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		ClientExceptionHandler handler = new ClientExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(handler);

		TaskController controller = new TaskController();
		log.info("TaskController client conncting to server...");

		String hostName = DEFAULT_HOSTNAME;
		if (args.length > 0)
			if (args[0] != null) {
				hostName = args[0];
			}

		Manager manager = new ClientTransport(new Socket(hostName, 1024));

		log.info("TaskController client connected, registering...");
		controller.subscribeTo(manager);
		controller.createTaskListView();
	}

	public void subscribeTo(Manager manager) {
		Random random = new Random();
		this.nick = "User" + random.nextInt(100);
		manager.subscribe(this);
		this.model = manager;
	}

	@Override
	public String getNick() {
		return nick;
	}

	@Override
	public void receiveTaskList(TaskList taskList) {
		log.info("receiveTaskList");

		if (taskListView != null)
			taskListView.update(taskList);
	}

	@Override
	public void receiveTaskById(Task task, Object cardState) {
		log.info("receiveTaskById");
		createTaskView(task, (CardState) cardState);
	}

	@Override
	public void receiveNewTask(Task task) {
		log.info("receiveNewTask");
		createTaskView(task, CardState.ADD);
	}

	// @Override
	// public void addTaskResponse(Object response) {
	// // TODO Auto-generated method stub
	// }

	@Override
	public void receiveSaveTaskOk() {
		log.info("receiveSaveTaskOk");
		if (taskView != null) {
			taskView.fireCloseAction();
		}

		if (taskListView != null) {
			taskListView.fireListAction();
		}
	}

	@Override
	public void receiveDeleteTaskOk() {
		log.info("receiveDeleteTaskOk");
		if (taskListView != null) {
			taskListView.fireListAction();
		}
	}

	@Override
	public void receiveNotify(Task task) {
		log.info("receiveNotify");
		createTaskNotifyView(task);
	}

	@Override
	public void receiveNotifyLaterOk() {
		log.info("receiveNotifyLaterOk");
		if (taskNotifyView != null) {
			taskNotifyView.fireCloseAction();
		}

		if (taskListView != null) {
			taskListView.fireListAction();
		}
	}

	public void createTaskListView() {
		taskListView = new TaskListView();
		views.add((GuiView) taskListView);
		taskListView.addActionListener(this);
		taskListView.show();
	}

	public void createTaskView(Task task, CardState cardState) {
		taskView = new TaskView(task, cardState, taskListView);
		views.add((GuiView) taskView);
		taskView.addActionListener(this);
		taskView.show();
	}

	public void createTaskNotifyView(Task task) {
		taskNotifyView = new TaskNotifyView(task, taskListView);
		views.add((GuiView) taskNotifyView);
		taskNotifyView.addActionListener(this);
		taskNotifyView.show();
	}

	public void actionPerformed(ActionEvent event) {
		if (event != null)
			if (event.getActionCommand() != null)
				log.info("actionPerformed " + event.getActionCommand());

		IView view = (IView) event.getSource();
		if (event.getActionCommand().equals(IView.ACTION_CLOSE)) {
			view.close();
			views.remove(view);

			if (view instanceof ITaskListView)
				taskListView = null;

			if (view instanceof ITaskView)
				taskView = null;

			if (view instanceof ITaskNotifyView)
				taskNotifyView = null;

			if (views.size() == 0)
				model.unsubscribe(this);
		}

		if (event.getActionCommand().equals(ITaskListView.ACTION_LIST)) {
			model.giveTaskList(this);
		}

		if (event.getActionCommand().equals(ITaskListView.ACTION_ADD)) {
			model.getNewTask(this);
		}

		if (event.getActionCommand().equals(ITaskListView.ACTION_EDIT)) {
			ITaskListView viewTL = (ITaskListView) view;
			Task task = viewTL.getSelectedTask();
			CardState cardState = CardState.EDIT;
			if (task == null) {
				JOptionPane.showMessageDialog(viewTL.getFrame(),
						"Task is not defined", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			model.getTaskById(this, task.getId(), cardState);
		}

		if (event.getActionCommand().equals(ITaskListView.ACTION_VIEW)) {
			ITaskListView viewTL = (ITaskListView) view;
			Task task = viewTL.getSelectedTask();
			CardState cardState = CardState.VIEW;
			if (task == null) {
				JOptionPane.showMessageDialog(viewTL.getFrame(),
						"Task is not defined", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			model.getTaskById(this, task.getId(), cardState);
		}

		if (event.getActionCommand().equals(ITaskView.ACTION_SAVE)) {
			try {
				Task task = ((ITaskView) view).getTask();
				CardState cardState = ((ITaskView) view).getCardState();
				if (cardState == CardState.ADD) {
					model.addTask(this, task);
				}

				if (cardState == CardState.EDIT) {
					model.editTask(this, task);
				}
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Warning",
						JOptionPane.WARNING_MESSAGE);
				// ClientExceptionHandler.showException(e);
			}
		}

		if (event.getActionCommand().equals(ITaskListView.ACTION_DELETE)) {
			ITaskListView viewTL = (ITaskListView) view;
			Task task = viewTL.getSelectedTask();
			if (task == null) {
				JOptionPane.showMessageDialog(viewTL.getFrame(),
						"Task is not defined", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			model.deleteTask(this, task);
		}

		if (event.getActionCommand().equals(ITaskNotifyView.ACTION_NOTIFYLATER)) {

			taskNotifyView = ((ITaskNotifyView) view);
			Object taskId = taskNotifyView.getTaskId();
			Date notifyLaterDate = null;
			try {
				notifyLaterDate = taskNotifyView.getNotifyLaterDate();
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
				// ClientExceptionHandler.showException(e);
			}

			model.notifyLater(this, taskId, notifyLaterDate);
		}

		if (event.getActionCommand().equals(ITaskListView.ACTION_SENDXML)) {
			UUID id = UUID.fromString("550d97b4-8c87-4893-ae5d-4e9a080e4f2e");

			model.getTaskById(this, id, CardState.EDIT);
		}

	}

}
