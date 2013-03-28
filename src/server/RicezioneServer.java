package server;

import java.util.HashMap;

import javax.swing.JTextArea;

public class RicezioneServer extends Thread{
     private HashMap<Integer, GestoreClient> clients;
     private JTextArea jt;
     public RicezioneServer(HashMap<Integer, GestoreClient> clients,JTextArea jt){
			this.clients = clients;
			this.jt = jt;
     }
     
     public void run(){
    	 while(true){
    	 for (int j = 1; j <= clients.size(); j++) {
             if(clients.get(j).ciSonoMsg()){
             	jt.append("Il Client "+j+" ha scritto:\n"+
                              clients.get(j).riceviMsg()+"\n");
             }
			}
    	 }
     }
}
