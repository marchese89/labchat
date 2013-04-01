package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NotificaClient extends Thread{

	private Lock l;
	private HashMap<Integer, GestoreClient> clients;
	private StringBuilder sb;
	Set<Integer> s;//set con tutti i client
	private Set<Integer> st;//set con tutti i client meno 1
	
	public NotificaClient(HashMap<Integer, GestoreClient> clients,Lock l){
		this.clients = clients;
		this.l = l;
	}
	public void run(){
		while (true){
			l.lock();
			if(clients.size()>1){
			s = clients.keySet();
			for (Integer i:s){
				st = new HashSet<Integer>(s);
				st.remove(i);
				sb = new StringBuilder();
				for(Integer j: st){
					sb.append("*"+j);
				}
				clients.get(i).inviaMsg(sb.toString());
				
			}//per ogni client nel set
			}//se ci sono client
			l.unlock();
			
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}//while true
	}

}
