package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import server.ChatServer;

public class ChatClient implements Runnable{

	public static void main(String[] args) {
		
		Runnable chatS = new ChatServer();
		Runnable chatC = new ChatClient();
		Thread server = new Thread(chatS);
		Thread client = new Thread(chatC);
		server.start();
		client.start();
		
	}
	
	public void run(){
		try {
			Socket client = new Socket("localhost",8189);
			InputStream is = new DataInputStream(client.getInputStream());
			OutputStream os = new DataOutputStream(client.getOutputStream());
			Scanner s = new Scanner(is);
			PrintWriter pr = new PrintWriter(os,true);
			pr.println("ciao server");
			while(true){
				if(s.hasNextLine())
				System.out.println("server ha scritto: "+s.nextLine());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
