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
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
    private NotificaClient notifica;
    private RicezioneServer ricez;
    
	public Server(JTextArea j) {
		jt = j;
		clients = new HashMap<String, GestoreClient>();
		try {
			s = new ServerSocket(8189);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Errore",null,JOptionPane.ERROR_MESSAGE);
			return;
		}
		l = new ReentrantLock();
	}

	public void run() {

       // connessione al DB
		try {
			conn = getConnection();
			jt.append("Connessione DB Stabilita \n");
			// stat = conn.createStatement();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, "Errore",null,JOptionPane.ERROR_MESSAGE);
			return;
		}
		try{
		ricez = new RicezioneServer(clients, jt, l,conn);
		ricez.start();
		}catch(Exception e){
			
		}
		notifica = new NotificaClient(clients, l,conn);
		notifica.start();
		
		while (true) {
			boolean restorePw = false;
			Socket incoming;
			try {
				incoming = s.accept();
				t = new GestoreClient(incoming);
				t.start();
				l.lock();
				while (!t.nomeClientPronto()) {
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
					restorePw = false;
				}
				if (!t.eNuovo()) {
					boolean pwBuona = verificaPass();
					if (pwBuona) {
						if(!clients.containsKey(t.getNomeClient())){
						clients.put(t.getNomeClient(), t);
						
						t.inviaMsg("correctlogin");
						}else
							t.inviaMsg("failedlogin");
						
						Object [][] off = ricez.sendOff(t.getNomeClient());
						if (off !=null){
						for (int i = 0; i<off.length; i++) {
							@SuppressWarnings("unchecked")
							LinkedList<String> ll = (LinkedList<String>) off[i][1];
							if (ll.size()>1) {
								StringTokenizer mitlist = new StringTokenizer(ll.removeFirst(), ",");
								while (mitlist.hasMoreTokens())
									t.inviaMsg("mn¦"+off[i][0]+"¦"+mitlist.nextToken());
							for (String j : ll){
								StringTokenizer st = new StringTokenizer(j,"-");
								String mit = st.nextToken();
								String mes = st.nextToken();
								t.inviaMsg("m¦" + off[i][0] + "¦" + mit + "¦" + mes);
							}
						}
						}
						}
						// appena il client si connette gli mandiamo tutti i
						// mess che
						// ha ricevuto quando era offline
					} else
						t.inviaMsg("failedlogin");
				}// se è un utente già registrato
				else {
					while (!t.emailPronta()) {
					}
					
					boolean res =aggiungiUtente(t.getNomeClient(), t.getPassword(),
							t.getEmail());
					if(res){
						t.inviaMsg("correctlogin");
					}else{
						t.inviaMsg("failedlogin");
					}
				}
				l.unlock();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Errore",null,JOptionPane.ERROR_MESSAGE);
				return;
			}

		}// while

	}// run

	private boolean aggiungiUtente(String username, String password, String email) {
		try {
			PreparedStatement statement = conn
					.prepareStatement("INSERT INTO utentiregistrati(username,pass,email) VALUES(?,?,?);");
			
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, email);
			statement.execute();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	private boolean verificaPass() {
		boolean pwCorretta = false;

		try {
			PreparedStatement statement = conn
					.prepareStatement("SELECT username,pass,email FROM utentiregistrati WHERE username = ?;");
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

	private static Connection getConnection() throws SQLException {

		String drivers = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://127.0.0.1:3306/utentichat";//
		String username = "root";
		String password = ".jhwh888.";

		System.setProperty("jdbc.drivers", drivers);

		return DriverManager.getConnection(url, username, password);
	}
    /*
	public void inviaMessaggio(String m, int i) {
		clients.get(i).inviaMsg(m);
	}
    */
}
