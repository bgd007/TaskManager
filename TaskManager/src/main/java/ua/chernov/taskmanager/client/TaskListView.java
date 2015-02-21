package ua.chernov.taskmanager.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ua.chernov.taskmanager.Task;
import ua.chernov.taskmanager.TaskList;
import ua.chernov.taskmanager.transport.Packets;

public class TaskListView extends GuiView implements ITaskListView {

	private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

	private JFrame frame;
	private JTable tbTasks;
	private TableModelTaskList tm;

	public TaskListView() {
		super();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createFrame();
				update(null);
			}
		});

	}

	protected void createFrame() {
		frame = new JFrame("Task manager");

		frame.setLayout(new BorderLayout());

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				fireAction(ACTION_CLOSE);
			}
		});

		tbTasks = new JTable();
		tm = new TableModelTaskList(true);
		tm.setDataSource(null);
		tbTasks.setModel(tm);

		tbTasks.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);

		@SuppressWarnings("serial")
		DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {

			SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);

			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				if (value instanceof Date) {
					value = f.format(value);
				}
				return super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
			}
		};

		int indColumnNotifyDate = tm
				.getColumnByName(TableModelTaskList.CAPTION_NOTIFYDATE);
		tbTasks.getColumnModel().getColumn(indColumnNotifyDate)
				.setCellRenderer(tableCellRenderer);

		JScrollPane jspTasks = new JScrollPane(tbTasks);

		JPanel panelAction = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelAction.setName("Panel");
		panelAction.setBackground(Color.GRAY);

		JPanel panelActionGrid = new JPanel(new GridLayout(1, 6, 5, 0));
		JButton btTest = new JButton("Test");
		btTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btTestClick();
				// throw new RuntimeException("MyException");
				// tbTasks.getSelectionModel().setSelectionMode(
				// ListSelectionModel.SINGLE_SELECTION);
			}
		});

		JButton btGetTaskList = new JButton("Refresh");
		btGetTaskList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireListAction();
				// giveTaskListClick();
			}
		});

		JButton btAddTask = new JButton("Add");
		btAddTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireAction(ACTION_ADD);
			}
		});

		JButton btEditTask = new JButton("Edit");
		btEditTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireAction(ACTION_EDIT);
			}
		});

		JButton btViewTask = new JButton("View");
		btViewTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireAction(ACTION_VIEW);
			}
		});

		JButton btDeleteTask = new JButton("Delete");
		btDeleteTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireAction(ACTION_DELETE);
			}
		});

		panelActionGrid.add(btTest);
		panelActionGrid.add(btGetTaskList);
		panelActionGrid.add(btAddTask);
		panelActionGrid.add(btEditTask);
		panelActionGrid.add(btViewTask);
		panelActionGrid.add(btDeleteTask);

		panelAction.add(panelActionGrid);
		// panelAction.setMinimumSize(new Dimension(200, 100));

		frame.add(jspTasks, BorderLayout.CENTER);
		frame.add(panelAction, BorderLayout.SOUTH);

		frame.setSize(800, 600);

		// frame.pack();
		frame.setLocationRelativeTo(null); // *** this will center your
											// app ***

		// Dimension dim = new Dimension((int) frame.getSize().getWidth(), 200);
		//
		// frame.setMinimumSize(dim);

		// frame.setVisible(true);
	}

	@Override
	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
				fireListAction();
			}
		});
	}

	@Override
	public void update(Object model) {
		// tm = new TableModelTaskList(true);
		tm.setDataSource((TaskList) model);
	}

	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}

	public JFrame getFrame() {
		return frame;
	}

	@Override
	public void fireListAction() {
		fireAction(ACTION_LIST);
	}

	@Override
	public Task getSelectedTask() {
		Task task = null;
		int row = tbTasks.getSelectedRow();
		if (row != -1) {
			task = tm.getTaskAtRow(row);
		}
		return task;
	}

	public void btTestClick() {		
//		try {
//			 DocumentBuilderFactory factory
//	          = DocumentBuilderFactory.newInstance();
//	        DocumentBuilder builder = factory.newDocumentBuilder();
//	        DOMImplementation impl = builder.getDOMImplementation();
//
//	        Document doc = impl.createDocument(null,null,null);
//	        Element e1 = doc.createElement("howto");
//	        doc.appendChild(e1);
//
//	        Element e2 = doc.createElement("java");
//	        e1.appendChild(e2);
//
//	        e2.setAttribute("url","http://www.rgagnon.com/howto.html");
//
//
//	        // transform the Document into a String
//	        DOMSource domSource = new DOMSource(doc);
//	        TransformerFactory tf = TransformerFactory.newInstance();
//	        Transformer transformer = tf.newTransformer();
//	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//	        transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
//	        transformer.setOutputProperty
//	            ("{http://xml.apache.org/xslt}indent-amount", "4");
//	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//	        java.io.StringWriter sw = new java.io.StringWriter();
//	        StreamResult sr = new StreamResult(sw);
//	        transformer.transform(domSource, sr);
//	        String xml = sw.toString();
//	        
//	        JOptionPane.showMessageDialog(null, xml);
//	        
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//
		fireAction(ACTION_SENDXML);
		// new Packets.SendTaskById(task, cardState);

		// BookType bt = new BookType();
		// bt.setType("typ1");
		//
		// Book book = new Book();
		// book.setId(101);
		// book.setName("Adventures of Huckleberry Finn");
		// book.setAuthor("Mark Twain");
		// book.setCardState(ITaskView.CardState.ADD);
		// book.setBookType(bt);
		//
		//
		// try {
		// File file = new File("book.xml");
		// JAXBContext context = JAXBContext.newInstance(Book.class);
		// Marshaller marshaller = context.createMarshaller();
		//
		// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		//
		// marshaller.marshal(book, file);
		// marshaller.marshal(book, System.out);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

}

@XmlRootElement
class Book {

	int id;
	String name;
	String author;
	ITaskView.CardState cardState;
	BookType bookType;

	public int getId() {
		return id;
	}

	@XmlAttribute
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	@XmlElement
	public void setAuthor(String author) {
		this.author = author;
	}

	public ITaskView.CardState getCardState() {
		return cardState;
	}

	@XmlElement
	public void setCardState(ITaskView.CardState cardState) {
		this.cardState = cardState;
	}

	public BookType getBookType() {
		return bookType;
	}

	@XmlElement
	public void setBookType(BookType bookType) {
		this.bookType = bookType;
	}

}

@XmlRootElement
class BookType {
	String type;

	String getType() {
		return type;
	}

	@XmlElement
	void setType(String type) {
		this.type = type;
	}

	// BookType(String type) {
	// setType(type);
	// }
}

@SuppressWarnings("serial")
@XmlRootElement
class TaskById implements Serializable {
	Task task;
	Object cardState;

	public TaskById(Task task, Object cardState) {
		setTask(task);
		setCardState(cardState);
	}

	@XmlElement
	void setTask(Task task) {
		this.task = task;
	}

	@XmlElement
	void setCardState(Object cardState) {
		this.cardState = cardState;
	}

}
