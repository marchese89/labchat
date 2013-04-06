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
import javax.swing.JScrollPane;

public class MainClient extends JFrame{
	
	JMenuItem connect;
	JMenuItem chatWith;
	JMenuItem disconnect;
	ActionListener al;
	WindowListener wl;
	NewClient cc;
	NewClientGUI client;
	//LinkedList<String> utentiConnessi;
	Vector<String> words;
	JList<String> wordList;
	String nomeClient;

	
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
		connect= new JMenuItem("Connetti");
		connect.addActionListener(al);
		disconnect = new JMenuItem("disconnetti");
		disconnect.addActionListener(al);
		chatWith = new JMenuItem("Chatta con");
		chatWith.addActionListener(al);
		chatWith.setEnabled(false);//rendo non disponibile il tasto per chattare
		disconnect.setEnabled(false);
		fileMenu.add(connect);
		fileMenu.add(disconnect);
		fileMenu.add(chatWith);
		menuBar.add(fileMenu);
		words= new Vector<String>();
	
		wordList = new JList<String>(words);
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
				
				
				String nome = JOptionPane.showInputDialog("login con nome");
				nomeClient = new String(nome);
				
				cc = new NewClient(nomeClient);
				cc.start();
				
				AggiornaConnessi ac = new AggiornaConnessi(cc,wordList,words);
				ac.start();
				
				cc.connetti(ip);
				abilitaChat();
				disabilitaConnetti();
				disconnect.setEnabled(true);
				
				JOptionPane.showMessageDialog(null,null,"connesso al server", 1);

			    }
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
			
		}

	
	}//classe interna
	class AscoltatoreFinestra extends WindowAdapter{



		@Override
		public void windowClosing(WindowEvent e) {
			cc.disconnetti();
			//System.out.println("provo a disconnettermi");
			
		}

		
	}


}
