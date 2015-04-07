package client;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import client.ChatClient;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

public class PrivateChatFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JButton btnSendButton;
	private String sender;
	private String empfaenger;
	private JTextArea textArea;
	private boolean enableWriting = true;
	private JPanel textFieldContainer;
	private JScrollPane scrollPane;
	private JPanel containerPane;
	
	public PrivateChatFrame (final ChatClient cc, final String login1, final String login2, String msg) {
		final JFrame self = this;
		this.sender = login1;
		this.empfaenger = login2;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("PrivateChat with " + login2);
		int height = 300;
		int width = 500;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((d.width - width)/2, (d.height - height)/2, width, height);
		getContentPane().setLayout(new BorderLayout(5, 5));
		containerPane = new JPanel();
		containerPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(containerPane, BorderLayout.CENTER);
		containerPane.setLayout(new BorderLayout(0, 0));
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 27) {
					self.setVisible(false);
				}
			}
		});
		
		scrollPane = new JScrollPane();
		containerPane.add(scrollPane);
		scrollPane.setViewportView(textArea);
		
		JPanel messagePanel = new JPanel();
		containerPane.add(messagePanel, BorderLayout.SOUTH);
		messagePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
		
		textFieldContainer = new JPanel();
		textFieldContainer.setBorder(new EmptyBorder(0, 0, 0, 10));
		messagePanel.add(textFieldContainer);
		textFieldContainer.setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		textFieldContainer.add(textField);
		textField.setColumns(10);
		
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					btnSendButton.doClick();
				} else if (e.getKeyCode() == 27) {
					self.setVisible(false);
				}
			}
		});
		btnSendButton = new JButton("Senden");
		btnSendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO check users black list before writing
				if(enableWriting){
					String message = textField.getText();
					displayText("[you]: " + message);
					if (message != null && message.length() > 0) {
						message = "[" + login1 + "]: [private]: " + login2 + " " + message;
						cc.sendDataString(message);
						textField.setText("");
					}
				}
			}
			
			
		});
		btnSendButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					btnSendButton.doClick();
					textField.requestFocus();
				} else if (e.getKeyCode() == 27) {
					self.setVisible(false);
				}
			}
		});
		messagePanel.add(btnSendButton);
		this.setVisible(true);
		if (msg != null && msg.length() > 0) {
			this.displayText(msg);
		}
		textField.requestFocus();
	}
	
	public String getSender() {
		return this.sender;
	}
	public String getEmpfaenger() {
		return this.empfaenger;
	}
	
	public void displayText(String text) {
		textArea.append(text + "\n");
		repaint();
	}
	public static void setWriteable(ArrayList<PrivateChatFrame> pcf , boolean value){
		for (PrivateChatFrame frame : pcf) {
			frame.enableWriting = value;
			frame.textField.setEditable(value);
		}
	}
	
	public static void update(ArrayList<PrivateChatFrame> pcf, String[] userArray) {
		for (PrivateChatFrame frame : pcf) {
			for (String user : userArray) {
				String name = user.split(" ")[0];
				String state = user.split(" ")[1];
				state = state.substring(1, state.length() - 1);
				boolean value = state.equals("online") ? true : false;
				if (frame.getEmpfaenger().equals(name)) {
					frame.enableWriting = value;
					frame.textField.setEditable(value);
					break;
				}
			}
		}		
	}
}
