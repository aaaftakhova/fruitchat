package client;
import java.io.BufferedReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class MessageListener implements Runnable {
	
	private BufferedReader in;
	private ChatClient c;	
	private ClientFrame clientframe;
	private ArrayList<PrivateChatFrame> privateChatFrames = new ArrayList<PrivateChatFrame>();
	
	MessageListener (BufferedReader in,ChatClient c, ClientFrame clientframe) {
		this.in = in;
		this.c=c;
		this.clientframe = clientframe;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				String line = in.readLine();				
				String[] lineArray = line.split(" ");
				if (lineArray[0].equals("[server]:")){
					switch (lineArray[1]){
					case "/update":
						line = line.substring(line.indexOf(" ") + 9);
						lineArray = line.split("/");
						String[] userArray = lineArray[0].split(";");
						String[] roomArray = lineArray[1].split(";");
						clientframe.update(userArray, roomArray);
						PrivateChatFrame.update(privateChatFrames, userArray);
						break;
					case "/off":
						JOptionPane.showMessageDialog(clientframe, "You're offline.", "Server message", JOptionPane.INFORMATION_MESSAGE);
						clientframe.setState("offline");
						clientframe.setMessaging(false);
						PrivateChatFrame.setWriteable(privateChatFrames, false);
						break;
					case "/ban":
						String issue = "";
						if (lineArray.length > 2) {
							issue = " Issue: " + line.substring(15);
						}
						clientframe.setState("You've been banned." + issue);
						clientframe.setMessaging(false);
						PrivateChatFrame.setWriteable(privateChatFrames, false);
						JOptionPane.showMessageDialog(clientframe, "Offline. You are banned.", "Server message", JOptionPane.INFORMATION_MESSAGE);
						break;
					case "/serverclose":
						clientframe.setState("offline");
						clientframe.setMessaging(false);
						PrivateChatFrame.setWriteable(privateChatFrames, false);
						JOptionPane.showMessageDialog(clientframe, "Server has been closed. You're offline.", "Server message", JOptionPane.INFORMATION_MESSAGE);						//
						break;
					default:
						clientframe.displayText(line);
						break;
					}
				
				} 
				//admin commands: warning
				else if (lineArray[0].equals("[admin]:")){
					JOptionPane.showMessageDialog(clientframe, line);
					clientframe.displayText(line);
				}
				//private message
				else if (lineArray[0].equals("[private]:")){
					boolean messageIsSent = false;
					String user = lineArray[1];
					line = "["+user +"]: " + line.substring(line.indexOf(user) + user.length() + 2);
					for (PrivateChatFrame frame : privateChatFrames) {
						if(frame.getEmpfaenger().equals(lineArray[1])){
							frame.setVisible(true);
							frame.displayText(line);
							messageIsSent = true;
							break;
						}
					}
					if (!messageIsSent) {
						privateChatFrames.add(new PrivateChatFrame(c, clientframe.getLogin(), user, line));
					}
				}
				//message in the current room: just display it
				else{
					clientframe.displayText(line);
				}
			} catch (Exception e) {
			}
		}
	}

	public void openPrivateDialog(String user) {
		for (PrivateChatFrame frame : privateChatFrames) {
			if (frame.getEmpfaenger().equals(user)) {
				frame.setVisible(true);
				return;
			}
		}
		privateChatFrames.add(new PrivateChatFrame(c, clientframe.getLogin(), user, ""));
	}
	
}
