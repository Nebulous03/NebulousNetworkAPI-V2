package nebulous.users;

import java.io.Serializable;
import java.util.ArrayList;

import nebulous.network.Connection;

public class User implements Serializable{
	
	public enum Choice { HAWK, DOVE, NONE }

	private transient static final long serialVersionUID = 300L;
	
	private transient Connection connection = null;
	private transient boolean online = false;
	
	private ArrayList<Choice> history = null;
	private String username = null;
	
	public User(String username, Connection connection) {
		this.username = username;
		this.connection = connection;
	}

	public void send(Object data) {
		connection.send(data);
	}
	
	public void request(Object data) {
		connection.sendReceive(data);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public ArrayList<Choice> getHistory() {
		return history;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public Connection getConnection() {
		return connection;
	}

}
