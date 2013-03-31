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
import javax.swing.JFrame;
import javax.swing.JOptionPane;

	public class NewClient extends Thread{

		private Scanner s;
		private PrintWriter pr;
		//private JTextArea jt;
		boolean connesso = false;
		private StringTokenizer st;
		private String destinatario;
		private Socket client;
		private LinkedList<String> messaggi;
		private LinkedList<String> utentiInComunicazione;
		private JFrame finestraUtente;
		
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
	    		}catch(IOException e){
	    			e.printStackTrace();
	    		}
		}
	    public NewClient(){
	    	
	    	messaggi = new LinkedList<String>();
	    	utentiInComunicazione = new LinkedList<String>();
	    }
		@Override
		public void run() {
			//riceviamo di continuo i messaggi dal server
			while(true){
			if(connesso){
			boolean done = false;
			while (!done && s.hasNextLine()) {
				String line = s.nextLine();
				//System.out.println("stringa ricevuta "+line);
			
				st = new StringTokenizer(line,":*",true);
				if(st.countTokens()==4){
					this.destinatario = st.nextToken();
					pr.println("+"+destinatario);//comunichiamo al server il dest.
				}else{
				
				
				st = new StringTokenizer(line,":");
				String mittente = st.nextToken();
				if(!utentiInComunicazione.contains(mittente)){
					utentiInComunicazione.addLast(mittente);
					
				    finestraUtente = new NewClientGUI(this);
				}
				String msg = st.nextToken();
				messaggi.addLast(mittente+" ha scritto:\n" + msg+"\n");
				if (msg.trim().equals("bye"))
					done = true;
		        }//se non è il messaggio speciale
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
		public String getDestinatario(){
			return destinatario;
		}
		public boolean eConnesso(){
			return connesso;
		}
		public void login(){
			String dest = JOptionPane.showInputDialog("destinatario");
			this.destinatario = dest;
			pr.println("+"+destinatario);//diciamo al server con chi vogliamo
			                             //comunicare
			pr.println("*"+destinatario);
			//dopo aver scelto il destinatario dei nostri messaggi
			//lo aggiungiamo alla lista degli utenti in comunicazione
			utentiInComunicazione.addLast(destinatario);
		}
		
			
		

	}



