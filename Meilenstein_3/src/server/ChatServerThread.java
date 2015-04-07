package server;
import java.io.*;
import java.net.*;
import core .Account;

public class ChatServerThread extends Thread {
	Socket client;
	private ChatServer server;
	private DataOutputStream out;
	private String login;
	private Account account;
	private boolean tryAgain = false;
	 ChatServerThread(ChatServer cs,Socket client) { 
		 this.client = client;
		 this.server = cs;
	 }
	 
	 public void run(){ // Bearbeitung einer aufgebauten Verbindung
		 try {
			 DataInputStream reader = new DataInputStream(client.getInputStream()); 
			 out = new DataOutputStream(client.getOutputStream()); 
			 BufferedReader in = new BufferedReader(new InputStreamReader(reader));
			 
			 login = in.readLine();
			 this.sendDataString("accepted");
			 String password = in.readLine();
			 
			 int handleResponse = server.handleAccount(login, password, this);
			 this.sendDataString(handleResponse + "" );
			 boolean online = false;
			 if (handleResponse == 0 || handleResponse == 2) {
				 online = true;
				 server.sendUpdates();
				 try {
					 if (account.isOnline()) {
						 server.publicMessage("[server]: \""+login+"\" is online!"); 
					 }		
					
				 } catch (Exception e) {
					 return;
				 } 
				 while (online) {
					 String message = in.readLine();
					 if (message.split(": ")[1].equals("[private]")) {
						String[] data = message.split(" ");
						int l = data[0].length() + data[1].length() + data[2].length() + 2;
						String empfaenger = data[2];
						String sender = data[0];
						sender = sender.substring(1, sender.length() - 2);
						message = "[private]: " + sender + " " + message.substring(l);
						server.accMessage(empfaenger, message); 
						continue;
					 } else if (message.startsWith("["+login+"]: /quit")) {
						 server.gui.addLog("log-out: user: " + login);
						 online=false;
						 continue;
					 } else if (message.startsWith("["+login+"]: /changeRoom")) {
						message = message.split(" ")[2];
						if(account != null){
							server.createNewRoom(message);
							server.changeRoomTo(message, account);
							
						}
						continue;
					} else if (message.startsWith("[" + login + "]: /rename Room")) {
						String[] data = message.split(" ");
						int res = server.renameRoom(data[1], data[2]);
						if (res == 1) {
							this.sendDataString("There is already a room with such name.");
						}
					}
					server.roomMessage(message, account.getRoom());
				 }
			 } else {
				 tryAgain = true;
			 }
			 
		 } catch (IOException e) {} // Fehler bei Ein- und Ausgabe
		 finally {
			 if ( client != null && !tryAgain ) try {
				 if (account.isOnline() == true) {					 
					 server.setAccOffline(this.login); 
				 }
				 client.close(); 
			 } catch (Exception e) { }
		 } 
	 }
	 
	 public void sendDataString (String data) {
		try {
			if (data == null) {
				return;
			}
			this.out.write((data+"\n").getBytes());
			this.out.flush();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
	}

	public void setAccount(Account account) {
		this.account=account;
	}	
		
	 
}
