package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import server.ServerGUI;
import server.ServerGUI.Ascoltatore;

public class ClientGUI extends JFrame{

	JButton b;
	JScrollPane js;
	JTextArea ricezione;
	JTextArea invio;
	Client cc;
	
	public ClientGUI(){
    	
	    Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension d = kit.getScreenSize();
	    setLocation(d.width/4, d.height/4);
	    setSize(d.width/2, d.height/2);
	    this.setLayout(new BorderLayout());
	    
		JPanel jp=new JPanel();
		ricezione = new JTextArea(13,30);
		js=new JScrollPane(ricezione);
		jp.add(js);
		invio = new JTextArea(5,30);
		JScrollPane js2 = new JScrollPane(invio);
		JPanel jp2 = new JPanel();
		jp2.add(js2);
		add(jp,BorderLayout.NORTH);
		add(jp2,BorderLayout.CENTER);
		b = new JButton("Invia");
		ActionListener al =  new Ascoltatore();
		b.addActionListener(al);
		add(b,BorderLayout.AFTER_LAST_LINE);
		
		cc = new Client(ricezione);
		Thread t = new Thread(cc);
		t.start();
		
	    }
	    
	public static void main(String[] args) {
		
		
		
		JFrame f= new ClientGUI();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Chat Client");
        f.setVisible(true);
	}
	
	public class Ascoltatore implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == b){
				cc.inviaMessaggio(invio.getText());
				ricezione.append("Hai scritto:\n"+invio.getText()+"\n");
				invio.setText("");
				//jt.append("il listener funziona\n");
				//System.out.println("il listener funziona");
			}
			
		}

	
	}
	
}
