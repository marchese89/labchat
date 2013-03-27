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
	boolean connesso = false;
	
	public void connetti(String ip){
		try{
			
    		Socket client = new Socket(ip, 8189);
    		InputStream is = new DataInputStream(client.getInputStream());
    		OutputStream os = new DataOutputStream(client.getOutputStream());
    		s = new Scanner(is);
    		pr = new PrintWriter(os, true);
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
		while(true){
		if(connesso){
		boolean done = false;
		while (!done && s.hasNextLine()) {
			String line = s.nextLine();
			jt.append("Server ha scritto:\n" + line+"\n");
			if (line.trim().equals("bye"))
				done = true;

		}// while
		}
		}//while esterno
	}//run
	public void inviaMessaggio(String m){
		pr.println(m);
	}
	
	

}
