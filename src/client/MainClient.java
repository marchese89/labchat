package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
	private ActionListener al;
	private WindowListener wl;
	private NewClient cc;
	private NewClientGUI client;
	//LinkedList<String> utentiConnessi;
	Vector<String> words;
	JList<String> wordList;
	private String nomeClient;
    private String password;
	
	public MainClient(){
		
		
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
		chatWith.setEnabled(false);//rendo non disponibile il tasto per chattare
		disconnect.setEnabled(false);
		fileMenu.add(connect);
		fileMenu.add(disconnect);
		fileMenu.add(chatWith);
		aiuto.add(datiDimenticati);//se l'utente ha dimenticato i suoi dati
		aiuto.add(iscriviti);//per la registrazione di un nuovo utente
		menuBar.add(fileMenu);
		menuBar.add(aiuto);
		words= new Vector<String>();
	
		wordList = new JList<String>(words);// Lista utenti connessi
		wordList.setMinimumSize(new Dimension(HEIGHT, WIDTH));
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
		
		
		
	}
	private  void abilitaChat(){
		chatWith.setEnabled(true);
	}
	private void disabilitaConnetti(){
		connect.setEnabled(false);
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
				cc = new NewClient(nomeClient,password,false);
				cc.start();
				
				AggiornaConnessi ac = new AggiornaConnessi(cc,wordList,words);
				ac.start();
				
				boolean ris = cc.connetti(ip);
				if(ris){//se tutto ha funzionato
				abilitaChat();
				disabilitaConnetti();
				disconnect.setEnabled(true);
				
				JOptionPane.showMessageDialog(null,null,"connesso al server", 1);
				}else{
					JOptionPane.showMessageDialog
					(null,null, "Username e/o password Errati",JOptionPane.ERROR_MESSAGE);
				}
				
				}//se la password non � la stringa vuota
				else{
					JOptionPane.showMessageDialog
					(null,null, "password nulla",JOptionPane.ERROR_MESSAGE);
				}
				}else{
					JOptionPane.showMessageDialog
					(null,null, "nome utente nullo",JOptionPane.ERROR_MESSAGE);
				}
			    }//if � stato premuto connect
				
				if(e.getSource() == chatWith){
					String dest = cc.login();
					client = new NewClientGUI(cc,dest);
					
				}
				if(e.getSource() == disconnect){
					cc.disconnetti();
					chatWith.setEnabled(false);
					connect.setEnabled(true);
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
					cc = new NewClient(user_name,pass,email,true);
					cc.start();
					boolean ris = cc.connetti(ip);
					if(ris){
						JOptionPane.showMessageDialog
						(null, "Iscrizione avvenuta con succeso");
					}
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

		
	}


}
