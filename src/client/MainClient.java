package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	ActionListener al;
	JMenuItem chatWith;
	NewClient cc;
	NewClientGUI client;
	//LinkedList<String> utentiConnessi;
	Vector<String> words;
	JList<String> wordList;

	
	public MainClient(){
		
		cc = new NewClient();
		cc.start();
		
		
		
		al =  new Ascoltatore();
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
		chatWith = new JMenuItem("Chatta con");
		chatWith.addActionListener(al);
		chatWith.setEnabled(false);//rendo non disponibile il tasto per chattare
		fileMenu.add(connect);
		fileMenu.add(chatWith);
		menuBar.add(fileMenu);
		words= new Vector<String>();
		words.add("         ");
		wordList = new JList<String>(words);
		
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
		
		AggiornaConnessi ac = new AggiornaConnessi(cc,wordList,words);
		ac.start();
		
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
				cc.connetti(ip);
				abilitaChat();
				disabilitaConnetti();
				JOptionPane.showMessageDialog(null,null,"connesso al server", 1);

			    }
				if(e.getSource() == chatWith){
					String dest = cc.login();
					client = new NewClientGUI(cc,dest);
					
				}
			
		}

	
	}


}
