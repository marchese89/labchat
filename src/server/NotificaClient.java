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
import java.util.concurrent.locks.ReentrantLock;
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
	private LinkedList<String> utentiAmici;
	
	public NotificaClient(HashMap<String, GestoreClient> clients,Lock l,Connection c){
		this.clients = clients;
		this.l = l;
		this.c = c;
	}
	public void run(){
		while (true){
			l.lock();
			
			try {
				ps = c.prepareStatement
						("SELECT * FROM utenti_amici WHERE utente1 = ?;");
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
					ResultSet result = ps.executeQuery();
					
					while (result.next()) {
						utentiAmici.addLast(result.getString(2));
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				sb = new StringBuilder();
				boolean almeno_uno = false;
				for(String j: st){
					if(utentiAmici.contains(j)){//inseriamo un utente se presente nella lista
					sb.append("*"+j);
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
			
			s = clients.keySet();
			for (String i:s){
				utentiAmici = new LinkedList<String>();
				try {
					ps.setString(1, i);
					ResultSet result = ps.executeQuery();
					
					while (result.next()) {
						utentiAmici.addLast(result.getString(2));
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			sb = new StringBuilder();
			for(String j:utentiAmici)
				sb.append("["+j);
			clients.get(i).inviaMsg(sb.toString());
			}//per ogni elemento nel KeySet
			
			l.unlock();
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}//while true
	}

}
