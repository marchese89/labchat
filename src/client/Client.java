package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JTextArea;

public class Client implements Runnable{

	private Scanner s;
	private PrintWriter pr;
	private JTextArea jt;
	boolean connesso = false;
	private StringTokenizer st;
	private String destinatario;
	private Socket client;
	public void connetti(String ip,String destinatario){
		try{
			this.destinatario = destinatario;
			//instauriamo una connessione con il server ed estraiamo
			//gli strumenti per inviare e ricevere testo
    		client = new Socket(ip, 8189);
    		InputStream is = new DataInputStream(client.getInputStream());
    		OutputStream os = new DataOutputStream(client.getOutputStream());
    		s = new Scanner(is);
    		pr = new PrintWriter(os, true);
    		pr.println("+"+destinatario);//diciamo al server con chi vogliamo comunicare
    		connesso = true;
    		}catch(IOException e){
    			e.printStackTrace();
    		}
	}
    public Client(JTextArea j){
    	jt = j;
    	
    }
	@Override
	public void run() {
		//riceviamo di continuo i messaggi dal server
		while(true){
		if(connesso){
		boolean done = false;
		while (!done && s.hasNextLine()) {
			String line = s.nextLine();
			//System.out.println("messagio ricevuto dal server: "+line);
			st = new StringTokenizer(line,":");
			String mittente = st.nextToken();
			String msg = st.nextToken();
			//TODO
			jt.append(mittente+" ha scritto:\n" + msg+"\n");
			if (msg.trim().equals("bye"))
				done = true;

		}// while
		}
		}//while esterno
	}//run
	
	//invia i messaggi al server
	public void inviaMessaggio(String m){
		pr.println(m);
	}

    
	public String getDestinatario(){
		return destinatario;
	}
	public boolean eConnesso(){
		return connesso;
	}

}
