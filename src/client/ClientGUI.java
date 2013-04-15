package client;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;


public class ClientGUI extends JFrame{

	private JButton b;
	private JScrollPane js;
	private JTextArea ricezione;
	private JTextArea invio;
	private Client cc;
	private ActionListener al;
	private String destinatario;
    private RicezioneClient rc;
    
	public ClientGUI(Client cc,String destinatario) {
        
		this.destinatario = destinatario;
	    this.cc = cc;
	
		al = new Ascoltatore();
        //dimensione, posizione e layout del frame
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		setLocation(d.width / 4, d.height / 4);
		setSize(d.width / 2, d.height / 2);
		this.setLayout(new BorderLayout());
        //pannelli vari
		JPanel jp = new JPanel();
		ricezione = new JTextArea(15, 50);
		ricezione.setEditable(false);
		ricezione.setWrapStyleWord(true);
		ricezione.setLineWrap(true);
		ricezione.setFont(new Font("Arial Black", Font.BOLD, 12));//ingrandisce il testo
		js = new JScrollPane(ricezione);
		jp.add(js);
		invio = new JTextArea(4, 45);
		invio.setWrapStyleWord(true);//va a capo di parola in parola 
		invio.setLineWrap(true);//va a capo automaticamente
		invio.setFont(new Font("Arial Black",Font.BOLD,12));
		//per far spostare il cursore al punto di partenza dopo l'invio di un messaggio
		Keymap km = invio.getKeymap ();
		km.addActionForKeyStroke (KeyStroke.getKeyStroke (KeyEvent.VK_ENTER, 0), new
		SendAction ());
		JScrollPane js2 = new JScrollPane(invio);
		JPanel jp2 = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.RIGHT);
		jp2.setLayout(fl);
		b = new JButton("Invia");
        b.addActionListener(al);
		jp2.add(js2);
		jp2.add(b);
		add(jp, BorderLayout.EAST);
		add(jp2, BorderLayout.SOUTH);
		
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setTitle("Conversazione con "+destinatario);
	    setVisible(true);
	    rc = new RicezioneClient(cc, ricezione);
	    rc.start();
       
	}//costrutture
	
	public void setFont(Font f){
		ricezione.setFont(f);
		invio.setFont(f);
		//repaint();
	}
    public void setForeground(Color c){
    	ricezione.setForeground(c);
    	invio.setForeground(c);
    	//repaint();
    }
   

	class Ascoltatore implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == b) {
				  if(!invio.getText().equals("")){
		          cc.inviaMessaggio(destinatario + ":" + invio.getText());
			      ricezione.append("Hai scritto:\n" + invio.getText() + "\n");
				  invio.setText("");
				  }
			}

		

		}

	}//classe
	private class SendAction extends AbstractAction{
	    public void actionPerformed (ActionEvent e){
	    	if(!invio.getText().equals("")){
		          cc.inviaMessaggio(destinatario + ":" + invio.getText());
			      ricezione.append("Hai scritto:\n" + invio.getText() + "\n");
				  invio.setText("");
				  }
	    }
	}


}
