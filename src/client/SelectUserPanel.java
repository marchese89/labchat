package client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.CloseAction;


public class SelectUserPanel extends JFrame {
	private Vector<String> words;
	private JList<String> wordList; 
	private ActionJList l ;
	protected static Client c;
	protected static Integer id; 
	private Set<String> dest;
	public SelectUserPanel(LinkedList<String> ll, String nomeClient, Client c, int id, Set<String> dest) {
		this.dest = dest;
		this.id = id;
		this.c = c;
		words = new Vector<String>();
		for (String i : ll)
			if (! i.equals(nomeClient) && !dest.contains(i))
			words.add(i);
		wordList = new JList<String>(words);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		setLocation(d.width / 4, d.height / 4);
		setSize(d.width / 4, d.height / 2);
		JPanel mainPanel = new JPanel();
		l = new ActionJList(wordList);
		wordList.addMouseListener(l);
		wordList.setMinimumSize(new Dimension(HEIGHT, WIDTH));
		wordList.setPreferredSize(new Dimension(250,190));
		wordList.setVisibleRowCount(8);
		JScrollPane sp = new JScrollPane(wordList);
		JPanel jp = new JPanel();
		jp.add(sp);
		JLabel utenti = new JLabel("Utenti Connessi");
		JPanel pannello2 = new JPanel();
		pannello2.add(utenti);

		add(pannello2, BorderLayout.NORTH);
		add(jp, BorderLayout.CENTER);
		setTitle("Aggiungi utente");
		setVisible(true);
		
	}
}
class ActionJList extends MouseAdapter {
	protected JList list;
	private String user;
	
	public ActionJList(JList l) {
		list = l;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int index = list.locationToIndex(e.getPoint());
			if (index >= 0){
			ListModel dlm = list.getModel();
			Object item = dlm.getElementAt(index);
			
			list.ensureIndexIsVisible(index);
			user = (String) item;
			SelectUserPanel.c.addUser(SelectUserPanel.id, user);
			}
		}
	}
}

