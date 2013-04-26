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

import Utility.Send;

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
			boolean restorePw = false;
			Socket incoming;
			try {
				System.out.println("Entro nel while true del Server");
				incoming = s.accept();
				t = new GestoreClient(incoming);
				t.start();
				l.lock();
				while (!t.nomeClientPronto()) {
					System.out.println("While 1 : Server.java");
				}
				if (t.forgetPassword()){
					restorePw = true;
					String name = t.getNomeClient();
					String email = t.getEmail();
					String newPass = Utility.RandomPassword.newRandomPassword(8) ;
					String sha1Pass = Utility.Security.cryptPassword(newPass);
					int res = 0;
					try {
						PreparedStatement forgetStatement =conn.prepareStatement ("UPDATE utentiregistrati SET pass = ? WHERE username = ? AND email = ?;");
						forgetStatement.setString(1, sha1Pass);
						forgetStatement.setString(2, name);
						forgetStatement.setString(3, email);
						System.out.println(forgetStatement.toString());
						res = forgetStatement.executeUpdate();
					}
					catch (Exception e){}
					if (res>0) {
						t.inviaMsg("correctsend:" + newPass);
						try {
						Send.send("prolagd@gmail.com", email , "La tua nuova password è: " + newPass);
						}
						catch (Exception e){}
					}
					else {
						t.inviaMsg("failedsend");
					}
				}
				while (!t.passwordPronta() && !restorePw) {
					System.out.println("While 2 : Server.java");
					restorePw = false;
				}
				if (!t.eNuovo()) {
					System.out.println("non è un utente nuovo");
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
							while (ll.size() > 0){
								//System.out.println("4");
								t.inviaMsg(ll.removeFirst());
								System.out.println("inviato mess offline");
							}
						}
					} else
						t.inviaMsg("failedlogin");
				}// se è un utente già registrato
				else {
					while (!t.emailPronta()) {
						System.out.println("While 3");
					}
					
					aggiungiUtente(t.getNomeClient(), t.getPassword(),
							t.getEmail());
				}
				l.unlock();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
				System.out.println("6");
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
		String url = "jdbc:mysql://127.0.0.1:3306/utentichat";//
		String username = "root";
		String password = "root";

		System.setProperty("jdbc.drivers", drivers);

		return DriverManager.getConnection(url, username, password);
	}

	public void inviaMessaggio(String m, int i) {
		clients.get(i).inviaMsg(m);
	}

}
