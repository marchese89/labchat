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
    private String mittente;
    private JLabel status;
    private StringTokenizer st;
	public RicezioneClient(Client c,JTextArea jt,String mittente,JLabel status){
		this.jt = jt;
		this.cc = c;
		this.mittente = mittente;
		this.status = status;
	}
	public void run(){
		while(true){
    		if(cc.eConnesso())
    		if(cc.ciSonoMsg(mittente)){
    			String m = cc.riceviMsg(mittente);
    			if(m.charAt(0) == '<'){
    				st = new StringTokenizer(m,"<");
    				st.nextToken();//rimuoviamo il mittente
    				status.setText("Visualizzato alle "+st.nextToken());
    				status.repaint();
    			}else{
    			jt.append(m);
    			status.setText("");
    			}
    		}
    	}
	}

}
