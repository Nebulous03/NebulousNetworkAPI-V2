package nebulous.users;

import java.util.ArrayList;

import nebulous.network.Connection;

public class UserDatabase {
	
	public ArrayList<User> users;
	
	public UserDatabase() {
		users = new ArrayList<User>();
	}
	
	//TODO: inefficient. find a better way...
	public User getUser(Connection connection) {
		for(User user : users)
			if(user.getConnection() == connection) return user;
		return null;
	}
	
	public void add(User user) {
		users.add(user);
	}
	
	public void remove(User user) {
		users.remove(user);
	}

}
