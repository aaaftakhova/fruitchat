package client;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PrivateChatList extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String selected = "";
	public PrivateChatList(final ClientFrame cf, DefaultListModel<String> onlineUsers) {
		super(cf);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		int dx = 200;
	    int dy = 300;
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - dx)/2, (dim.height - dy)/2, dx, dy);
		setResizable(false);
		setTitle("Online Users List");
		getContentPane().setLayout(new BorderLayout(10, 10));
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		final JList<String> list = new JList<String>(onlineUsers);
		scrollPane.setViewportView(list);
		
		JButton btnOpenPrivateDialog = new JButton("private chat");
		contentPane.add(btnOpenPrivateDialog, BorderLayout.SOUTH);
		btnOpenPrivateDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (list.getSelectedIndex() != -1) {
					selected = list.getSelectedValue().toString();
					setVisible(false);
				} else {
					JOptionPane.showMessageDialog(null, "No user was selected");
				}
			}
		});
	}


	public static String getUser(ClientFrame cf, DefaultListModel<String> OnlineUsersList) {
		PrivateChatList list = new PrivateChatList(cf, OnlineUsersList);
		list.setVisible(true);
		return selected;
	}
}