package server;

import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

public class RicezioneServer extends Thread {
	private HashMap<Integer, GestoreClient> clients;
	private JTextArea jt;
	private StringTokenizer st;
	private Set<Integer> chiavi;
	private Lock l;
	public RicezioneServer(HashMap<Integer, GestoreClient> clients, JTextArea jt,Lock l) {
		this.clients = clients;
		this.jt = jt;
		this.l = l;
	}
    //iteriamo sulle chiavi dell'HashMap e riceviamo i messaggi
	//smistandoli ai rispettivi destinatari
	public void run() {
		while (true) {
			l.lock();
			chiavi = clients.keySet();
			if (chiavi.size() > 0)
				for (Integer j : chiavi) {
					// for (int j = 1; j <= clients.size(); j++) {
					if (clients.get(j).ciSonoMsg()) {
						String messaggio = clients.get(j).riceviMsg();
						
						st = new StringTokenizer(messaggio,":");
						String dest = st.nextToken();
						String msg = st.nextToken();
						
						if (dest.equals("server")) {
							jt.append("Il Client " + j + " ha scritto:\n" + msg
									+ "\n");
						} else {
							
							clients.get(Integer.parseInt(dest)).inviaMsg(
									j + ":" + msg);
						}
						
					}//se ci sono messaggi nella coda di ogni client
				}//iterazione su tutte le chiavi
			l.unlock();
		}//while true
	}
}
