package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
/**
 * 
 * @author giovanni
 * 
 */
public class GestoreClient extends Thread{

	private Socket incoming;
	private int counter;
	private Scanner in;
	private PrintWriter out;
	private LinkedList<String> msg;
	
	public GestoreClient(Socket i,int c){
		incoming = i;
		counter = c;
		msg = new LinkedList<String>();
	}

	@Override
	public void run() {
		
		try{
			try{
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();
				
				in = new Scanner(inStream);
				out = new PrintWriter(outStream,true);
				
				boolean letta= false;
				while(!letta && in.hasNextLine()){
					msg.addLast(in.nextLine());
				}
			}finally{
				incoming.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}//run
	public boolean ciSonoMsg(){
		return msg.size() > 0;
	}
	//per ricevere i messaggi dal client
	public String riceviMsg(){
		return msg.removeFirst();
	}
	//per inviare messaggi al client
	public void inviaMsg(String m){
		out.println(m);
	}
	
}
