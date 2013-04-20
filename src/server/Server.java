package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

public class Server implements Runnable {

	private ServerSocket s;
	private GestoreClient t;
	private JTextArea jt;
	private HashMap<String, GestoreClient> clients;
	private Lock l;
	//private Statement stat;
	private Connection conn;
	private HashMap<String, LinkedList<String>> messaggiOffline;

	public Server(JTextArea j) {
		jt = j;
		clients = new HashMap<String, GestoreClient>();
		messaggiOffline = new HashMap<String, LinkedList<String>>();
		try {
			s = new ServerSocket(8189);
		} catch (IOException e) {
			e.printStackTrace();
		}
		l = new ReentrantLock();
	}

	public void run() {

       // connessione al DB
		try {
			conn = getConnection();
			System.out.println("Connessione DB Stabilita");
			// stat = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Thread ricez = new RicezioneServer(clients, messaggiOffline, jt, l,conn);
		ricez.start();
		
		Thread notifica = new NotificaClient(clients, l,conn);
		notifica.start();
		
		
		while (true) {
			Socket incoming;
			try {
				incoming = s.accept();
				t = new GestoreClient(incoming);
				t.start();
				l.lock();
				while (!t.nomeClientPronto()) {
				}
				while (!t.passwordPronta()) {
				}

				if (!t.eNuovo()) {
					boolean pwBuona = verificaPass();
					if (pwBuona) {
						clients.put(t.getNomeClient(), t);
						t.inviaMsg("correctlogin");
						// appena il client si connette gli mandiamo tutti i
						// mess che
						// ha ricevuto quando era offline
						if (messaggiOffline.containsKey(t.getNomeClient())) {
							LinkedList<String> ll = messaggiOffline.get(t
									.getNomeClient());
							while (ll.size() > 0)
								t.inviaMsg(ll.removeFirst());
						}
					} else
						t.inviaMsg("failedlogin");
				}// se è un utente già registrato
				else {
					while (!t.emailPronta()) {
					}
					aggiungiUtente(t.getNomeClient(), t.getPassword(),
							t.getEmail());
				}
				l.unlock();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}// while

	}// run

	private void aggiungiUtente(String username, String password, String email) {
		try {
			PreparedStatement statement = conn
					.prepareStatement("INSERT INTO utentiregistrati VALUES(?,?,?);");
			
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, email);
			statement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean verificaPass() {
		boolean pwCorretta = false;

		try {
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM utentiregistrati WHERE username = ?;");
			statement.setString(1, t.getNomeClient());
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String password = result.getString(2);
				if (password.equals(t.getPassword()))
					pwCorretta = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pwCorretta;
	}

	public static Connection getConnection() throws SQLException {

		String drivers = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/utentichat";//
		String username = "root";
		String password = ".jhwh888.";

		System.setProperty("jdbc.drivers", drivers);

		return DriverManager.getConnection(url, username, password);
	}

	public void inviaMessaggio(String m, int i) {
		clients.get(i).inviaMsg(m);
	}

}
