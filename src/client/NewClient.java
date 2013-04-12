package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

	public class NewClient extends Thread{

		private Scanner s;
		private PrintWriter pr;
		//private JTextArea jt;
		boolean connesso = false;
		private StringTokenizer st;
		private Socket client;
		private LinkedList<String> messaggi;
		private LinkedList<String> utentiInComunicazione;
		private volatile LinkedList<String> utentiConnessi;
		private Lock l;
		private JFrame finestraUtente;
		private String nomeClient;
		private String password;
		private String email;
		private boolean nuovoUtente;
		private LinkedList<String> listaContatti;
		
		public boolean connetti(String ip){
			boolean risultato = false;
			try{
				//instauriamo una connessione con il server ed estraiamo
				//gli strumenti per inviare e ricevere testo
	    		client = new Socket(ip, 8189);
	    		InputStream is = new DataInputStream(client.getInputStream());
	    		OutputStream os = new DataOutputStream(client.getOutputStream());
	    		s = new Scanner(is);
	    		pr = new PrintWriter(os, true);
	    		
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
	    		}catch(IOException e){
	    			e.printStackTrace();
	    		}
			
			return risultato;
		}//connetti
	    public NewClient(String nomeClient,String password,String email,boolean nU){
	    	
	    	this.nomeClient = nomeClient;
	    	this.password = password;
	    	this.nuovoUtente = nU;
	    	this.email = email;
	    	messaggi = new LinkedList<String>();
	    	utentiInComunicazione = new LinkedList<String>();
	    	utentiConnessi = new LinkedList<String>();
	    	l = new ReentrantLock();
	    	this.listaContatti = new LinkedList<String>();
	    }
	    public NewClient(String nomeClient,String password,boolean nU){
	    	
	    	this.nomeClient = nomeClient;
	    	this.password = password;
	    	this.nuovoUtente = nU;
	    	messaggi = new LinkedList<String>();
	    	utentiInComunicazione = new LinkedList<String>();
	    	utentiConnessi = new LinkedList<String>();
	    	l = new ReentrantLock();
	    	this.listaContatti = new LinkedList<String>();
	    }
		@Override
		public void run() {
			//riceviamo di continuo i messaggi dal server
			while(true){
			if(connesso){
			boolean done = false;
			while (!done && s.hasNextLine()) {
				String line = s.nextLine();		
				//stiamo ricevendo la lista degli utenti connessi
				if(line.charAt(0)=='*'){
					st = new StringTokenizer(line,"*");
				    l.lock();
					utentiConnessi.clear();
					while(st.hasMoreTokens())
						utentiConnessi.addLast(st.nextToken());
					    
					l.unlock();
				}else if(line.charAt(0)=='?'){//messaggio aggiunta contatto
					st = new StringTokenizer(line,"?");
			        String mitt = st.nextToken();
					int ris =JOptionPane.showConfirmDialog
					(null, "L'utente "+mitt+" vuole aggiungerti come contatto, Accetti");
					if(ris == JOptionPane.OK_OPTION){
						inviaMessaggio("["+nomeClient+"["+mitt);//conferma richiesta
					}
					
				}else if(line.charAt(0)=='['){
					l.lock();
					listaContatti.clear();
					st = new StringTokenizer(line, "[");
					while(st.hasMoreTokens())
						listaContatti.add(st.nextToken());
					l.unlock();
					
				}else{
				st = new StringTokenizer(line,":");
				String mittente = st.nextToken();
				if(!utentiInComunicazione.contains(mittente)){
					utentiInComunicazione.addLast(mittente);
					
				    finestraUtente = new NewClientGUI(this,mittente);
				}
				
				String msg = st.nextToken();
				messaggi.addLast(mittente+" ha scritto:\n" + msg+"\n");
				if (msg.trim().equals("bye"))
					done = true;
				
			}//else (mssaggi normali)
			}// while
			}
			}//while esterno
		}//run
		
		//invia i messaggi al server
		public void inviaMessaggio(String m){
			pr.println(m);
		}
        public boolean ciSonoMsg(){
        	return messaggi.size() > 0;
        }
	    public String riceviMsg(){
	    	return messaggi.removeFirst();
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
		public LinkedList<String> getListaContatti(){
			return listaContatti;
		}

	}



