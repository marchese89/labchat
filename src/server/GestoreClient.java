package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * 
 * @author giovanni
 * 
 */
public class GestoreClient extends Thread {

	private Socket incoming;
	private Scanner in;
	private PrintWriter out;
	private LinkedList<String> msg;
	private String nomeClient;
    private boolean nomeClientPronto;
	public GestoreClient(Socket i) {
		incoming = i;
		nomeClientPronto = false;
		msg = new LinkedList<String>();
	}

	@Override
	public void run() {
		// riceviamo di continuo i messaggi dal client
		// il primo messaggio ci dice chi � il destinatario dei messaggi inviati
		// dal client
		try {
			try {
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();

				in = new Scanner(inStream);
				out = new PrintWriter(outStream, true);

				while (true) {
					if (in.hasNextLine()) {
						String s = in.nextLine();
						if (s.charAt(0) == '{') {//stiamo per leggere il nome del client
							StringTokenizer st = new StringTokenizer(s, "{");
							nomeClient = st.nextToken();
							nomeClientPronto = true;
						} else {
							msg.addLast(s);
						}
					}
				}
			} finally {
				incoming.close();
			}
		} catch (IOException e) {
			e.printStackTrace();

		}

	}// run

	public boolean ciSonoMsg() {
		return msg.size() > 0;
	}

	// per ricevere i messaggi dal client
	public String riceviMsg() {
		return msg.removeFirst();

	}

	public String anteprimaMsg() {
		return msg.getFirst();
	}

	// per inviare messaggi al client
	public void inviaMsg(String m) {
		out.println(m);
	}
	public boolean nomeClientPronto(){
		return nomeClientPronto;
	}
	public String getNomeClient(){
		return nomeClient;
	}

}
