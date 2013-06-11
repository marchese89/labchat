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
	private HashMap<Integer, LinkedList<String>> group;
	private StringTokenizer st;
	private volatile Set<String> chiavi;
	private Lock l;
	
	/* Variabili di istanza della conversazione offline */
	private HashMap<String, LinkedList<Integer>> nameUser;
	private HashMap<Integer, LinkedList<String>> offlineMes;
	/* Fine variabili di istanza delle conversazioni offline */
	
	private Connection conn;
	private PreparedStatement statement,statementInsert,removeStatement,
	                          lockStatement,unlockStatement;
	private int id = 1; //Da modificare: gestire la chiusura di una chat. Altrimenti il numero di elementi dell'hashmap group sarà molto alto.
	
	private int getID () {
		int holdId = id;
		id = id + 1;
		return holdId;
	}
	public RicezioneServer(HashMap<String, GestoreClient> clients,
			JTextArea jt,Lock l,Connection conn) {
		group = new HashMap<Integer, LinkedList<String>>();
		this.clients = clients;
		this.jt = jt;
		this.l = l;
		nameUser = new HashMap<String, LinkedList<Integer>>();
		offlineMes = new HashMap<Integer, LinkedList<String>>();
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
						jt.append(j+"\n");
						String messaggio = clients.get(j).riceviMsg();
						if (messaggio.equals("disconnect")){//messaggio di disconnessione
							chiavi.remove(j);
							clients.remove(j);
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
						
						/* Messaggio "visualizzato alle" che funziona solo nel caso di conversazioni a due utenti" */
						
						else if (messaggio.charAt(0)=='<'){//visualizzato alle...
							st = new StringTokenizer(messaggio,"<");
                            String mit = st.nextToken();//rimuoviamo il mittente
							st.nextToken();//rimuoviamo l'ora
							Integer id = Integer.parseInt(st.nextToken());
							if(group.containsKey(id) && group.get(id).size()<=2){
								mit = (!group.get(id).getFirst().equals(mit)) ? group.get(id).getFirst() : group.get(id).getLast();
								jt.append("Il server riceve il messaggio e lo rispedisce (Ricezione Server) \n");
								if (clients.containsKey(mit))
								clients.get(mit).inviaMsg(messaggio);
								}
							else if (!(group.get(id).size()==2)) {
								jt.append("Il server riceve il messaggio ma la conversazione di id" + id + "  non è a due utenti! Ma è a: "+ group.get(id).size() + " utente/i " + "\n");
								for (String i : group.get(id))
									jt.append(i+", ");
									jt.append("\n");
							}
							else {jt.append("Group non contiene quella conversazione \n");}
						}
						
						/** Parte che si occupa della rimozione di un utente dalla conversazione quando chiude la finestra */
						else if (messaggio.charAt(0)=='&'){
							st = new StringTokenizer(messaggio,"&");
							int id = Integer.parseInt(st.nextToken());
							String userToRemove = st.nextToken();
							LinkedList<String> al = group.get(id);
							if (!offlineMes.containsKey(id) && group.get(id).size()>2) { // Se la conversazione non è una conversazione offline ed è una conversazione di gruppo
								for (String i : al){
									if (!i.equals(userToRemove))
									clients.get(i).inviaMsg("&"+id+"&"+userToRemove);
								}
							group.get(id).remove(userToRemove);
							if (group.get(id).size()==0) group.remove(id);
							}
						}
						
						/** Fine parte che si occupa della rimozione di un utente dalla conversazione quando chiude la finestra */
						else if (messaggio.charAt(0)=='(' && messaggio.charAt(1)==':') {//invio la lista dei destinatari a partire dall'id.
							st = new StringTokenizer(messaggio,"(:");
							int id = Integer.parseInt(st.nextToken());
							String mitt = st.nextToken();
							StringBuilder sb = new StringBuilder();
							for (String i : group.get(id))
								sb.append(":"+i);
							clients.get(mitt).inviaMsg("(:"+sb.toString());
						}
						else if(messaggio.charAt(0)=='A'){//messaggio addUser
						       st = new StringTokenizer(messaggio.substring(2,messaggio.length()),",");
						       String mittente = st.nextToken();
						       String destinatario = st.nextToken();
						       try {
						    	   //interroghiamo il database
								statement.setString(1, destinatario);
								ResultSet result = statement.executeQuery();
								while(result.next()){
									String user = result.getString(1);
									if(clients.containsKey(user)){
										newSuspendedRequest(mittente,destinatario);
									clients.get(user).inviaMsg("?"+j);
									
									}
									else{
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
						} 
						/* Gestione dell'invio dei messaggi in chat. */
						
						else if (messaggio.substring(0,3).equals("ri^")){
							StringTokenizer st = new StringTokenizer (messaggio, "^");
							st.nextToken();
							String mittente = st.nextToken();
							String destinatario = st.nextToken();
							int id = getID();
							LinkedList<String> ll = new LinkedList<String>();
							ll.add(mittente); 
							ll.add(destinatario);
							group.put(id, ll);
							if (clients.containsKey(destinatario)) {
								clients.get(destinatario).inviaMsg("mn:"+id+":"+mittente);
							}
							else { // si tratta di un messaggio offline 
								if (!nameUser.containsKey(destinatario)) {
									nameUser.put(destinatario, new LinkedList<Integer>());
									nameUser.get(destinatario).add(id);
									offlineMes.put(id, new LinkedList<String>());
								}
								else {
									nameUser.get(destinatario).add(id);
									offlineMes.put(id, new LinkedList<String>());
								}
								
							}
							clients.get(mittente).inviaMsg("^"+id);
						}
						else if (messaggio.charAt(0) =='l') {
							StringTokenizer st = new StringTokenizer(messaggio, "^");
							st.nextToken();
							int id = Integer.parseInt(st.nextToken());
							String userToAdd = st.nextToken();
							for (String i: group.get(id))
								if (clients.containsKey(i))
								clients.get(i).inviaMsg("l;" + id + ";" + userToAdd);
							StringBuilder sb = new StringBuilder();
							for (String i: group.get(id))
								sb.append(":"+i);
							clients.get(userToAdd).inviaMsg("mn:"+id+sb.toString());
							group.get(id).add(userToAdd);
						}
						else if (messaggio.charAt(0)=='m'){
							StringTokenizer st = new StringTokenizer(messaggio, ";");
							st.nextToken();
							int id = Integer.parseInt(st.nextToken());
							String mitt = st.nextToken();
							String message = st.nextToken();
							for (String i : group.get(id)) {
								if (!i.equals(mitt) && clients.containsKey(i)) // è online.
									clients.get(i).inviaMsg("m:" + id + ":" + mitt + ":" + message );
								if (!clients.containsKey(i)){ // se non è online
									if (!nameUser.containsKey(i)) { // La seguente condizione verrà spiegata in basso
										LinkedList<Integer> ll = new LinkedList<Integer>();
										ll.add(id);
										nameUser.put(i, ll);
									}
									if (offlineMes.containsKey(id)) { // Stessa cosa del commento in basso riferito ai messaggi piuttosto che all'utente.
									offlineMes.get(id).addLast(mitt + "-" +message);
									}
									else {
										offlineMes.put(id, new LinkedList<String>());
										offlineMes.get(id).addLast(mitt+"-"+message);
									}
									/*
									 * Quella condizione può accadere quando l'utente A invia un messaggio all'utente B. L'utente B si collega ma A ora non è più online.
									 * Ora sarà A a dover essere inserito nella lista. Quella condizione fa questa cosa.
									 */
								}
							}
							
						}
						
						/* Fine gestione messaggi chat. */						
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
		public Object[][] sendOff(String user) {
			//Usare opportuni strumenti di mutua esclusione.
			if (nameUser.containsKey(user)){
			Object [][] off = new Object[nameUser.get(user).size()][2];
			for (int i = 0; i<off.length; i++) {
				int id = nameUser.get(user).get(i);
				off[i][0] = id;
				LinkedList<String> ll = new LinkedList<String>();
				StringBuilder sb = new StringBuilder();
				for (String x : group.get(id)) {
					if (!x.equals(user))
						sb.append(x + ",");
				}
				ll.addFirst(sb.toString());
				for (String m : offlineMes.get(id))
					ll.addLast(m);
				off[i][1] = ll;
			}
			LinkedList<Integer> ll = nameUser.remove(user);
			boolean exist = false;
			Set<String> key = nameUser.keySet();
			for (Integer i : ll) {
				for (String j : key) {
					if (nameUser.get(j).contains(i)) exist = true;
				}
				if (!exist) offlineMes.remove(i);
				exist = false;
			}
			
			return off;
			}
			return null;
		}
}

