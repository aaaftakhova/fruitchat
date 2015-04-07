package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.io.File;
import java.io.IOException;

public class ClientLoginFrame extends JDialog {

	private static final long serialVersionUID = -6022215087836283330L;
	private String server="";
	private String pass="";
	private String login="";
	private JPanel contentPane;
	private HintTextField server_textField;
	private HintTextField login_textField;
	private HintPwField password_textField;
	private ClientFrame cf;
	/**
	 * Create the frame.
	 */
	public ClientLoginFrame(final ClientFrame s) {
		super(s);
		setResizable(false);
		cf = s;
		try {
			setIconImage(ImageIO.read(new File("./data/gfx/fruitchat.gif")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
	    int dx = 420;
	    int dy = 150;
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - dx)/2, (dim.height - dy)/2, dx, dy);
		setTitle("Login on Fruit Chat");
		getContentPane().setLayout(new BorderLayout(5, 5));
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(420, 150));
		getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel inputFieldsPanel = new JPanel();
		contentPane.add(inputFieldsPanel);
		inputFieldsPanel.setLayout(null);
		server_textField = new HintTextField("Server IP");
		server_textField.setBounds(10, 10, 400, 25);
		inputFieldsPanel.add(server_textField);
		server_textField.setColumns(10);
		
		login_textField = new HintTextField("Login");
		login_textField.setBounds(10, 45, 400, 25);
		inputFieldsPanel.add(login_textField);
		login_textField.setColumns(10);
		
		password_textField = new HintPwField("Password");
		password_textField.setBounds(10, 80, 400, 25);
		inputFieldsPanel.add(password_textField);
		password_textField.setColumns(10);
		
		JButton connect_button = new JButton("Connect");
		connect_button.setBounds(10, 115, 400, 25);
		inputFieldsPanel.add(connect_button);
		connect_button.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				((JButton) e.getSource()).doClick();
			}
		});
		connect_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				server = server_textField.getText();
				pass = password_textField.getText();
				login = login_textField.getText();
				if (server.length() == 0 || pass.length() == 0 || login.length() == 0) {
					JOptionPane.showMessageDialog(null, "Not all fields were filled. Please, try again!", "Warning" , JOptionPane.INFORMATION_MESSAGE);
					server="";
					pass="";
					login="";
					return;
				}
				String regExPattern = "^[a-zA-Z0-9]*$";
				boolean loginNotAllowed = login.equals("admin") || login.equals("server") || login.equals("private");
				if (!login.matches(regExPattern) || loginNotAllowed) {
					JOptionPane.showMessageDialog(null, "Login may only contain lettes and numbers. Please, try again!", "Warning", JOptionPane.INFORMATION_MESSAGE);
					server="";
					pass="";
					login="";
					return;
				}
				setVisible(false);
			}
		});
		setMinimumSize(new Dimension(420, 150));
		pack();
	}

	@Override public void dispose () {
		try {
			cf.closeCC();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public void close() {
		System.exit(0);
	}
	
	public void showFalsePassword () {
		String message = "The login or password is false. Please, try again.";
		String title = "Information";
		JOptionPane.showMessageDialog(null,message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void showBannedInfo () {
		String message = "You're banned. Sorry!";
		String title = "Information";
		JOptionPane.showMessageDialog(null,message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * @return server, login, pass
	 */
	public String[] getLoginData(){
		this.setVisible(true);
		return new String[] {server, login, pass};
	}
}
