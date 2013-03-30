package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import client.ClientGUI.Ascoltatore;

public class MainClient extends JFrame{
	
	JMenuItem connect;
	ActionListener al;
	
	public MainClient(){
		
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
		fileMenu.add(connect);
		menuBar.add(fileMenu);
		
		String[] words = { "angelo","giovanni","ciao3"};
		JList wordList =new JList(words);
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new MainClient();

	}
	
	public class Ascoltatore implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
		
				if(e.getSource() == connect){
				String ip = JOptionPane.showInputDialog("inserire indirizzo ip");
				//cc.connetti(ip);
				JOptionPane.showMessageDialog(null,null,"connesso al server", 1);
			}
			
		}

	
	}

}
