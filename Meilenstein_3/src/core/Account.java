package core;

import java.util.ArrayList;
import server.ChatServerThread;

public class Account {
	private String login, password, room;
	private boolean online;
	private ChatServerThread cst;
	
	public Account (String login, String password, String room) {
		this.online = false;
		this.room = room;
		this.login = login;
		this.password = password;
		this.cst = null;
	}
	public Account (String login, String password, String room, ChatServerThread cst, ArrayList<Room> rooms) {
		this.online = true;
		this.room = room;
		rooms.get(0).addUser();
		cst.setAccount(this);
		this.setCST(cst);
		this.setLogin(login);
		this.setPassword(password);
	}
	public int setRoom(String room, ArrayList<Room> rooms) {
		int id = -1;
		if (this.room != null && !room.equals(this.room)) {
			for (Room r : rooms) {
				if ((this.room).equals(r.getName())) {
					id = rooms.indexOf(r); 
				}
			}
		}
		for (Room r : rooms) {
			if (room.equals(r.getName())) {
				this.room = room;
				r.addUser();
				if (id > -1) {
					rooms.get(id).removeUser();
				}
				return 0;
			}
		}
		return 1;
	}
	public void updateRoomName (String newName) {
		room = newName;
	}
	public String getRoom() {
		return room;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setCST(ChatServerThread cst) {
		this.cst = cst;
	}
	public ChatServerThread getCST() {
		return cst;
	}
	public String toString () {
		return this.login+" ("+(online?"online":"offline")+", "+room+")";
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
}
