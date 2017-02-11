package nebulous.network;

import java.io.IOException;
import java.net.Socket;

import nebulous.utils.Console;

public abstract class Client {
	
	protected Connection connection = null;
	protected boolean stopped = false;
	
	public Client(String address, short port) {
		
		try {
			this.connection = new Connection(new Socket(address, port));
		} catch (IOException e) {
			Console.printErr("Clinet/Error", "Failed to bind to port " + port + "!");
			e.printStackTrace();
		}
		
	}
	
	public abstract void onConnection();
	public abstract void onDisconnection();
	
	public abstract void onIncomingData(Object data);
	public abstract void onOutgoingData(Object data);

	public void start() {
		
		connection.startListening();
		
		Thread clientHandler = new Thread("Client_Handler_Thread") {
			
			@Override
			public void run() {
				while(!stopped) {
					Object data = connection.getNextObject();
					if(data == null) {
						onDisconnection();
						close();
					} else {
						onIncomingData(data);
					}
				}
			}
			
		};
		
		clientHandler.start();
		
		onConnection();
	}
	
	public void close() {
		connection.close();
		stopped = true;
	}
	
	public void send(Object object) {
		connection.send(object);
		onOutgoingData(object);
	}
	
	public Object request(Object object) {
		Object requested = connection.sendReceive(object);
		onOutgoingData(object);
		return requested;
	}
	
	public Connection getConnection() {
		return connection;
	}

}
