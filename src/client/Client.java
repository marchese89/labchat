package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JTextArea;

public class Client implements Runnable{

	
	Scanner s;
	PrintWriter pr;
	JTextArea jt;
	
	public static void main(String[] args) {
		
	}
    public Client(JTextArea j){
    	jt = j;
    	try{
	
    		Socket client = new Socket("localhost", 8189);
    		InputStream is = new DataInputStream(client.getInputStream());
    		OutputStream os = new DataOutputStream(client.getOutputStream());
    		s = new Scanner(is);
    		pr = new PrintWriter(os, true);
    		
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    }
	@Override
	public void run() {
		boolean done = false;
		while (!done && s.hasNextLine()) {
			String line = s.nextLine();
			jt.append("Server ha scritto:\n" + line+"\n");
			if (line.trim().equals("bye"))
				done = true;

		}// while
	}
	public void inviaMessaggio(String m){
		pr.println(m);
	}
	
	

}
