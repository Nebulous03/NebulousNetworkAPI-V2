package nebulous.network;

import nebulous.requests.LoginRequest;
import nebulous.users.User;
import nebulous.users.UserDatabase;
import nebulous.utils.Console;

public class HawkDoveServer extends Server{
	
	public static final short PORT = 31416;
	
	public UserDatabase database = null;
	
	public User playerOne = null;
	public User playerTwo = null;
	
	public static void main(String[] args) {
		HawkDoveServer server = new HawkDoveServer(PORT);
		server.start();
	}

	public HawkDoveServer(short port) {
		super(port);
		database = new UserDatabase();
	}

	@Override
	public void onServerStart() {
		Console.println("Server", "Server started successfully...");
	}

	@Override
	public void onServerStop() {
		Console.println("Server", "Server stopped successfully...");
	}

	@Override
	public void onUserConnected(Connection connection) {
		
		User user = database.getUser(connection);
		
		if(user == null) {
			
			// User will be null if sendRecieve() times out.
			Object test = connection.sendReceive(new LoginRequest());
			
			if(test == null) {
				Console.println("Server", "New user attempted to connect, but timed out...");
			} else {
				database.add((User)test);
				Console.println("Server", "Registered new user -> " + ((User)test).getUsername());
			}
			
		}
		
		if(user != null) {
			user.setOnline(true);
			Console.println("Server", "User " + user.getUsername() + " is now online.");
		}
		
	}

	@Override
	public void onUserDisconnected(Connection connection) {
		User user = database.getUser(connection);
		Console.println("Server", "User " + user.getUsername() + " is now offline.");
		user.setOnline(false);
	}

	@Override
	public void onIncomingData(Connection connection, Object data) {
		
	}

	@Override
	public void onOutgoingData(Connection connection, Object data) {
		
	}

}
