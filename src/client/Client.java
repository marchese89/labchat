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
		private HashMap<String,LinkedList<String>> messaggi;//chiave: mittente mess...
		private LinkedList<String> utentiInComunicazione;
		private LinkedList<String> utentiCheHoBloccato;
		private volatile LinkedList<String> utentiConnessi;
		private Lock l;
		private HashMap<String,JFrame> finestreUtenti; 
		private String nomeClient;
		private String password;
		private String email;
		private boolean nuovoUtente;
		private LinkedList<String> listaContatti, suspendedUser;
		private Semaphore sem = new Semaphore(0);
		
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
	    	messaggi = new HashMap<String,LinkedList<String>>();
	    	utentiInComunicazione = new LinkedList<String>();
	    	utentiConnessi = new LinkedList<String>();
	    	l = new ReentrantLock();
	    	this.listaContatti = new LinkedList<String>();
	    	utentiCheHoBloccato = new LinkedList<String>();
	    	finestreUtenti = new HashMap<String,JFrame>();
	    }
	    public Client(String nomeClient,String password,boolean nU){
	    	
	    	this.nomeClient = nomeClient;
	    	this.password = password;
	    	this.nuovoUtente = nU;
	    	messaggi = new HashMap<String,LinkedList<String>>();
	    	utentiInComunicazione = new LinkedList<String>();
	    	utentiConnessi = new LinkedList<String>();
	    	l = new ReentrantLock();
	    	this.listaContatti = new LinkedList<String>();
	    	utentiCheHoBloccato = new LinkedList<String>();
	    	finestreUtenti = new HashMap<String,JFrame>();
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
					
				}else if (line.charAt(0)== '<'){
					st = new StringTokenizer(line,"<");
					String mitt = st.nextToken();
					if(messaggi.containsKey(mitt))
						messaggi.get(mitt).addLast(line);
					else{
						messaggi.put(mitt, new LinkedList<String>());
						messaggi.get(mitt).addLast(line);
					}
					
				}else{
				st = new StringTokenizer(line,":");
				String mittente = st.nextToken();
				if(!utentiInComunicazione.contains(mittente)){
					utentiInComunicazione.addLast(mittente);
					
				    JFrame f = new ClientGUI(this,mittente,false);
				    finestreUtenti.put(mittente, f);
				}
				
				String msg = st.nextToken();
				if(messaggi.containsKey(mittente))
				messaggi.get(mittente).addLast((mittente+" ha scritto:\n" + msg+"\n"));
				else{
					messaggi.put(mittente, new LinkedList<String>());
					messaggi.get(mittente).addLast(mittente+" ha scritto:\n" + msg+"\n");
				}
				if (msg.trim().equals("bye"))
					done = true;
				
			}//else (mssaggi normali)
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
        public boolean ciSonoMsg(String mitt){
        	if(messaggi.containsKey(mitt))
        	return messaggi.get(mitt).size() > 0;
        	else return false;
        }
	    public String riceviMsg(String mitt){//riceviamo messaggi dal mittente selezionato
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
		public String login(){
			String dest = JOptionPane.showInputDialog("destinatario");
			utentiInComunicazione.addLast(dest);
			return dest;
		}
		public void disconnetti(){
			pr.println("disconnect");
			connesso = false;
		}
		public synchronized LinkedList<String> getListaContatti(){
			    return listaContatti;

		}
		
		public void setFont(Font f){
			Set<String> utenti = finestreUtenti.keySet();
			for(String i: utenti){
				finestreUtenti.get(i).setFont(f);
			}
		}
		public void setForeground(Color c){
			Set<String> utenti = finestreUtenti.keySet();
			for(String i: utenti){
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



