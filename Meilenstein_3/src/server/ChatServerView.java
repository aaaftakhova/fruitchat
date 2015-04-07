package server;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JLabel;

import java.awt.Dimension;
import javax.swing.border.TitledBorder;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import core.Account;
import core.Room;

import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import java.awt.Insets;

public class ChatServerView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6908718466521965307L;
	private ChatServer server;
	private ArrayList<String> logs = new ArrayList<String>();
	private JPanel contentPane;
	private JLabel lblID;
	private JLabel lblName;
	private JLabel lblMitglieder;
	private JLabel lblEditierbar;
	private JList<String> accountList;
	private JList<String> roomsList;
	private DefaultListModel<String> listModel;
	private DefaultListModel<String> roomListModel;
	private JTextArea serverLogOutput;
	private ChatServerView selfPointer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatServerView frame = new ChatServerView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * Create the frame.
	 */
	public ChatServerView() {
		setMinimumSize(new Dimension(500, 300));
		selfPointer = this;
		try {
			setIconImage(ImageIO.read(new File("./data/gfx/fruitchat.gif")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		setTitle("Server");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				server.publicMessage("[server]: /serverclose");
				server.createLogFile(logs);
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int dx = 600;
		int dy = 400;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - dx) / 2, (dim.height - dy) / 2, dx, dy);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnRoom = new JMenu("Room");
		menuBar.add(mnRoom);
		
		JMenuItem mntmCreateRoom = new JMenuItem("create...");
		mntmCreateRoom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mntmCreateRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s = null;
				while (s == null) {
					s = JOptionPane.showInputDialog(null, "Room name");
					if (s == null) break;
					if (!s.matches("^[a-zA-Z0-9]*$") || s.equals(server.getDefaultRoomName())) {
						JOptionPane.showMessageDialog(null, "The room name is not valid", "Error: action unavailable", JOptionPane.INFORMATION_MESSAGE);
						s = null;
					}
				}
				
				if (s != null) {
					server.createNewRoom(s);
				}				
			}
		});
		mnRoom.add(mntmCreateRoom);
		
		JMenuItem mntmDeleteRoom = new JMenuItem("delete");
		mntmDeleteRoom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
		mntmDeleteRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (roomsList.getSelectedIndex() > -1){
					String room = roomsList.getSelectedValue().toString();
					room = room.substring(0, room.indexOf("("));
					if (!isEditable(room)) return;
					int answer = JOptionPane.showConfirmDialog(null, "Do you really want to delete " + room + "?");					
					if (answer == 0) {
						server.deleteRoom(room);
					}
				} else {
					 JOptionPane.showMessageDialog(null, "No room was selected");
				}
			}
		});
		mnRoom.add(mntmDeleteRoom);
		
		JMenuItem mntmRenameRoom = new JMenuItem("rename...");
		mntmRenameRoom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mntmRenameRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (roomsList.getSelectedIndex() > -1) {
					String room = roomsList.getSelectedValue().toString();
					room = room.substring(0, room.indexOf("("));
					if (!isEditable(room)) return;
					String s = JOptionPane.showInputDialog("Rename room " + room + " to");
					server.renameRoom(room, s);
				 } else {
					 JOptionPane.showMessageDialog(null, "No room was selected");
				 }
			}
		});
		mnRoom.add(mntmRenameRoom);
		
		JMenu mnUser = new JMenu("User");
		menuBar.add(mnUser);
		
		JMenuItem mntmWarn = new JMenuItem("warn...");
		mntmWarn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK));
		mntmWarn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (accountList.getSelectedIndex() > -1) {
					String[] selectedUserData = accountList.getSelectedValue().toString().split(" ");
					String user = selectedUserData[0];
					//check if online
					String state = selectedUserData[1];
					state = state.substring(1, state.length() - 1);
					if (state.equals("offline")) { 
						JOptionPane.showMessageDialog(null, "This user is offline.", "Error: action not available", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					String message = JOptionPane.showInputDialog("Input the warning message: ");
					server.accMessage(user, "[admin]: " + message);
					selfPointer.addLog("user: " + user + " :warned: text: " + message);
				}
			}
		});
		mnUser.add(mntmWarn);
		
		JMenuItem mntmOff = new JMenuItem("send offline");
		mntmOff.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK));
		mntmOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (accountList.getSelectedIndex() > -1) {
					String[] selectedUserData = accountList.getSelectedValue().toString().split(" ");
					String user = selectedUserData[0];
					//check if online
					String state = selectedUserData[1];
					state = state.substring(1, state.length() - 1);
					if (state.equals("offline")) { 
						JOptionPane.showMessageDialog(null, "This user is offline.", "Error: action not available", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int answer = JOptionPane.showConfirmDialog(null, "Do you want to send user ["+user+"] offline?");
					if (answer == 0) {
						server.accMessage(user, "[server]: /off " + user);
						selfPointer.addLog("user: " + user + " :sent offline");
						server.setAccOffline(user);
					}
				}
			}
		});
		mnUser.add(mntmOff);
		
		JMenuItem mntmBan = new JMenuItem("ban...");
		mntmBan.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.ALT_MASK));
		mntmBan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (accountList.getSelectedIndex() > -1) {
					String user = accountList.getSelectedValue().toString().split(" ")[0];
					String issue = JOptionPane.showInputDialog("Issue of ban: "); 
					int answer = JOptionPane.showConfirmDialog(null, "Do you really want to ban user ["+user+"] ?");
					if (answer == 0) {
						issue = "[server]: /ban " + issue;
						server.accMessage(user, issue);
						try {
							server.setAccOffline(user);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
						
						}
						server.updateBanList(user);
					}
				}
			}
		});
		mnUser.add(mntmBan);
		
		JMenuItem mntmDeleteUser = new JMenuItem("delete account");
		mntmDeleteUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (accountList.getSelectedIndex() > -1) {
					String accName = accountList.getSelectedValue().toString().split(" ")[0];
					String state = accountList.getSelectedValue().toString().split(" ")[1];
					state = state.substring(1, state.length() - 1);
					//check if offline
					if (state.equals("online")) {
						JOptionPane.showMessageDialog(null, "Accounts being used at the moment cannot be removed");
					} else {
						int answer = JOptionPane.showConfirmDialog(null, "Do you really want to delete account ["+accName+"] ?");
						if (answer == 0) {
							server.deleteAccount(accName);
						}
					}
				}
			}
		});
		mnUser.add(mntmDeleteUser);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenuItem mntmShowBanList = new JMenuItem("ban list...");
		mntmShowBanList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		mntmShowBanList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new BanListEdit(server).setVisible(true);
			}
		});
		mnOptions.add(mntmShowBanList);
		
		JMenuItem mntmChangeDefaultRoomName = new JMenuItem("change default-room name...");
		mntmChangeDefaultRoomName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name = JOptionPane.showInputDialog(null, "New name");
				String regExPattern = "^[a-zA-Z0-9]*$";
				//if exist, not null and numerical
				if (name!= null && name.length() > 0 && name.matches(regExPattern)) {
					if (name.equals(server.getDefaultRoomName())) return;
					boolean done = server.setDefaultRoomName(name);
					if (!done) {
						JOptionPane.showMessageDialog(null, "There is already a room with such name");
					}
				} else {
					JOptionPane.showMessageDialog(null, "The name doesn't fullfill the naming requirements.");
				}
			}
		});
		mnOptions.add(mntmChangeDefaultRoomName);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JPanel serverlog = new JPanel();
		serverlog.setPreferredSize(new Dimension(350,400));
		serverlog.setBorder(new TitledBorder(null, "Serverlog",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel status = new JPanel();
		status.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		status.setLayout(new BorderLayout(0, 0));

		JTabbedPane statusTabPane = new JTabbedPane(JTabbedPane.TOP);
		statusTabPane.setPreferredSize(new Dimension(250,400));
		statusTabPane.setToolTipText("");
		status.add(statusTabPane, BorderLayout.CENTER);

		JPanel acc_Tab = new JPanel();
		acc_Tab.setToolTipText("");
		statusTabPane.addTab("Users\n", null, acc_Tab, null);
		acc_Tab.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_accs = new JScrollPane();
		scrollPane_accs.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		acc_Tab.add(scrollPane_accs);
		
		listModel = new DefaultListModel<>();
		accountList = new JList<String>(listModel);
		accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_accs.setViewportView(accountList);
		
		

		JPanel room_Tab = new JPanel();
		room_Tab.setToolTipText("");
		statusTabPane.addTab("Rooms", null, room_Tab, null);
		room_Tab.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_rooms = new JScrollPane();
		scrollPane_rooms.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		room_Tab.add(scrollPane_rooms);
		
		roomListModel = new DefaultListModel<>();
		roomsList = new JList<String>(roomListModel);
		roomsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		roomsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				 int id = roomsList.getSelectedIndex();
				 if (id > -1) {
					 String roomInfo = roomsList.getSelectedValue().toString();
					 String name = roomInfo.substring(0, roomInfo.indexOf("("));
					 String users = roomInfo.substring(roomInfo.indexOf("(") + 1, roomInfo.indexOf(")"));
					 String edit = name.equals(server.getDefaultRoomName()) ? "no" : "yes";
					 lblID.setText("ID: \n" + id);
					 lblName.setText("Name: \n" + name);
					 lblMitglieder.setText("Members: \n" + users);
					 lblEditierbar.setText("Editable: \n" + edit); 
				 } else {
					 lblID.setText("ID: \n");
					 lblName.setText("Name: \n");
					 lblMitglieder.setText("Members: \n");
					 lblEditierbar.setText("Editable: \n");
				 }
				 
			}
		});
		
		scrollPane_rooms.setViewportView(roomsList);
		
		JPanel info = new JPanel();
		info.setPreferredSize(new Dimension(250, 150));
		room_Tab.add(info, BorderLayout.SOUTH);
		info.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Room Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		info.setLayout(null);
		
		lblID = new JLabel("ID:\n");
		lblID.setBounds(10, 10, 220, 30);
		info.add(lblID);
		
		lblName = new JLabel("Name: ");
		lblName.setBounds(10, 70, 220, 30);
		info.add(lblName);
						
		lblMitglieder = new JLabel("Members: ");
		lblMitglieder.setBounds(10, 40, 220, 30);
		info.add(lblMitglieder);
		
		lblEditierbar = new JLabel("Editable: ");
		lblEditierbar.setBounds(10, 100, 220, 30);
		info.add(lblEditierbar);
		contentPane.setLayout(new BorderLayout(0, 0));
		serverlog.setLayout(new BorderLayout(4, 4));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		serverlog.add(scrollPane);

		serverLogOutput = new JTextArea();
		serverLogOutput.setMargin(new Insets(2, 2, 2, 2));
		serverLogOutput.setEditable(false);
		serverLogOutput.setLineWrap(true);
		// textArea.setPreferredSize(new Dimension(10,1000));
		scrollPane.setViewportView(serverLogOutput);
		contentPane.add(serverlog, BorderLayout.CENTER);
		contentPane.add(status, BorderLayout.EAST);
		try {
			server=new ChatServer(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(server).start();
	}

	public void FillBenutzerList (ArrayList<Account> accs) {
		this.listModel.clear();
		for (Account acc : accs) {
			this.listModel.addElement(acc.toString());
		}
		repaint();
	}
	
	public void FillRoomsList (ArrayList<Room> rooms) {
		this.roomListModel.clear();
		for (Room room : rooms) {
			this.roomListModel.addElement(room.getRoomString());
		}
		repaint();
	}
	
	public void addLog(String str){
		logs.add(str);
		serverLogOutput.append(str+"\n");
		repaint();
	}
	
	public boolean isEditable(String room) {
		if (room.equals(server.getDefaultRoomName())) {
			JOptionPane.showMessageDialog(null, "The default room cannot be changed or deleted.", "Error: action unavailable", JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else {
			return true;
		}
	}
	
}
