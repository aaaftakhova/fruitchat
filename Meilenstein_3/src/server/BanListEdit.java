package server;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import java.awt.Rectangle;

public class BanListEdit extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> bannedUsers = new ArrayList<String>();
	private JList<Object> list;
	private DefaultListModel<Object> listModel;
	
	public BanListEdit(final ChatServer cs) {
		setMinimumSize(new Dimension(150, 150));
		setTitle("Ban List");
		getContentPane().setLayout(new BorderLayout(0, 0));
		int height = 300;
		int width = 200;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((d.width - width)/2, (d.height - height)/2, width, height);
		try {
			BufferedReader in = new BufferedReader(new FileReader (new File ("./data/banned.txt")));
			String line;
			while ((line = in.readLine()) != null) {
				bannedUsers.add(line);
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listModel = new DefaultListModel<>();
		
		JPanel containerPane = new JPanel();
		containerPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(containerPane, BorderLayout.CENTER);
		containerPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		containerPane.add(scrollPane, BorderLayout.CENTER);
		
		list = new JList<Object>(listModel);
		scrollPane.setViewportView(list);
		
		JButton btnDeban = new JButton("rehabilitate");
		btnDeban.setBounds(new Rectangle(0, 10, 0, 0));
		containerPane.add(btnDeban, BorderLayout.SOUTH);
		btnDeban.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int l = list.getSelectedIndex();
				if (l > -1) {
					String name = list.getSelectedValue().toString();
					bannedUsers.remove(l);
					saveNewBannedList(bannedUsers);
					FillModelList(bannedUsers);
					cs.rehabilitate(name);
				} else {
					JOptionPane.showMessageDialog(null, "no user was selected");
				}
			}
		});
		FillModelList(bannedUsers);
	}
	
	public void saveNewBannedList (ArrayList<String> users) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File ("./data/banned.txt")));
			for (String string : users) {
				bw.append(string);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void FillModelList (ArrayList<String> users) {
		this.listModel.clear();
		for (String s : users) {
			this.listModel.addElement(s);
		}
		repaint();
	}
	
}
