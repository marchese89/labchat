package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadedEchoHandler implements Runnable {

	private Socket incoming;
	private int counter;

	public ThreadedEchoHandler(Socket i, int c) {
		incoming = i;
		counter = c;

	}

	public void run() {
		try {
			try {
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();
				Scanner in = new Scanner(inStream);
				PrintWriter out = new PrintWriter(outStream, true);
				out.println("Hello!");
				boolean done = false;
				while (!done && in.hasNextLine()) {
					String line = in.nextLine();
					System.out.println("Un client ha scritto: " + line);
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

	}
}
