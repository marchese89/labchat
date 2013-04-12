package client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PannelloContatti extends JFrame{
	
	private LinkedList<String> contatti;
	
	public PannelloContatti(LinkedList<String> contatti){
		this.contatti = contatti;
		Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension d = kit.getScreenSize();
	    setLocation(d.width/4, d.height/4);
	    setSize(d.width/4, d.height/2);
	    
	    JPanel mainPanel = new JPanel();
	    JTextArea jta = new JTextArea();
	    jta.setEditable(false);
	    jta.setPreferredSize(new Dimension((d.width/4)-20, d.height/2));
	    for (String j:contatti)
	    jta.append(j+"\n");
	    
	    JScrollPane jsp = new JScrollPane(jta);
	    mainPanel.add(jsp);
	    this.add(mainPanel);
	    setTitle("Lista Contatti");
	    setVisible(true);
	}
	
	public static void main(String []args){
		LinkedList<String> l = new LinkedList<String>();
		l.add("utente1");l.add("utente2");
		PannelloContatti p = new PannelloContatti(l);
		
		p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}



}
