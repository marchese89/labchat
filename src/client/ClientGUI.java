package client;


import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.Keymap;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;


@SuppressWarnings("serial")
public class ClientGUI extends JFrame{

	private JButton b;
	private JScrollPane js;
	private JTextArea ricezione;
	private JTextArea invio;
	private Client cc;
	private ActionListener al;
	private RicezioneClient rc;
    private Focus f;
    private StringTokenizer st;
    private JLabel statusLabel;
    private boolean fantasma;
    private int id ;
    private Set<String> dest;
    private JButton addUser;
    private String nomeClient;
    private AscoltatoreFinestra alF; 
    public void aggiorna () {
    	setTitle("Conversazione con "+dest.toString());
    }
    
	public ClientGUI(Client cc,Set<String> dest, boolean ghost, int id, String nomeClient) {
		alF = new AscoltatoreFinestra();
		addWindowListener(alF);
		this.nomeClient = nomeClient;
        this.id = id;
        this.dest = dest;
		fantasma = ghost;//diciamo se la finestra è solo fittizia...
	    this.cc = cc;
	    f = new Focus();
		al = new Ascoltatore();
		this.addWindowListener(f);
        //dimensione, posizione e layout del frame
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		setLocation(d.width / 4, d.height / 4);
		setSize(d.width / 2, (d.height / 2)+20);
		this.setLayout(new BorderLayout());
        //pannelli vari
		JPanel jp = new JPanel();
		ricezione = new JTextArea(14, 50);
		ricezione.setEditable(false);
		ricezione.setWrapStyleWord(true);
		ricezione.setLineWrap(true);
		ricezione.setFont(new Font("Arial Black", Font.BOLD, 12));//ingrandisce il testo
		js = new JScrollPane(ricezione);
		jp.add(js);
		invio = new JTextArea(4, 45);
		invio.setWrapStyleWord(true);//va a capo di parola in parola 
		invio.setLineWrap(true);//va a capo automaticamente
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
        addUser = new JButton("Aggiungi utente");
        addUser.addActionListener(al);
		jp2.add(js2);
		jp2.add(b);
		jp2.add(addUser);
		//barra di stato
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder((Border) new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setPreferredSize(new Dimension(getWidth(), 20));
		statusPanel.setLayout((LayoutManager) new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		
		statusLabel = new JLabel("status");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
	
		add(jp, BorderLayout.NORTH);
		add(jp2, BorderLayout.CENTER);
		add(statusPanel,BorderLayout.SOUTH);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setTitle("Conversazione con "+dest.toString());
	    rc = new RicezioneClient(cc, ricezione,id,statusLabel,this);
	    rc.start();
       
	}//costrutture
	
	public void setFont(Font f){
		ricezione.setFont(f);
		invio.setFont(f);
		repaint();
	}
    public void setForeground(Color c){
    	ricezione.setForeground(c);
    	invio.setForeground(c);
    	repaint();
    }
    
    public void append (String s) {
    	ricezione.append(s);
    }
    public void playSound() {
    	try {
    		InputStream in = new FileInputStream("sounds/sound.wav");
    		AudioStream au = new AudioStream(in);
    		AudioPlayer.player.start(au);
    	} catch (Exception e) {e.printStackTrace();}
    	}

	class Ascoltatore implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!fantasma){
			if (e.getSource() == b) {
				  if(!invio.getText().equals("")){
					  cc.sendMessage(id, invio.getText());
		        //  cc.inviaMessaggio("M"+destinatario + ":" + invio.getText());
			      ricezione.append("Hai scritto:\n" + invio.getText() + "\n");
				  invio.setText("");
				  statusLabel.setText("");
				  }
			}
			else if (e.getSource() == addUser) {
				LinkedList<String> ll = cc.utentiConnessi();
				@SuppressWarnings("unused")
				SelectUserPanel p = new SelectUserPanel(ll,nomeClient,cc, id, dest);
			}

			}else{
				if(!invio.getText().equals("")){
				   ricezione.append("Hai scritto:\n" + invio.getText() + "\n");
				   invio.setText("");
				   statusLabel.setText("");
				}
			}

		}//actionPerformed

	}//classe
	
	private class Focus extends WindowAdapter{


		@Override
		public void windowActivated(WindowEvent arg0) {
			if (dest.size()==1){
			GregorianCalendar gc = new GregorianCalendar();
            Date d =gc.getTime();
            st = new StringTokenizer(d.toString());
            st.nextToken();st.nextToken();st.nextToken();
            String ora = st.nextToken();
            st= new StringTokenizer(ora,":");
            String oraFormatted = st.nextToken()+":"+st.nextToken();
			cc.inviaMessaggio("<"+cc.getNomeClient()+"<"+oraFormatted+"<"+id);
			System.out.println(cc.getNomeClient() + 
					" visualizza il messaggio e informa il server (ClientGUI)");
			}	
			
		}


	}//classe
	private class SendAction extends AbstractAction{
	    public void actionPerformed (ActionEvent e){
	    	if(!fantasma){
	    	if(!invio.getText().equals("")){
	    		cc.sendMessage(id, invio.getText());
		         // cc.inviaMessaggio("M"+destinatario + ":" + invio.getText());
			      ricezione.append("Hai scritto:\n" + invio.getText() + "\n");
				  invio.setText("");
				  statusLabel.setText("");
	    	}
		    }else{
					  if(!invio.getText().equals("")){
					  ricezione.append("Hai scritto:\n" + invio.getText() + "\n");
					  invio.setText("");
					  statusLabel.setText("");
					  }
				  }
	    }//metodo
	}//classe
	


	class AscoltatoreFinestra extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			if (cc != null)
				if (cc.eConnesso()){
					cc.inviaMessaggio("&" + id + "&" + nomeClient);
				}
			

		}

	}// classe interna
}
