package client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("all")
public class PannelloContatti extends JFrame{
	
	private LinkedList<String> contatti,utentiBloccati;
	private StringTokenizer st;
	
	public PannelloContatti(LinkedList<String> contatti,LinkedList<String> utentiBloccati){
		this.contatti = contatti;
		this.utentiBloccati = utentiBloccati;
		Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension d = kit.getScreenSize();
	    setLocation(d.width/4, d.height/4);
	    setSize(d.width/4, d.height/2);
	    
	    JPanel mainPanel = new JPanel();
	    JTextArea jta = new JTextArea();
	    jta.setEditable(false);
	    jta.setPreferredSize(new Dimension((d.width/4)-20, d.height/2));
	    for (String j:contatti){
	    	st = new StringTokenizer(j," ");
	    	String contatto = st.nextToken();
	        jta.append(contatto+ " ");
	        if(this.utentiBloccati.contains(contatto)){
	        	jta.append("bloccato\n");
	        }else
	        	jta.append("\n");
	        
	    }
	    JScrollPane jsp = new JScrollPane(jta);
	    mainPanel.add(jsp);
	    this.add(mainPanel);
	    setTitle("Lista Contatti");
	    setVisible(true);
	}
	

}
