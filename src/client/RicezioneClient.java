package client;
import java.util.StringTokenizer;

import javax.swing.JLabel;
/**
 * riceve i messaggi da un singolo client e li stampa 
 * sulla JTextArea
 */
import javax.swing.JTextArea;

public class RicezioneClient extends Thread{
    private TextPaneH jt;
    private Client cc;
    private JLabel status;
    private StringTokenizer st;
    private int id;
    private ClientGUI client;
    private boolean [] flag;
	public RicezioneClient(Client c,TextPaneH jt,int id,JLabel status, ClientGUI client, boolean[]  flag){
		this.flag = flag;
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
    				jt.appendWho("Aggiunto utente: " + m.substring(2,m.length()) );
    				jt.setCaretPosition(jt.getDocument().getLength());
    			}
    			else{
    			StringTokenizer st = new StringTokenizer(m,":");
    			String name = st.nextToken();
    			String msg = m.substring(name.length()-1);
    			if (msg!=null && name != null){
    				st = new StringTokenizer(name," ");
    				name = new String (st.nextToken());
    			jt.appendWhoF(name);
    			jt.appendThat(msg.substring(4));
    			jt.setCaretPosition(jt.getDocument().getLength());
    			flag[0] = true;
    			client.playSound();
    			status.setText("");
    			}
    			}
    		}
    	}
	}

}
