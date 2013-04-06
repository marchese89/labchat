package server;

/**
 * questa classe riceve tutti i messaggi che i client inviano
 * ( perchè i client sono connessi direttamente solo con il server)
 * e li smista ai rispettivi destinatari. Riceve anche il messaggio 
 * speciale per la disconnessione di un client
 * 
 */
import java.util.HashMap;
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
	public RicezioneServer(HashMap<String, GestoreClient> clients, JTextArea jt,Lock l) {
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
			
				for (String j : chiavi) {
					
					if (clients.get(j).ciSonoMsg()) {
						String messaggio = clients.get(j).riceviMsg();
						if (messaggio.equals("disconnect")){//messaggio di disconnessione
							chiavi.remove(j);
							clients.remove(j);
							//System.out.println("l'utente "+j+" si è disconnesso");
							break;
						}else{
						st = new StringTokenizer(messaggio,":");
						String dest = st.nextToken();
						String msg = st.nextToken();
						
						if (dest.equals("server")) {
							jt.append("Il Client " + j + " ha scritto:\n" + msg
									+ "\n");
						} else {
							
							clients.get(dest).inviaMsg(
									j + ":" + msg);
						}
						}//se non è un messaggio di disconnessione
					}//se ci sono messaggi nella coda di ogni client
				}//iterazione su tutte le chiavi
			l.unlock();
				
		}//while true
	}
}
