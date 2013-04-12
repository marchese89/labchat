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
import javax.swing.JTextArea;

public class RicezioneServer extends Thread {
	private HashMap<String, GestoreClient> clients;
	private JTextArea jt;
	private StringTokenizer st;
	private volatile Set<String> chiavi;
	private Lock l;
	private HashMap<String,LinkedList<String>> messaggiOffline;
	private Connection conn;
	private PreparedStatement statement;
	private PreparedStatement statementInsert;
	private PreparedStatement removeStatement;
	
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
					.prepareStatement("SELECT * FROM utentiregistrati WHERE username = ?;");
			statementInsert = conn.prepareStatement("INSERT INTO utenti_amici VALUES(?,?)");
			removeStatement = conn.prepareStatement
					("DELETE FROM utenti_amici WHERE utente1=? AND utente2 =?");
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
						}else if(messaggio.charAt(0)=='A'){//messaggio addUser
							   
						       st = new StringTokenizer(messaggio,":");
						       st.nextToken();//eliminiamo 'A'
						       try {
						    	   //interroghiamo il database
								statement.setString(1, st.nextToken());
								ResultSet result = statement.executeQuery();
								System.out.println("query eseguita");
								while(result.next()){
									String user = result.getString(1);
									//inviamo la richiesta all'utente interessato
									if(clients.containsKey(user)){
									clients.get(user).inviaMsg("?"+j);
									
									}
									else{
										if(!messaggiOffline.containsKey(user))
										messaggiOffline.put(user, new LinkedList<String>());
										
										messaggiOffline.get(user).addLast("?"+j);
									}
										
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						
						}else if(messaggio.charAt(0)=='['){//inseriamo 2 utenti amici
							st = new StringTokenizer(messaggio,"[");
							String utente1 = st.nextToken();
							String utente2 = st.nextToken();
							try {
								//inseriamo nel DB le 2 possibili coppie
								statementInsert.setString(1, utente1);
								statementInsert.setString(2, utente2);
								statementInsert.execute();
								statementInsert.setString(1, utente2);
								statementInsert.setString(2, utente1);
								statementInsert.execute();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							
							
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
						}else{
						st = new StringTokenizer(messaggio,":");
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
				
		}//while true
	}
}
