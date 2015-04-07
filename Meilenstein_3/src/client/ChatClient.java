package client;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class ChatClient {
	private DataOutputStream out;
	@SuppressWarnings("unused")
	private boolean run = true;
	private ClientFrame clientFrame;
	private Socket server;
	private MessageListener ml;
	private String[] loginData;   //0=server; 1=login; 2=password;
	private ClientLoginFrame loginFrame;
	public ChatClient () {
		server = null;
		BufferedReader in = null;
		try {
			clientFrame = new ClientFrame(this);
			loginFrame = new ClientLoginFrame(clientFrame);
			boolean signedIn = false;
			boolean serverFound = false;
			while (!signedIn) {				
				while (!serverFound) {
					//get and check input
					loginData = loginFrame.getLoginData();
					if(loginData==null || loginData[0].length() == 0 || loginData[1].length() == 0 || loginData[2].length() == 0)
						System.exit(0); //unhandled error!
					
					//clear server, in/out-streams
					if (server != null) server.close();
					if (in != null) in.close();
					if (out != null) {out.flush(); out.close();}
					
					//set up client socket
					clientFrame.setLogin(loginData[1]);
					try {
						server = new Socket(loginData[0], 21284);
						serverFound = true;
					} catch (UnknownHostException e) {
						server = null;
						serverFound = false;
						JOptionPane.showMessageDialog(null, "Could not connect to \"" + loginData[0] + "\". This server doesn't exist.");
					}
					catch (ConnectException e) {
						server = null;
						serverFound = false;
						JOptionPane.showMessageDialog(null, "Could not connect to \"" + loginData[0] + "\". This server is closed.");
					}
				}				
				//set up in/out-streams
				DataInputStream reader = new DataInputStream(server.getInputStream()); 
				out = new DataOutputStream(server.getOutputStream()); 
				in = new BufferedReader(new InputStreamReader(reader));

				//try to sign in using input
				this.sendDataString(loginData[1]);
				String isReady = in.readLine();
				//clientFrame.displayText(isReady);
				if (isReady.length() == 0) {
					clientFrame.displayText("Fehler eingetreten!");
					server.close();
					System.exit(0);
				}
				this.sendDataString(loginData[2]);

				//get server response
				isReady = in.readLine();
				int response = Integer.parseInt(isReady);
				//handle on response
				switch (response) {
				case 0:
					//sign in successfull
					loginFrame.setVisible(false);
					clientFrame.setVisible(true);
					clientFrame.setState("online");
					signedIn = true;
					break;
				case 1:
					//false password
					signedIn = false;
					serverFound = false;
					loginFrame.showFalsePassword();
					break;
				case 2:
					//create new account and sign in
					loginFrame.setVisible(false);
					clientFrame.setVisible(true);
					clientFrame.setState("New account created. You're online.");
					signedIn = true;
					break;
				case 3:
					signedIn = false;
					serverFound = false;
					//this is a banned account
					loginFrame.showBannedInfo();
					break;
				default:; //smth got wrong
				}
			}

			
			//deal with it
			 
			//create message listener
			ml = new MessageListener(in, this, clientFrame); 
			new Thread(ml).start();
			//write messages, more messages!
			try {
				while (true) {
					if(clientFrame.isSent()){
						String message = clientFrame.getInputText();
						this.sendDataString("["+loginData[1]+"]: "+message);
						clientFrame.messageSent();
					}
				 }
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		} catch (IOException e) {
			 e.printStackTrace();
		} // Fehler bei Ein-und Ausgabe
	 	finally { 
			if (server != null) try {
				server.close(); 
			} catch ( IOException e ) {} 
		}
	}
	
	public void sendDataString (String data) {
		try {
			this.out.write((data+"\n").getBytes());
			this.out.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}	
	}	
	
	public static void main(String[] args) {
		new ChatClient();
	}
	public void setRun(boolean bool){
		run = bool;
	}
	public void close() throws IOException {
		loginFrame.close();
		clientFrame.dispose();
		server.close();
		System.exit(0);
	}

	public void openPrivateDialog(String user) {		
		if (ml != null) {
			ml.openPrivateDialog(user);
		}
	}

	public void setLoginData(String[] strings) {
		this.loginData = strings;
	}
}
