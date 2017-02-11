package nebulous.network;

import nebulous.requests.Request;
import nebulous.requests.Request.RequestType;
import nebulous.requests.UserRequest;
import nebulous.users.User;
import nebulous.users.User.Choice;
import nebulous.utils.Console;

public class HawkDoveClient extends Client{
	
	public static final String ADDRESS = "localhost";
	public static final short  PORT	   = 31416;
	
	public static String username = "Default_Username";
	public static User   user     = null;
	
	public static void main(String[] args) {
		
		if(args.length < 1) {
			Console.println("Usage: filename.jar [username]");
		} else {
			username = args[0];
			Client client = new HawkDoveClient(ADDRESS, PORT);
			client.start();
		}
		
	}

	public HawkDoveClient(String address, short port) {
		super(address, port);
		user = new User(username, connection);
	}

	@Override
	public void onIncomingData(Object data) {
		
		Console.println("RECEIVED " + data.getClass().getName());
		Console.println(((Request)data).getRequestType());
		
		if(((Request)data).getRequestType() == RequestType.LOGIN) {
			Console.println("Client", "Sending login data to the server...");
			send(user);
		} else if (((Request)data).getRequestType() == RequestType.USER) {
			User opponent = (User)request(new UserRequest());
			send(onPlay(opponent));
		}
	}

	@Override
	public void onOutgoingData(Object data) {
		if(data instanceof Choice) {
			Console.println("Client", "Sent choice of " + (Choice)data);
		}
	}
	
	public Choice onPlay(User opponentUser) {
		return Choice.HAWK;
	}

	@Override
	public void onConnection() {
		Console.println("Client", "Successfully connected to the server.");
	}

	@Override
	public void onDisconnection() {
		Console.println("Client", "User disconnected.");
	}

}
