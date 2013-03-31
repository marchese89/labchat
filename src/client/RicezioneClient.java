package client;

import javax.swing.JTextArea;

public class RicezioneClient extends Thread{
    private JTextArea jt;
    private NewClient cc;
	public RicezioneClient(NewClient c,JTextArea jt){
		this.jt = jt;
		this.cc = c;
	}
	public void run(){
		while(true){
    		if(cc.eConnesso())
    		if(cc.ciSonoMsg())
    			jt.append(cc.riceviMsg());
    		
    		
    	}
	}

}
