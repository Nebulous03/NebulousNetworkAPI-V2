package nebulous.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import nebulous.utils.Console;

public class Connection {
	
	private Socket SOCKET = null;

	private ObjectInputStream  inputStream  = null;
	private ObjectOutputStream outputStream = null;
	
	public Object lastReceived = null;
	
	private boolean requesting = false;
	private boolean closed = false;
	
	public Connection(Socket socket) {
		
		this.SOCKET = socket;
		
		try {
			
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void startListening() {
		
		Thread listenThread = new Thread("ConnectionListenThread_" + getRemotePort()) {
			
			@Override
			public void run() {
				while(true) {

					try {
						lastReceived = read();
					} catch (IOException e) {
						close();
					}
					
				}
			}
			
		};
		
		listenThread.start();
	}
	
	public Object read() throws IOException {
		
		try {
			
			this.lastReceived = inputStream.readObject();
			Console.println("(read())Received " + lastReceived.getClass().getName());	// rm
			lastReceived = null;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return lastReceived;
	}
	
	public void send(Object data) {
		try {
			Console.println("(send())Sent " + data.getClass().getName());	// rm
			outputStream.writeObject(data);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object sendReceive(Object data) {
		send(data);
		return waitForObject(3000);
	}
	
	public Object getNextObject() {
		
//		Object nextObject = null;
		
		while(true) {
			
			if(this.closed) {
				return null;
			}
			
			if(this.lastReceived != null) {
				Console.print("(getNext())" + lastReceived.getClass().getName()); //rm
			}
			
			if(this.lastReceived != null){
//				nextObject = this.lastReceived;
				Console.print("(getNext())Received " + lastReceived.getClass().getName());	// rm
				break;
			}
		}
		
		//this.lastReceived = null;
		
		return lastReceived;
	}
	
	public Object waitForObject(int timeout) {
		
		Object nextObject = null;
		
		double start = System.currentTimeMillis();
		double end = start + timeout;
		
		double currentTime = 0;
		
		requesting = true;
		
		while(true) {
			
			currentTime = System.currentTimeMillis();
			
			if(currentTime >= end) {
				requesting = false;
				return null;
			}
			
			if(lastReceived != null){
				requesting = false;
				nextObject = lastReceived;
				Console.println("(wait())Received " + lastReceived.getClass().getName());	// rm
				break;
			}
			
		}
		
		//lastReceived = null;
		return nextObject;
		
	}
	
	public void close() {
		try {
			SOCKET.close();
			closed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket getSocket() {
		return SOCKET;
	}

	public int getLocalPort() {
		return SOCKET.getLocalPort();
	}
	
	public int getRemotePort() {
		return SOCKET.getPort();
	}
	
	public String getLocalAddress() {
		return SOCKET.getLocalAddress().getHostAddress();
	}
	
	public String getRemoteAddress() {
		return SOCKET.getRemoteSocketAddress().toString();
	}
	
	public ObjectInputStream getInputStream() {
		return inputStream;
	}

	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}
	
}
