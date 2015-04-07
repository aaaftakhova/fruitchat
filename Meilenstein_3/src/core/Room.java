package core;

public class Room {
	
	private String name;
	private int users;
	
	public Room (String name) {
		this.name = name;
		users = 0;
	}
	
	public int getUsers () {
		return users;
	}
	public String getName () {
		return name;
	}
	public void setName (String name) {
		this.name = name;
	}
	public String getRoomString () {
		return name + "(" + users + ")";
	}
	public void addUser () {
		users++;
	}
	public void removeUser() {
		users--;
	}
	public void changeRoomName (String name) {
		this.name = name;
	}	
}
