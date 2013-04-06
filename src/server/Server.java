package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

public class Server implements Runnable {

	private ServerSocket s;
	private JTextArea jt;
	private HashMap<String, GestoreClient> clients;
	private Lock l; 
	
	
	public Server(JTextArea j) {
		jt = j;
		clients = new HashMap<String, GestoreClient>();
		try {
			s = new ServerSocket(8189);
		} catch (IOException e) {
			e.printStackTrace();
		}
		l = new ReentrantLock();
	}

	public void run() {
		
		Thread ricez = new RicezioneServer(clients,jt,l);
		ricez.start();
		Thread notifica = new NotificaClient(clients, l);
		notifica.start();
		while (true) {
			Socket incoming;
			try {
				incoming = s.accept();
				GestoreClient t = new GestoreClient(incoming);
				t.start();
				l.lock();
				while(!t.nomeClientPronto()){
					//System.out.println("nome client ancora non pronto");
					}
				clients.put(t.getNomeClient(), t);
				
				l.unlock();
				

			} catch (IOException e) {
				e.printStackTrace();
			}

		}// while

	}
    
	public void inviaMessaggio(String m,int i) {
		clients.get(i).inviaMsg(m);
	}

}
