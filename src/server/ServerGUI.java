package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerGUI extends JFrame{
	//definizione componenti di rete
	Server s;
	//componenti i/o
	//definizione elementi grafici
	JButton b;
	JScrollPane js;
	JTextArea ricezione;
	JTextArea invio;
	JMenuItem connect;
	//costruttore
    public ServerGUI(){
    	
    ActionListener al =  new Ascoltatore();
    
    
	Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension d = kit.getScreenSize();
    setLocation(d.width/4, d.height/4);
    setSize(d.width/2, d.height/2);
    this.setLayout(new BorderLayout());
    
	JPanel jp=new JPanel();
	ricezione = new JTextArea(13,30);
	js=new JScrollPane(ricezione);
	jp.add(js);
	invio = new JTextArea(4,30);
	JScrollPane js2 = new JScrollPane(invio);
	JPanel jp2 = new JPanel();
	jp2.add(js2);
	//JPanel unione = new JPanel();
	//unione.setLayout(new BorderLayout());
	add(jp,BorderLayout.NORTH);
	add(jp2,BorderLayout.CENTER);
	//add(unione,BorderLayout.CENTER);//aggiungiamo il pannello composto
	
	
	
	
	b = new JButton("Invia");
	
	b.addActionListener(al);
	add(b,BorderLayout.SOUTH);
	
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Chat Server");
    setVisible(true);
	
	s = new Server(ricezione);	
	Thread t = new Thread(s);
	t.start();
	
    }
    
    
   
	public static void main(String[] args) {
		
		
		JFrame f= new ServerGUI();
		
        
        
        
	}
	

public class Ascoltatore implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == b){
			s.inviaMessaggio("server:"+invio.getText(),1);//riga modificata
			ricezione.append("Hai scritto:\n"+invio.getText()+"\n");
			invio.setText("");
			//jt.append("il listener funziona\n");
			//System.out.println("il listener funziona");
		}
		
		
	}

}
}
