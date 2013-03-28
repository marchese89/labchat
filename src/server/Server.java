package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JTextArea;

public class Server implements Runnable {

	private ServerSocket s;
	private JTextArea jt;
	private HashMap<Integer, GestoreClient> clients;

	public Server(JTextArea j) {
		jt = j;
		clients = new HashMap<Integer, GestoreClient>();
		try {
			s = new ServerSocket(8189);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		int i = 1;
		Thread ricez = new RicezioneServer(clients,jt);
		ricez.start();
		while (true) {
			Socket incoming;
			try {
				incoming = s.accept();
				GestoreClient t = new GestoreClient(incoming, i);
				clients.put(i, t);
				t.start();
				i++;
				
				

			} catch (IOException e) {
				e.printStackTrace();
			}

		}// while

	}

	public void inviaMessaggio(String m,int i) {
		clients.get(i).inviaMsg(m);
	}

}
