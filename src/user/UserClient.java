package user;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserClient {
	
	private Utente user;
	private Client c;
	
	class Client implements Runnable {
		
		private InputHandler inHandler = null;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private String ip;
		private int port;
		private Socket clientSocket;
		
		public Client (String ip, int port) {
			this.ip = ip;
			this.port = port;
			System.out.println("Creato client");
		}
		
		public void setObjectForInputHandler (Object o) throws IOException{
			inHandler.sendData(o);
		}

		
		public void shutdown() throws IOException, InterruptedException {
			if (!clientSocket.isClosed()) {
				clientSocket.close();
				in.close();
				out.close();
				Thread.sleep(10000);
				System.exit(0);
			}
		}
		
		@Override
		public void run() {
			System.out.println("In run client");
			try {
				clientSocket = new Socket(ip, port);
				System.out.println("Creato client socket.");
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				in = new ObjectInputStream(clientSocket.getInputStream());
				inHandler = new InputHandler();
				Thread t = new Thread(inHandler);
				t.start();
				
				inHandler.sendData(user);

				Object o;
				
				while ((o = in.readObject()) != null) { //reading objects...
					switch (o.getClass().getSimpleName()) {
					case "String":
						System.out.println((String)o);
						break;
					}
				}
				
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				try {
					shutdown();
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				} 
			} 
			

			
		}
		
		
		class InputHandler implements Runnable {

			private boolean finished = false;

			@Override
			public void run() {	
				try {
					while (!finished) {
						
					} 
				} catch (Exception e) {
					try {
						shutdown();
					} catch (IOException | InterruptedException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				
			}
			
			public void sendData (Object s) throws IOException {
				out.writeObject(s);
				out.flush();
				out.reset();
			}

			
		}

	}
}
