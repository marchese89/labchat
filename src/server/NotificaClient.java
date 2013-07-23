package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import javax.swing.JOptionPane;
/**
 * 
 * @author giovanni
 * Questa classe provvede ad inviare a ogni client connesso
 * la lista dei client con i quali può comunicare
 */
public class NotificaClient extends Thread{

	private Lock l;
	private HashMap<String, GestoreClient> clients;
	private StringBuilder sb;
	private Set<String> s;//set con tutti i client
	private Set<String> st;//set con tutti i client meno 1
	private Connection c;
	private PreparedStatement ps;
	private PreparedStatement utentiBloccati;
	private LinkedList<String> utentiAmici,lockedUsers;
	
	public NotificaClient(HashMap<String, GestoreClient> clients,Lock l,Connection c){
		this.clients = clients;
		this.l = l;
		this.c = c;
	}
	public void run(){
		
		try{
			
		
		while (true){
			l.lock();
			
			try {
				ps = c.prepareStatement
						("SELECT * FROM utenti_amici WHERE utente1 = ?;");//otteniamo la lista degli amici di utente1
				utentiBloccati = c.prepareStatement
						("SELECT utente1 FROM utenti_amici WHERE utente2 = ? AND " +
						  "bloccato_da = 1;");
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			if(clients.size()>1){
			s = clients.keySet();
			for (String i:s){
				st = new HashSet<String>(s);
				st.remove(i);
				//inserico nella linked list gli amici di uno specifico utente
				utentiAmici = new LinkedList<String>();
				
				try {
					ps.setString(1, i);
					ResultSet result = ps.executeQuery();//tutti i contatti (on line e non)
					
					while (result.next()) {
						utentiAmici.addLast(result.getString(2));
						
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				sb = new StringBuilder();
				sb.append("*");
				boolean almeno_uno = false;
				for(String j: st){
					if(utentiAmici.contains(j)){//inseriamo un utente se presente nella lista
					sb.append("¦"+j);//notifichiamo agli utenti connessi i propri contatti on line...
					almeno_uno = true;
					}
				}
				if(almeno_uno)//se c'è almeno una persona da aggiungere
				clients.get(i).inviaMsg(sb.toString());
				else
					clients.get(i).inviaMsg("* ");//lista amici vuota
				

			}//per ogni client nel set
			}//se ci sono client
			if(clients.size() == 1){//se prima c'erano 2 client e uno si disconnette...
				s = clients.keySet();
				for (String i:s)
				clients.get(i).inviaMsg("* ");
			}
			//inviamo la lista dei contatti (interroghiamo prima il DB)
			//inviamo anche la lista degli utenti che ogni utente ha bloccato
			//inviamo la lista degli utenti che hanno bloccato il nostro utente target
			s = clients.keySet();
			for (String i:s){
				utentiAmici = new LinkedList<String>();
				lockedUsers = new LinkedList<String>();
				try {
					ps.setString(1, i);
					ResultSet result = ps.executeQuery();
					utentiBloccati.setString(1, i);
					ResultSet userLocked = utentiBloccati.executeQuery();
					//aggiorniamo la lista degli utenti amci con la notifica di blocco
					while (result.next()) {
						int bloccato_da = result.getInt(3);
						if(bloccato_da ==0)//notifichiamo al client se è stato bloccato
						utentiAmici.addLast(result.getString(2)+" f");//non bloccato	
						else
							utentiAmici.addLast(result.getString(2)+" t");//bloccato
					}
					//aggiorniamo la lista degli utenti bloccati dal singolo utente
					while(userLocked.next()){
						lockedUsers.addLast(userLocked.getString(1));
					}
					//inviamo all'utente i la lista dei contatti che ha bloccato
					if(lockedUsers.size()>0){
					sb = new StringBuilder();
					sb.append("L");
					for(String u: lockedUsers){
						sb.append("¦"+u);
					}
					clients.get(i).inviaMsg(sb.toString());
					}else{
					//se sblocco l'ultimo contatto mentre sono on line ricevo la notifica
						clients.get(i).inviaMsg("L ");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (utentiAmici.size()>0){
			sb = new StringBuilder();
			sb.append("[");
			for(String j:utentiAmici)
				sb.append("¦"+j);
			clients.get(i).inviaMsg(sb.toString());
			}//per ogni elemento nel KeySet
			}
			l.unlock();
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}//while true
		
		}catch(Exception e){
			//TODO
			JOptionPane.showMessageDialog(null, "Errore",null,JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

}
