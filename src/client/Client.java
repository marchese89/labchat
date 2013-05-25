package client;

import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

	public class Client extends Thread{

		private Scanner s;
		private PrintWriter pr;
		private boolean connesso = false;
		private StringTokenizer st;
		private Socket client;
		//variabili chat di gruppo
		
		private HashMap<Integer,LinkedList<String>> messaggi = new HashMap<Integer,LinkedList<String>>();//chiave: mittente mess...
		private HashMap<Integer,Set<String>> usersgroup = new HashMap<Integer,Set<String>>(); //id e destinatari
		private HashMap<Integer,ClientGUI> finestreUtenti = new HashMap<Integer,ClientGUI>();
		
		//fine variabili di chat di gruppo
		//private LinkedList<String> utentiInComunicazione;
		private LinkedList<String> utentiCheHoBloccato;
		private volatile LinkedList<String> utentiConnessi;
		private Lock l;
	
		private String nomeClient;
		private String password;
		private String email;
		private boolean nuovoUtente;
		private LinkedList<String> listaContatti, suspendedUser;
		private Semaphore sem = new Semaphore(0);
		private Semaphore list = new Semaphore(0);
		private Semaphore id = new Semaphore(0);
		private Set<String> temp = new HashSet<String>();
		private int ID = -1;
		
		public void addUser (int id, String newuser){
			inviaMessaggio("l^" + id +"^" + newuser);
		}
		public void sendMessage (int id, String message){
			inviaMessaggio("m;"+id+";"+nomeClient+";"+message);
		}
		public void addDest(String dest, Font f, Color c, boolean ghost) { 
			inviaMessaggio("ri^"+nomeClient+"^"+dest);
			try {
				id.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(id + " ID");
			if (ID!=-1) {
			Set<String> ll = new HashSet<>();
			ll.add(dest);
			usersgroup.put(ID, ll);
			messaggi.put(ID, new LinkedList<String>());
			ClientGUI f1 = new ClientGUI(this,ll,ghost, ID,nomeClient);
			finestreUtenti.put(ID, f1);
			ID = -1;
			System.out.println("aggiunto un client");
			}
			else {
				throw new IllegalArgumentException ("ID = -1");
			}
		}
		
		private void restorerConnection (String ip) {
			try {
			client = new Socket(ip, 8189);
    		InputStream is = new DataInputStream(client.getInputStream());
    		OutputStream os = new DataOutputStream(client.getOutputStream());
    		s = new Scanner(is);
    		pr = new PrintWriter(os, true);
			}
			catch (Exception e) { }
		}
		
		public String forgetPassword(String ip){
			String res = null;
			restorerConnection (ip); // da modificare
			pr.println("§"+":"+nomeClient+":"+email);
			boolean end = false;
    		while(!end && s.hasNextLine()){
    			String result = s.nextLine();
    			if (result.indexOf("correctsend")>= 0 ) {
    				StringTokenizer st = new StringTokenizer(result,":");
    				st.nextToken();
    				end = true;
    			    res = new String(st.nextToken());
    			}
    			else{
    				end = true;
    				res = null;
    			}
    				
    		}
			return res;
		}
		
		public boolean connetti(String ip){
			restorerConnection (ip);
			boolean risultato = false;
			try{
	    		if(nuovoUtente){//se ci stiamo registrando inviamo un mess speciale
	    			pr.println("N"+":"+nomeClient+":"+password+":"+email);
	    			risultato = true;
	    		}else{
	    		pr.println("{"+nomeClient+"{"+password);
	    		
	    		boolean done = false;
	    		while(!done && s.hasNextLine()){
	    			String result = s.nextLine();
	    			if(result.equals("correctlogin")){
	    				connesso = true;
	    				done = true;
	    			    risultato = true;
	    			}
	    			if(result.equals("failedlogin")){
	    				done = true;
	    				risultato = false;
	    			}
	    				
	    		}
	    		}
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
			
			return risultato;
		}//connetti
		public Client (String nomeClient, String email) {
			this.nomeClient = nomeClient;
			this.email = email;
		}
	    public Client(String nomeClient,String password,String email,boolean nU){
	    	this.nomeClient = nomeClient;
	    	this.password = password;
	    	this.nuovoUtente = nU;
	    	this.email = email;
	    	messaggi = new HashMap<Integer,LinkedList<String>>();
	    //	utentiInComunicazione = new LinkedList<String>();
	    	utentiConnessi = new LinkedList<String>();
	    	l = new ReentrantLock();
	    	this.listaContatti = new LinkedList<String>();
	    	utentiCheHoBloccato = new LinkedList<String>();
	    	finestreUtenti = new HashMap<Integer,ClientGUI>();
	    }
	    public Client(String nomeClient,String password,boolean nU){
	    	this.nomeClient = nomeClient;
	    	this.password = password;
	    	this.nuovoUtente = nU;
	    	messaggi = new HashMap<Integer,LinkedList<String>>();
	    //	utentiInComunicazione = new LinkedList<String>();
	    	utentiConnessi = new LinkedList<String>();
	    	l = new ReentrantLock();
	    	this.listaContatti = new LinkedList<String>();
	    	utentiCheHoBloccato = new LinkedList<String>();
	    	finestreUtenti = new HashMap<Integer,ClientGUI>();
	    }
		@Override
		public void run() {
			//riceviamo di continuo i messaggi dal server
			while(true){
			if(connesso){
			boolean done = false;
			while (!done && s.hasNextLine()) {
				String line = s.nextLine();	/*
				JOptionPane.showMessageDialog(null, null,
						line,
						JOptionPane.ERROR_MESSAGE);*/
				//stiamo ricevendo la lista degli utenti connessi
				if(line.charAt(0)=='*'){
					
					st = new StringTokenizer(line,"*");
				    l.lock();
					utentiConnessi.clear();
					while(st.hasMoreTokens())
						utentiConnessi.addLast(st.nextToken());
					    
					l.unlock();
					
				}else if(line.charAt(0)=='ç'){
					suspendedUser = new LinkedList<String>();
					String users = line.substring(2,line.length());
					StringTokenizer st = new StringTokenizer(users, ",");
					while (st.hasMoreTokens()){
						suspendedUser.add(st.nextToken());
					}
					sem.release();
				}
				else if(line.charAt(0)=='L'){//lista dei contatti che ho bloccato
					
					st = new StringTokenizer(line,"L");
					l.lock();
					utentiCheHoBloccato.clear();
					while(st.hasMoreTokens())
				         utentiCheHoBloccato.addLast(st.nextToken());
					l.unlock();
					
				}else if(line.charAt(0)=='?'){//messaggio aggiunta contatto
					JOptionPane.showMessageDialog(null,
							"Qualcuno ti ha aggiunto ai propri amici! Aggiorna la lista delle richieste sospese!");
				}
				else if(line.charAt(0)=='['){//messaggio lista contatti con blocchi e non
					l.lock();
					listaContatti.clear();
					st = new StringTokenizer(line, "[");
					while(st.hasMoreTokens())
						listaContatti.add(st.nextToken());
					l.unlock();
					
				}
				/*
				 * Parte modificata da bruno_scrivo : cambiare i caratteri di controllo
				 */
				else if (line.charAt(0)=='m'){ //ricevuto un nuovo messaggio
					StringTokenizer st = new StringTokenizer(line, ":");
					st.nextToken();
					int id = Integer.parseInt(st.nextToken());
					String mittente = st.nextToken();
					
					if (line.charAt(1)=='n'){ // se sta iniziando ora la conversazione
						Set<String> ll = new HashSet<String>();
						ll.add(mittente);
						while (st.hasMoreTokens())
							ll.add(st.nextToken());
						usersgroup.put(id, ll);
						messaggi.put(id, new LinkedList<String>());
						finestreUtenti.put(id, new ClientGUI(this,ll,false,id,nomeClient));
					}
					else {
						String messaggio = st.nextToken();
						if (!finestreUtenti.containsKey(id)) {
							inviaMessaggio("(:" + id+"(:" + nomeClient); //richiedo la lista degli utenti di quella conversazione
							try {
								list.acquire();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							usersgroup.put(id, temp);
							messaggi.put(id, new LinkedList<String>());
							finestreUtenti.put(id, new ClientGUI(this,temp,false,id,nomeClient));
						}
					finestreUtenti.get(id).setVisible(true);
					messaggi.get(id).add(mittente + " ha scritto: " + messaggio);
					}
				}
				else if (line.charAt(0)=='^') { //Fatto
					ID = Integer.parseInt(line.substring(1,line.length()));
					id.release();
				}
				else if (line.charAt(0)=='(' && line.charAt(1) == ':') {
					String mess = line.substring(2,line.length());
					StringTokenizer st = new StringTokenizer (mess,":");
					while (st.hasMoreTokens())
						temp.add(st.nextToken());
					list.release();
				}
				
				else if (line.charAt(0)=='&'){
					st = new StringTokenizer(line,"&");
					int id = Integer.parseInt(st.nextToken());
					String userToRemove = st.nextToken();
					Set<String> al = usersgroup.get(id);
					usersgroup.get(id).remove(userToRemove);
					finestreUtenti.get(id).append("L'utente " + userToRemove + " ha abbandonato la conversazione");
					finestreUtenti.get(id).aggiorna();
					}
				else if (line.charAt(0)=='l'){ //Fatto. Il server invia al client un messaggio per aggiungere un utente alla conversazione
					StringTokenizer st = new StringTokenizer(line,";");
					st.nextToken();
					int id = Integer.parseInt(st.nextToken());
					String addUser = st.nextToken();
					usersgroup.get(id).add(addUser);
					finestreUtenti.get(id).aggiorna();
					finestreUtenti.get(id).setVisible(true);
					messaggi.get(id).add("##"+addUser);
				}
				/*
				 * Fine parte modificata.
				 */
				
				else if (line.charAt(0)== '<'){ //visualizzato alle...
					st = new StringTokenizer(line,"<");
                    st.nextToken();//rimuoviamo il mittente
					st.nextToken();//rimuoviamo l'ora
					Integer id = Integer.parseInt(st.nextToken());
					if (usersgroup.containsKey(id) && usersgroup.get(id).size()==1){
						System.out.println("L'altro client riceve il messaggio dal server e si appronta a stampare visualizzato alle...");
						messaggi.get(id).addLast(line);
					}
					else {
						System.out.println("L'altro client riceve il messaggio dal server ma la conversazione non sembra a due utenti");
					}
				}
					
			}// while
			}
			try {
				sleep(1000);
			}
			catch (Exception e){}
			}//while esterno
			
		}//run
		
		//invia i messaggi al server
		public void inviaMessaggio(String m){
			pr.println(m);
		}
        public boolean ciSonoMsg(int mitt){
        	if(messaggi.containsKey(mitt))
        	return messaggi.get(mitt).size() > 0;
        	else return false;
        }
	    public String riceviMsg(int mitt){//riceviamo messaggi dal mittente selezionato
	    	return messaggi.get(mitt).removeFirst();
	    }
	    
		public boolean eConnesso(){
			return connesso;
		}
		public boolean nuovoUtente(){
			return nuovoUtente;
		}
		/*
		public void aggiungiUtente(String u){
			utentiInComunicazione.addLast(u);
		}
		*/
		public synchronized LinkedList<String> utentiConnessi(){
			return utentiConnessi;
		}
		/*
		public String login(){
			String dest = JOptionPane.showInputDialog("destinatario");
			utentiInComunicazione.addLast(dest);
			return dest;
		}*/
		public void disconnetti(){
			pr.println("disconnect");
			connesso = false;
		}
		public synchronized LinkedList<String> getListaContatti(){
			    return listaContatti;

		}
		
		public void setFont(Font f){
			Set<Integer> utenti = finestreUtenti.keySet();
			for(Integer i: utenti){
				finestreUtenti.get(i).setFont(f);
			}
		}
		public void setForeground(Color c){
			Set<Integer> utenti = finestreUtenti.keySet();
			for(Integer i: utenti){
				finestreUtenti.get(i).setForeground(c);
			}
		}
		public String getNomeClient(){
			return nomeClient;
		}
		public Lock getLockListaContatti(){
			return l;
		}
		public LinkedList<String> getUtentiBloccati(){
			return utentiCheHoBloccato;
		}

		public LinkedList<String> getSuspendedList() {
			inviaMessaggio("ç:"+nomeClient);
			try {
				sem.acquire();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return suspendedUser;
		}
		

	}



