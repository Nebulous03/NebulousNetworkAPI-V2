package nebulous.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import nebulous.utils.Console;

public abstract class Server {
	
	private short PORT = 0;
	
	private ServerSocket serverSocket = null;
	private ArrayList<Connection> connections = null;
	
	private boolean stopped = false;
	
	public Server(short port) {
		this.PORT = port;
		this.connections = new ArrayList<Connection>();
	}
	
	public abstract void onServerStart();
	public abstract void onServerStop();
	
	public abstract void onUserConnected(Connection connection);
	public abstract void onUserDisconnected(Connection connection);
	
	public abstract void onIncomingData(Connection connection, Object data);
	public abstract void onOutgoingData(Connection connection, Object data);
	
	public synchronized void start() {
		
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			Console.printErr("Server/Error", "Failed to bind to port " + PORT + "!");
			e.printStackTrace();
		}
		
		startConnectionThread();
		onServerStart();
		
	}
	
	public synchronized void stop() {
		
		onServerStop();
		
		for(Connection connection : connections)
			connection.close();
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			Console.printErr("Server/Error", "Failed to close server socket!");
			e.printStackTrace();
		}
		
		stopped = true;
		
	}
	
	private void startConnectionThread() {
		
		Thread connectionThread = new Thread("ServerConnectionThread") {
			
			@Override
			public void run() {
				
				while(true) {
					try {
						
						Socket incoming = serverSocket.accept();
						Connection connection = new Connection(incoming);
						connections.add(connection);
						connection.startListening();
						
						onUserConnected(connection);
						
						Thread clientHandler = new Thread("Client_Handler_Thread_" + connection.getRemotePort()) {
							
							@Override
							public void run() {
								while(!stopped) {
									Object data = connection.getNextObject();
									if(data == null){
										onUserDisconnected(connection);
									} else {
										onIncomingData(connection, data);
									}
								}
							}
							
						};
						
						clientHandler.start();

					} catch (IOException e) {
						break;
					}
				}
				
			}
			
		};
		
		connectionThread.start();
	}
	
	public void send(Connection connection, Object data) {
		try {
			connection.getOutputStream().writeObject(data);
			connection.getOutputStream().flush();
			onOutgoingData(connection, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendAll(Object data) {
		for(Connection connection : connections) {
			send(connection, data);
			onOutgoingData(connection, data);
		}
	}
	
	public short getPort() {
		return PORT;
	}
	
	public String getLocalAddress() {
		return serverSocket.getInetAddress().getHostAddress();
	}
	
	public String getFullLocalAddress() {
		return getLocalAddress() + ":" + PORT;
	}

}
