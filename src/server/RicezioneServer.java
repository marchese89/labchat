package server;

/**
 * questa classe riceve tutti i messaggi che i client inviano
 * ( perchè i client sono connessi direttamente solo con il server)
 * e li smista ai rispettivi destinatari. Riceve anche il messaggio 
 * speciale per la disconnessione di un client
 * 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class RicezioneServer extends Thread {
	private HashMap<String, GestoreClient> clients;
	private JTextArea jt;
	private StringTokenizer st;
	private volatile Set<String> chiavi;
	private Lock l;
	private HashMap<String,LinkedList<String>> messaggiOffline;
	private Connection conn;
	private PreparedStatement statement,statementInsert,removeStatement,
	                          lockStatement,unlockStatement;

	
	public RicezioneServer(HashMap<String, GestoreClient> clients,
			HashMap<String,LinkedList<String>> messaggiOffline,
			JTextArea jt,Lock l,Connection conn) {
		this.clients = clients;
		this.jt = jt;
		this.l = l;
		this.messaggiOffline = messaggiOffline;
		this.conn = conn;
	}
    //iteriamo sulle chiavi dell'HashMap e riceviamo i messaggi
	//smistandoli ai rispettivi destinatari
	public void run() {
		
		try {
			statement = conn
					.prepareStatement("SELECT username,pass,email FROM utentiregistrati WHERE username = ?;");
			statementInsert = conn.prepareStatement("INSERT INTO utenti_amici VALUES(?,?,?);");
			removeStatement = conn.prepareStatement
					("DELETE FROM utenti_amici WHERE utente1=? AND utente2 =?;");
			lockStatement = conn.prepareStatement
					("UPDATE utenti_amici SET bloccato_da = 1 WHERE utente1 = ? AND utente2 = ?;");
			unlockStatement = conn.prepareStatement
					("UPDATE utenti_amici SET bloccato_da = 0 WHERE utente1 = ? AND utente2 = ?;");
			
					
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		while (true) {
			l.lock();
			chiavi = clients.keySet();
			if (chiavi.size() > 0)
			
				for (String j : chiavi) {
					
					if (clients.get(j).ciSonoMsg()) {
						String messaggio = clients.get(j).riceviMsg();
						if (messaggio.equals("disconnect")){//messaggio di disconnessione
							chiavi.remove(j);
							clients.remove(j);
							//System.out.println("l'utente "+j+" si è disconnesso");
							break;
						}
						else if(messaggio.charAt(0)=='U'){
							st = new  StringTokenizer(messaggio,"U");
							String toUnlock = st.nextToken();
							try{
								unlockStatement.setString(1, toUnlock);//utente da sbloccare
								unlockStatement.setString(2, j);//utente che sblocca
								unlockStatement.execute();
							}catch (SQLException e) {
								e.printStackTrace();
							}
						}else if(messaggio.charAt(0)=='L'){
							st = new  StringTokenizer(messaggio,"L");
							String toLock = st.nextToken();
							try {
								lockStatement.setString(1, toLock);//utente da bloccare
								lockStatement.setString(2, j);//utente che blocca
								lockStatement.execute();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}else if (messaggio.charAt(0) == 'ç') {
							String client = messaggio.substring(2,messaggio.length());
							getSuspendedList(client);
						}
						else if (messaggio.charAt(0)=='<'){//visualizzato alle...
							st = new StringTokenizer(messaggio,"<");
                            st.nextToken();//rimuoviamo il mittente
							st.nextToken();//rimuoviamo l'ora
							String destinatario = st.nextToken();
							if(clients.containsKey(destinatario)){
								clients.get(destinatario).inviaMsg(messaggio);
								}else{//il client è offline
									if(!messaggiOffline.containsKey(destinatario))
									messaggiOffline.put(destinatario, new LinkedList<String>());
									messaggiOffline.get(destinatario).addLast(messaggio);
								}
						}else if(messaggio.charAt(0)=='A'){//messaggio addUser
							   System.out.println("ricevuto mess addUser");
						       st = new StringTokenizer(messaggio.substring(2,messaggio.length()),",");
						       String mittente = st.nextToken();
						       String destinatario = st.nextToken();
						       try {
						    	   //interroghiamo il database
								statement.setString(1, destinatario);
								System.out.println(statement.toString());
								ResultSet result = statement.executeQuery();
								System.out.println("Query eseguita: " );
								while(result.next()){
									String user = result.getString(1);
									if(clients.containsKey(user)){
										System.out.println("L'utente: " + user + " è online e invio la richiesta");
										newSuspendedRequest(mittente,destinatario);
									clients.get(user).inviaMsg("?"+j);
									
									}
									else{
										System.out.println("L'utente: " + user + " non è online e invio la richiesta");
										newSuspendedRequest(mittente,destinatario);
									}
										
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						
						}else if(messaggio.charAt(0)=='['){//inseriamo 2 utenti amici
							boolean add = messaggio.charAt(2)=='Y';
							st = new StringTokenizer(messaggio.substring(4,messaggio.length()),",");
							String utente1 = st.nextToken();
							String utente2 = st.nextToken();
							if (add){
							try {
								//inseriamo nel DB le 2 possibili coppie
								statementInsert.setInt(3,0);
								statementInsert.setString(1, utente1);
								statementInsert.setString(2, utente2);
								statementInsert.execute();
								statementInsert.setString(1, utente2);
								statementInsert.setString(2, utente1);
								statementInsert.execute();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							}
							removeFromSuspended(utente1, utente2);
							
						}else if(messaggio.charAt(0)=='R'){//messaggio rimozione utente
							st = new StringTokenizer(messaggio,"R");
							String utente1=j;
							String utente2 =st.nextToken();
							try{
								//rimuoviamo le due coppie di utenti dal DB
								removeStatement.setString(1, utente1);
								removeStatement.setString(2, utente2);
								removeStatement.execute();
								removeStatement.setString(1, utente2);
								removeStatement.setString(2, utente1);
								removeStatement.execute();
							}catch(SQLException e){
								e.printStackTrace();
							}
						}else if (messaggio.charAt(0)=='M'){
						st = new StringTokenizer(messaggio,"M:");
						String dest = st.nextToken();
						String msg = st.nextToken();
						
						if (dest.equals("server")) {
							jt.append("Il Client " + j + " ha scritto:\n" + msg
									+ "\n");
						} else {
							if(clients.containsKey(dest)){
							clients.get(dest).inviaMsg(
									j + ":" + msg);
							}else{//il client è offline
								if(!messaggiOffline.containsKey(dest))
								messaggiOffline.put(dest, new LinkedList<String>());
								messaggiOffline.get(dest).addLast(j + ":" + msg);
							}
						}
						}//se non è un messaggio di disconnessione
					}//se ci sono messaggi nella coda di ogni client
				}//iterazione su tutte le chiavi
			l.unlock();
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}//while true
	}
		private void removeFromSuspended(String id, String applicant) {
			try {
				PreparedStatement stringToId = conn.prepareStatement("SELECT id FROM utentiregistrati WHERE username = ?");
				PreparedStatement remove = conn.prepareStatement("DELETE FROM suspended_request WHERE id = ? && applicant = ?");
				stringToId.setString(1, id);
				ResultSet r = stringToId.executeQuery();
				r.next();
				String final_id = r.getString(1);
				remove.setString(1, final_id);
				remove.setString(2, applicant);
				remove.execute();
					}
					catch (SQLException e) {}
				
			}
		
		private void newSuspendedRequest(String mittente, String destinatario) {
			try {
		PreparedStatement stringToId = conn.prepareStatement("SELECT id FROM utentiregistrati WHERE username = ?");
		PreparedStatement verify = conn.prepareStatement("SELECT id FROM suspended_request WHERE id = ? && applicant = ?");
		PreparedStatement insert = conn.prepareStatement("INSERT INTO suspended_request VALUES (?,?)");
		stringToId.setString(1, destinatario);
		ResultSet r = stringToId.executeQuery();
		r.next();
		String id = r.getString(1);
		verify.setString(1,id);
		verify.setString(2,mittente);
		r = verify.executeQuery();
		if (!r.next()){
		insert.setString(1, id);
		insert.setString(2,mittente);
		insert.execute();
			}
			}
			catch (SQLException e) {}
		
	}
		private void getSuspendedList(String client) {
		/*
		 * Codice sicuramente funzionante (Testato);
		 */
		try {
			PreparedStatement stringToId =conn.prepareStatement ("SELECT id FROM utentiregistrati WHERE username = ?");
			PreparedStatement list =conn.prepareStatement ("SELECT applicant FROM suspended_request WHERE id = ?");
			stringToId.setString(1, client);
			ResultSet r = stringToId.executeQuery();
			r.next();
			String id = r.getString(1);
			list.setString(1,id);
			r = list.executeQuery();
			StringBuilder sb = new StringBuilder();
			while (r.next()){
				sb.append(r.getString(1)+",");
			}
			clients.get(client).inviaMsg("ç:"+sb.toString());

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
