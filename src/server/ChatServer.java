package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer implements Runnable {

	public void run() {

		try {
			int i = 1;
			ServerSocket s = new ServerSocket(8189);

			while (true) {
				Socket incoming = s.accept();
				System.out.println("Spawning " + i);
				Runnable r = new ThreadedEchoHandler(incoming, i);
				Thread t = new Thread(r);
				t.start();
				i++;
			}// while

		} catch (IOException e) {

		}

	}// run
}// class

