package database;

import user.Utente;
import org.json.simple.JSONObject;
import utility.JSONUtility;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {
	
	private JSONObject jsonObject = JSONUtility.readJsonFile("src/memoria.json");
	private Server s;
	
	public Database () {
		System.out.println("Creato database server.");
		
		s = new Server();
		Thread t = new Thread(() -> {
			try {
				s.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		t.start();
	}
	
	
	public boolean passwordCorretta (String user, String password) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		return (utente.get("password").equals(password));
	}
	
	public boolean usernameEsiste (String username) {
		return !(jsonObject.get(username).equals(null)); 
	}
	
	@SuppressWarnings("unchecked")
	public void modificaCredenziali (Utente user, String username, String password) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		utente.put("username", username);
		utente.put("password", password);
		
		JSONUtility.aggiornaJsonFile(jsonObject);
	}
	
	@SuppressWarnings("unchecked")
	public void primoAccessoEseguito (Utente user) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		utente.put("primo-accesso", true);
		
		JSONUtility.aggiornaJsonFile(jsonObject);
	}
	
	class Server implements Runnable {
		
		private static final int SERVER_PORT = 25565;
		private ServerSocket serverSocket;
		private ArrayList<ConnectionHandler> connections;
		private ExecutorService pool;
		private HashMap<Utente, ConnectionHandler> userClients;
		
	
		public Server () {
			connections = new ArrayList<>();
			userClients = new HashMap<>();
		}

		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(SERVER_PORT);
				pool = Executors.newCachedThreadPool(); 
				System.out.println("Accettando client...");
				while (true) {
					Socket client = serverSocket.accept();
					ConnectionHandler handler = new ConnectionHandler(client);
					connections.add(handler);
					System.out.println("Nuovo client connesso!");
					pool.execute(handler); 
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void broadcastObj(Object o) throws IOException {
			for (ConnectionHandler c : connections) {
				System.out.println("Ciclo broadcastObj");
				c.sendObj(o);
			}
		}
		
		public void removeChFromConnections (ConnectionHandler ch) {
			connections.remove(ch);
		}
		
		public void shutdownServer() throws IOException {
			if (!serverSocket.isClosed()) serverSocket.close();
			for (ConnectionHandler ch : connections) ch.shutdown();
		}
		
		class ConnectionHandler implements Runnable {
			
			private Socket clientSocket;
			private ObjectOutputStream outData;
			private ObjectInputStream inData;
			private Utente user;
			
			public Utente getUser() {
				return user;
			}

			public ConnectionHandler (Socket s) {
				clientSocket = s;
			}
			
			@Override
			public void run() {
				try {
					Object o = null;
					outData = new ObjectOutputStream(clientSocket.getOutputStream());
					inData = new ObjectInputStream(clientSocket.getInputStream());
					sendObj("Benvenuto!");
					while ((o = inData.readObject()) != null) {
						switch (o.getClass().getSimpleName()) {
						case "String":
							System.out.println((String)o);
							broadcastObj((String)o);
							break;
						}
					}
				}
				catch (IOException | ClassNotFoundException e) {
					connections.remove(this); //viene rimosso connectionhandler
					try {
						broadcastObj(user.getUsername() + " si è disconnesso."); //viene comunicato uscita del client
						userClients.remove(user);
						shutdown();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println("Rimosso connectionhandler.");
				}
			}
			
			public void sendObj(Object o) throws IOException {
				outData.writeObject(o);
				outData.flush();
				outData.reset();
			}

			
			public void shutdown() throws IOException {
				outData.close();
				inData.close();
				if (!clientSocket.isClosed()) {
					System.out.println("Client disconnesso.");
					clientSocket.close();
				}
			}
			
		}	
		
	}
}
