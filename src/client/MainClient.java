package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

public class MainClient extends JFrame{
	
	private JMenuItem connect;
	private JMenuItem chatWith;
	private JMenuItem disconnect;
	private JMenuItem datiDimenticati;
	private JMenuItem iscriviti;
	private JMenuItem aggiungiContatto;
	private JMenuItem rimuoviContatto;
	private JMenuItem listaContatti;
	private JMenuItem stileTesto;
	private ActionListener al;
	private WindowListener wl;
	private Client cc;
	private HashMap<String,JFrame> finestreUtenti;
	//private NewClientGUI client;
	//LinkedList<String> utentiConnessi;
	Vector<String> words;
	JList<String> wordList;
	private String nomeClient;
    private String password;
    private Connection conn;
	private PreparedStatement stat;
    private LinkedList<String> contatti;
	private MainClient mc;
	private Font font;
	private Color colore;
	
	public MainClient(){
		
		this.font =  new Font("Verdana", Font.BOLD, 12);
		this.colore = Color.BLACK;
		
		this.mc = this;
		contatti = new LinkedList<String>();
		finestreUtenti = new HashMap<String,JFrame>();
		
		al =  new Ascoltatore();
		wl = new AscoltatoreFinestra();
		addWindowListener(wl);
		
		Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension d = kit.getScreenSize();
	    setLocation(d.width/4, 0);
	    setSize(d.width/4, d.height/2);
	    this.setLayout(new BorderLayout());
	    
	    JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		JMenu aiuto = new JMenu("Aiuto");
		JMenu opzioni = new JMenu("Opzioni");
		JMenu personalizza = new JMenu("Personalizza");
		datiDimenticati = new JMenuItem("Dati Dimenticati");
		datiDimenticati.addActionListener(al);
		iscriviti = new JMenuItem("Iscrizione");
		iscriviti.addActionListener(al);
		connect= new JMenuItem("Connetti");
		connect.addActionListener(al);
		disconnect = new JMenuItem("Disconnetti");
		disconnect.addActionListener(al);
		chatWith = new JMenuItem("Chatta con");
		chatWith.addActionListener(al);
		aggiungiContatto = new JMenuItem("Aggiungi Contatto");
		aggiungiContatto.addActionListener(al);
		aggiungiContatto.setEnabled(false);
		chatWith.setEnabled(false);//rendo non disponibile il tasto per chattare
		disconnect.setEnabled(false);
		rimuoviContatto = new JMenuItem("Rimuovi Contatto");
		rimuoviContatto.addActionListener(al);
		rimuoviContatto.setEnabled(false);
	    listaContatti = new JMenuItem("Lista Contatti");
	    listaContatti.addActionListener(al);
	    listaContatti.setEnabled(false);
	    stileTesto = new JMenuItem("Stile Testo");
	    stileTesto.addActionListener(al);
		fileMenu.add(connect);
		fileMenu.add(disconnect);
		fileMenu.add(chatWith);
		aiuto.add(datiDimenticati);//se l'utente ha dimenticato i suoi dati
		aiuto.add(iscriviti);//per la registrazione di un nuovo utente
		opzioni.add(listaContatti);
		opzioni.add(aggiungiContatto);
		opzioni.add(rimuoviContatto);
		personalizza.add(stileTesto);
		menuBar.add(fileMenu);
		menuBar.add(aiuto);
		menuBar.add(opzioni);
		menuBar.add(personalizza);
		words= new Vector<String>();
	
		wordList = new JList<String>(words);// Lista utenti connessi
		wordList.setMinimumSize(new Dimension(HEIGHT, WIDTH));
		wordList.setPreferredSize(new Dimension(HEIGHT,WIDTH));
		wordList.setVisibleRowCount(8);
		JScrollPane sp= new JScrollPane(wordList);
		JPanel jp = new JPanel();
		jp.add(sp);
		JLabel utenti = new JLabel("Utenti Connessi");
		JPanel pannello2 = new JPanel();
		pannello2.add(utenti);
		
		add(pannello2,BorderLayout.NORTH);
		add(jp,BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Main Client");
		setVisible(true);
		
		
		
	}//costruttore
	private  void abilitaChat(){
		chatWith.setEnabled(true);
	}
	private void disabilitaConnetti(){
		connect.setEnabled(false);
	}
	
	public void setFont(Font f){
		Set<String> utenti = finestreUtenti.keySet();
		for(String i: utenti){
			finestreUtenti.get(i).setFont(f);
		}
		if(!(cc == null))
		cc.setFont(f);//modifichiamo il font delle finestre di NewClient
		this.font = f;//modifichiamo il font delle future finestre
	}
	public void setForeground(Color c){
		Set<String> utenti = finestreUtenti.keySet();
		for(String i: utenti){
			finestreUtenti.get(i).setForeground(c);
		}
		if(!(cc == null))
		cc.setForeground(c);//modifichiamo il colore delle finestre di NewClient
		this.colore = c;
	}
	public static void main(String[] args) {
		JFrame f = new MainClient();

	}

	
	public class Ascoltatore implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
		
				if(e.getSource() == connect){
				String ip = JOptionPane.showInputDialog("inserire indirizzo ip");
				
				//login con user name e password
				
				String nome = JOptionPane.showInputDialog("login con nome");
				nomeClient = new String(nome);
				if(!nomeClient.equals("")){
				JPasswordField pf = new JPasswordField();
				int okCxl = JOptionPane.showConfirmDialog
						(null, pf, "Password", JOptionPane.OK_CANCEL_OPTION, 
								JOptionPane.PLAIN_MESSAGE);
				if (okCxl == JOptionPane.OK_OPTION) {
					  password = new String(pf.getPassword());
					}
				if(!password.equals("")){
				cc = new Client(nomeClient,password,false);
				cc.start();
				
				AggiornaConnessi ac = new AggiornaConnessi(cc,wordList,words);
				ac.start();
				
				boolean ris = cc.connetti(ip);
				if(ris){//se tutto ha funzionato
				abilitaChat();
				disabilitaConnetti();
				disconnect.setEnabled(true);
				aggiungiContatto.setEnabled(true);
				listaContatti.setEnabled(true);
				iscriviti.setEnabled(false);
				rimuoviContatto.setEnabled(true);
				
				setFont(font);
				setForeground(colore);
				JOptionPane.showMessageDialog(null,null,"connesso al server",1);
				}else{
					JOptionPane.showMessageDialog
					(null,null, "Username e/o password Errati",JOptionPane.ERROR_MESSAGE);
				}
				
				}//se la password non è la stringa vuota
				else{
					JOptionPane.showMessageDialog
					(null,null, "password nulla",JOptionPane.ERROR_MESSAGE);
				}
				}else{
					JOptionPane.showMessageDialog
					(null,null, "nome utente nullo",JOptionPane.ERROR_MESSAGE);
				}
			    }//if è stato premuto connect
				
				if(e.getSource() == chatWith){
					String dest = cc.login();
					ClientGUI f = new ClientGUI(cc,dest);
					f.setFont(font);
					f.setForeground(colore);
					finestreUtenti.put(dest, f);
					
				}
				if(e.getSource() == disconnect){
					cc.disconnetti();
					chatWith.setEnabled(false);
					connect.setEnabled(true);
					iscriviti.setEnabled(true);
					//cancello l'elenco degli utenti connessi
					words = new Vector<String>();
					wordList.setListData(words);
					wordList.repaint();
					
				}
				if(e.getSource() == iscriviti){
					String ip = JOptionPane.showInputDialog("inserire indirizzo ip");
					String user_name = JOptionPane.showInputDialog("username");
					String pass = "";
					JPasswordField pf2 = new JPasswordField();
					int okCxl = JOptionPane.showConfirmDialog
							(null, pf2, "Password", JOptionPane.OK_CANCEL_OPTION, 
									JOptionPane.PLAIN_MESSAGE);
					if (okCxl == JOptionPane.OK_OPTION) {
						  pass = new String(pf2.getPassword());
						}
					String email = JOptionPane.showInputDialog("Email");
					cc = new Client(user_name,pass,email,true);
					cc.start();
					boolean ris = cc.connetti(ip);
					if(ris){
						JOptionPane.showMessageDialog
						(null, "Iscrizione avvenuta con succeso");
					}
				}
				
				if(e.getSource() == aggiungiContatto){
					if(cc.eConnesso()){
					String nomeContatto = JOptionPane.showInputDialog("Nome Contatto");
					contatti = cc.getListaContatti();
					if(!contatti.contains(nomeContatto)  && 
							(!nomeContatto.equals(nomeClient))){
					cc.inviaMessaggio("A:"+nomeContatto);//richiesta aggiunta contatto
					JOptionPane.showMessageDialog(null, "Richiesta Inviata");
					}else{
						JOptionPane.showMessageDialog
						(null, null, "Contatto già presente", JOptionPane.ERROR_MESSAGE);
					}
					}//se è connesso
				}
				if(e.getSource() == listaContatti){
			
					contatti = cc.getListaContatti();
					JFrame pC = new PannelloContatti(contatti);
				}
				if(e.getSource() == rimuoviContatto){
					String toRemove = JOptionPane.showInputDialog
							("Nome Contatto da Rimuovere");
					cc.inviaMessaggio("R"+toRemove);//richiesta cancellazione contatto
					JOptionPane.showMessageDialog(null, "Richiesta Inviata");
				}
				if(e.getSource() == stileTesto){
					PannelloFont pf = new PannelloFont(mc);
				}
			
		}//actionPerformed

	
	}//classe interna
	class AscoltatoreFinestra extends WindowAdapter{



		@Override
		public void windowClosing(WindowEvent e) {
			if(cc != null)
			if(cc.eConnesso())
			cc.disconnetti();
			//System.out.println("provo a disconnettermi");
			
		}

		
	}//classe interna


}
