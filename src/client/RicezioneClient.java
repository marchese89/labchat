package client;
import java.util.StringTokenizer;

import javax.swing.JLabel;
/**
 * riceve i messaggi da un singolo client e li stampa 
 * sulla JTextArea
 */
import javax.swing.JTextArea;

public class RicezioneClient extends Thread{
    private JTextArea jt;
    private Client cc;
    private JLabel status;
    private StringTokenizer st;
    private int id;
    private ClientGUI client;
	public RicezioneClient(Client c,JTextArea jt,int id,JLabel status, ClientGUI client){
		this.client = client;
		this.jt = jt;
		this.cc = c;
		this.id = id;
		this.status = status;
	}
	public void run(){
		while(true){
    		if(cc.eConnesso())
    		if(cc.ciSonoMsg(id)){
    			String m = cc.riceviMsg(id);
    			if(m.charAt(0) == '<'){
    				st = new StringTokenizer(m,"<");
    				st.nextToken();//rimuoviamo il mittente
    				status.setText("Visualizzato alle "+st.nextToken());
    				status.repaint();
    				System.out.println("Messaggio visualizzato correttamente");
    			}
    			else if (m.length()>2 && m.substring(0,2).equals("##")){
    				client.aggiorna();
    				jt.append("Aggiunto utente: " + m.substring(2,m.length()) +"\n");
    			}
    			else{
    			jt.append(m+"\n");
    			client.playSound();
    			status.setText("");
    			}
    		}
    	}
	}

}
