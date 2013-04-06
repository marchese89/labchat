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
		
		public void connetti(String ip){
			try{
				//instauriamo una connessione con il server ed estraiamo
				//gli strumenti per inviare e ricevere testo
	    		client = new Socket(ip, 8189);
	    		InputStream is = new DataInputStream(client.getInputStream());
	    		OutputStream os = new DataOutputStream(client.getOutputStream());
	    		s = new Scanner(is);
	    		pr = new PrintWriter(os, true);
	    		connesso = true;
	    		pr.println("{"+nomeClient);
	    		}catch(IOException e){
	    			e.printStackTrace();
	    		}
		}
	    public NewClient(String nomeClient){
	    	
	    	this.nomeClient = nomeClient;
	    	messaggi = new LinkedList<String>();
	    	utentiInComunicazione = new LinkedList<String>();
	    	utentiConnessi = new LinkedList<String>();
	    	l = new ReentrantLock();
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
		

	}



