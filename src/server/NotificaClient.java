package server;

import java.util.HashMap;
import java.util.HashSet;
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
	Set<String> s;//set con tutti i client
	private Set<String> st;//set con tutti i client meno 1
	
	public NotificaClient(HashMap<String, GestoreClient> clients,Lock l){
		this.clients = clients;
		this.l = l;
	}
	public void run(){
		while (true){
			l.lock();
			if(clients.size()>1){
			s = clients.keySet();
			for (String i:s){
				st = new HashSet<String>(s);
				st.remove(i);
				sb = new StringBuilder();
				for(String j: st){
					sb.append("*"+j);
				}
				clients.get(i).inviaMsg(sb.toString());
				
			}//per ogni client nel set
			}//se ci sono client
			if(clients.size() == 1){//se prima c'erano 2 client e uno si disconnette...
				s = clients.keySet();
				for (String i:s)
				clients.get(i).inviaMsg("* ");
			}
			l.unlock();
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}//while true
	}

}
