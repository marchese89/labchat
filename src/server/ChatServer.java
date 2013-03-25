package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer implements Runnable {

	public void run() {

		try {
			ServerSocket s = new ServerSocket(8189);
			Socket incoming = s.accept();
			try {

				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();
				Scanner in = new Scanner(inStream);
				PrintWriter out = new PrintWriter(outStream, true);
				out.println("Hello!");
				out.println("secondo messaggio");
				boolean done = false;

				while (!done && in.hasNextLine()) {
					String line = in.nextLine();
					out.println("Echo: " + line);
					if (line.trim().equals("bye"))
						done = true;
			
				}// while
			} finally {
				incoming.close();
			}// finally
		}// try esterno
		catch (Exception e) {
			System.out.println("Errore sconosciuto");
		}// catch

	}// run
}// class

