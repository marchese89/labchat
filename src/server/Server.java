package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JTextArea;

public class Server implements Runnable{

	private ServerSocket s;
	private Socket incoming;
	private Scanner in;
	private PrintWriter out;
	private JTextArea jt;
	
	public Server(JTextArea j){
		jt = j;
		try {
		s = new ServerSocket(8189);
		incoming = s.accept();
		
	    InputStream inStream = incoming.getInputStream();
		OutputStream outStream = incoming.getOutputStream();
		in = new Scanner(inStream);
		out = new PrintWriter(outStream, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		boolean done = false;
		while (!done && in.hasNextLine()) {
			String line = in.nextLine();
			jt.append("Un client ha scritto:\n" + line+"\n");
			if (line.trim().equals("bye"))
				done = true;

		}// while

	}
	public void inviaMessaggio(String m){
		out.println(m);
	}

}
