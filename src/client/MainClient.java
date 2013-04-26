package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import javax.swing.ListModel;

import Utility.Security;


@SuppressWarnings("all")
public class MainClient extends JFrame {

	private JMenuItem connect, chatWith, disconnect, datiDimenticati,
			iscriviti, aggiungiContatto, rimuoviContatto, listaContatti,
			stileTesto, bloccaContatto, sbloccaContatto;

	private ActionListener al;
	private WindowListener wl;
	private Client cc;
	private HashMap<String, JFrame> finestreUtenti;
	// private NewClientGUI client;
	// LinkedList<String> utentiConnessi;
	Vector<String> words;
	JList<String> wordList;
	private String nomeClient;
	private String password;
	private Connection conn;
	private PreparedStatement stat;
	private LinkedList<String> contatti;// i contatti che ho nella mia lista
										// contatti
	private LinkedList<String> utentiCheMiHannoBloccato;
	private LinkedList<String> utentiCheHoBloccato;
	private Lock l;// per gestire la concorrenza su utentiCheMiHannoBloccato e
					// su contatti
	private MainClient mc;
	private Font font;
	private Color colore;

	public MainClient() {

		l = new ReentrantLock();
		utentiCheMiHannoBloccato = new LinkedList<String>();

		this.font = new Font("Verdana", Font.BOLD, 12);
		this.colore = Color.BLACK;

		this.mc = this;
		contatti = new LinkedList<String>();
		finestreUtenti = new HashMap<String, JFrame>();

		al = new Ascoltatore();
		wl = new AscoltatoreFinestra();
		addWindowListener(wl);

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		setLocation(d.width / 4, 0);
		setSize(d.width / 4, d.height / 2);
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
		connect = new JMenuItem("Connetti");
		connect.addActionListener(al);
		disconnect = new JMenuItem("Disconnetti");
		disconnect.addActionListener(al);
		chatWith = new JMenuItem("Chatta con");
		chatWith.addActionListener(al);
		aggiungiContatto = new JMenuItem("Aggiungi Contatto");
		aggiungiContatto.addActionListener(al);
		aggiungiContatto.setEnabled(false);
		chatWith.setEnabled(false);// rendo non disponibile il tasto per
									// chattare
		disconnect.setEnabled(false);
		rimuoviContatto = new JMenuItem("Rimuovi Contatto");
		rimuoviContatto.addActionListener(al);
		rimuoviContatto.setEnabled(false);
		listaContatti = new JMenuItem("Lista Contatti");
		listaContatti.addActionListener(al);
		listaContatti.setEnabled(false);
		stileTesto = new JMenuItem("Stile Testo");
		stileTesto.addActionListener(al);
		bloccaContatto = new JMenuItem("Blocca Contatto");
		bloccaContatto.addActionListener(al);
		sbloccaContatto = new JMenuItem("SbloccaContatto");
		sbloccaContatto.addActionListener(al);
		fileMenu.add(connect);
		fileMenu.add(disconnect);
		fileMenu.add(chatWith);
		aiuto.add(datiDimenticati);// se l'utente ha dimenticato i suoi dati
		aiuto.add(iscriviti);// per la registrazione di un nuovo utente
		opzioni.add(listaContatti);
		opzioni.add(aggiungiContatto);
		opzioni.add(rimuoviContatto);
		opzioni.add(bloccaContatto);
		opzioni.add(sbloccaContatto);
		personalizza.add(stileTesto);
		menuBar.add(fileMenu);
		menuBar.add(aiuto);
		menuBar.add(opzioni);
		menuBar.add(personalizza);
		words = new Vector<String>();
		wordList = new JList<String>(words);// Lista utenti connessi
		wordList.addMouseListener(new ActionJList(wordList));
		wordList.setMinimumSize(new Dimension(HEIGHT, WIDTH));
		wordList.setPreferredSize(new Dimension(HEIGHT, WIDTH));
		wordList.setVisibleRowCount(8);
		JScrollPane sp = new JScrollPane(wordList);
		JPanel jp = new JPanel();
		jp.add(sp);
		JLabel utenti = new JLabel("Utenti Connessi");
		JPanel pannello2 = new JPanel();
		pannello2.add(utenti);

		add(pannello2, BorderLayout.NORTH);
		add(jp, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Main Client");
		setVisible(true);

	}// costruttore

	private void abilitaChat() {
		chatWith.setEnabled(true);
	}

	private void disabilitaConnetti() {
		connect.setEnabled(false);
	}

	public void setFont(Font f) {
		Set<String> utenti = finestreUtenti.keySet();
		for (String i : utenti) {
			finestreUtenti.get(i).setFont(f);
		}
		if (!(cc == null))
			cc.setFont(f);// modifichiamo il font delle finestre di NewClient
		this.font = f;// modifichiamo il font delle future finestre
	}

	public void setForeground(Color c) {
		Set<String> utenti = finestreUtenti.keySet();
		for (String i : utenti) {
			finestreUtenti.get(i).setForeground(c);
		}
		if (!(cc == null))
			cc.setForeground(c);// modifichiamo il colore delle finestre di
								// NewClient
		this.colore = c;
	}

	public static void main(String[] args) {
		JFrame f = new MainClient();
	}
	
	public void chattaWith (String dest) {
		l.lock();// per la linkedList utentiCheMiHannoBloccato
		if (!utentiCheMiHannoBloccato.contains(dest)) {
			l.unlock();
			ClientGUI f = new ClientGUI(cc, dest, false);
			f.setFont(font);
			f.setForeground(colore);
			finestreUtenti.put(dest, f);
			System.out.println("creata finestra vera");
		} else {
			l.unlock();
			// creiamo una finestra che non invia i messaggi
			ClientGUI f = new ClientGUI(cc, dest, true);
			f.setFont(font);
			f.setForeground(colore);
			finestreUtenti.put(dest, f);
		}
	}

	public class Ascoltatore implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==wordList){
				System.out.println("ciao");
			}

			if (e.getSource() == connect) {
				String ip = JOptionPane
						.showInputDialog("inserire indirizzo ip");

				// login con user name e password

				String nome = JOptionPane.showInputDialog("login con nome");
				nomeClient = new String(nome);
				if (!nomeClient.equals("")) {
					JPasswordField pf = new JPasswordField();
					int okCxl = JOptionPane.showConfirmDialog(null, pf,
							"Password", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					if (okCxl == JOptionPane.OK_OPTION) {
						password = new String(pf.getPassword());
					}
					if (!password.equals("")) {
						cc = new Client(nomeClient, Security.cryptPassword(password), false);
						cc.start();

						AggiornaConnessi ac = new AggiornaConnessi(cc,
								wordList, words);
						ac.start();
						// per avere sempre aggiornata la lista dei contatti che
						// ci hanno bloccato
						NotificaBloccati nb = new NotificaBloccati(cc,
								utentiCheMiHannoBloccato, l);
						nb.start();
						boolean ris = cc.connetti(ip);
						if (ris) {// se tutto ha funzionato
							abilitaChat();
							disabilitaConnetti();
							disconnect.setEnabled(true);
							aggiungiContatto.setEnabled(true);
							listaContatti.setEnabled(true);
							iscriviti.setEnabled(false);
							rimuoviContatto.setEnabled(true);

							setFont(font);
							setForeground(colore);
							JOptionPane.showMessageDialog(null, null,
									"connesso al server", 1);
						} else {
							JOptionPane.showMessageDialog(null, null,
									"Username e/o password Errati",
									JOptionPane.ERROR_MESSAGE);
						}

					}// se la password non è la stringa vuota
					else {
						JOptionPane.showMessageDialog(null, null,
								"password nulla", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, null,
							"nome utente nullo", JOptionPane.ERROR_MESSAGE);
				}
			}// if è stato premuto connect

			if (e.getSource() == chatWith) {
				String dest = cc.login();
				chattaWith(dest);
			}
			
			if (e.getSource() == datiDimenticati) {
				
				String user = JOptionPane.showInputDialog("Inserisci il tuo nome utente");
				String email =  JOptionPane.showInputDialog("Inserisci la tua mail");
				cc = new Client (user, email);
				boolean ris = cc.forgetPassword();
				if (ris) {
					JOptionPane.showMessageDialog(null,"La tua password è stata reimpostata e inviata al tuo indirizzo email");
				}
				else {
					JOptionPane.showMessageDialog(null, null,
							"Dati non corretti", JOptionPane.ERROR_MESSAGE);
				}
			}
			if (e.getSource() == disconnect) {
				cc.disconnetti();
				chatWith.setEnabled(false);
				connect.setEnabled(true);
				iscriviti.setEnabled(true);
				// cancello l'elenco degli utenti connessi
				words = new Vector<String>();
				wordList.setListData(words);
				wordList.repaint();

			}
			if (e.getSource() == iscriviti) {
				String ip = JOptionPane
						.showInputDialog("inserire indirizzo ip");
				String user_name = JOptionPane.showInputDialog("username");
				String pass = "";
				JPasswordField pf2 = new JPasswordField();
				int okCxl = JOptionPane.showConfirmDialog(null, pf2,
						"Password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (okCxl == JOptionPane.OK_OPTION) {
					pass = new String(pf2.getPassword());
				}
				String email = JOptionPane.showInputDialog("Email");
				cc = new Client(user_name, Security.cryptPassword(pass), email, true);
				cc.start();
				boolean ris = cc.connetti(ip);
				if (ris) {
					JOptionPane.showMessageDialog(null,
							"Iscrizione avvenuta con succeso");
				}
			}

			if (e.getSource() == aggiungiContatto) {
				if (cc.eConnesso()) {
					String nomeContatto = JOptionPane
							.showInputDialog("Nome Contatto");
					l.lock();// sincronizzazione lista contatti
					contatti = cc.getListaContatti();
					if (!contatti.contains(nomeContatto)
							&& (!nomeContatto.equals(nomeClient))) {
						cc.inviaMessaggio("A:" + nomeContatto);// richiesta
																// aggiunta
																// contatto
						JOptionPane
								.showMessageDialog(null, "Richiesta Inviata");
					} else {
						JOptionPane.showMessageDialog(null, null,
								"Contatto già presente",
								JOptionPane.ERROR_MESSAGE);
					}
					l.unlock();
				}// se è connesso
			}
			if (e.getSource() == listaContatti) {
				l.lock();
				contatti = cc.getListaContatti();
				cc.getLockListaContatti().lock();
				LinkedList<String> utentiBloccati = new LinkedList<String>(
						cc.getUtentiBloccati());
				System.out.println("lista Contatti: " + contatti);
				System.out.println("utenti bloccati: " + utentiBloccati);
				cc.getLockListaContatti().unlock();
				JFrame pC = new PannelloContatti(contatti, utentiBloccati);
				l.unlock();
			}
			if (e.getSource() == rimuoviContatto) {
				String toRemove = JOptionPane
						.showInputDialog("Nome Contatto da Rimuovere");
				cc.inviaMessaggio("R" + toRemove);// richiesta cancellazione
													// contatto
				JOptionPane.showMessageDialog(null, "Richiesta Inviata");
			}
			if (e.getSource() == stileTesto) {
				PannelloFont pf = new PannelloFont(mc);
			}

			if (e.getSource() == bloccaContatto) {
				String target = JOptionPane
						.showInputDialog("Contatto da Bloccare");
				l.lock();
				cc.getLockListaContatti().lock();
				contatti = cc.getListaContatti();
				LinkedList<String> listaContatti = new LinkedList<String>();
				StringTokenizer st;
				for (String i : contatti) {
					st = new StringTokenizer(i, " ");
					listaContatti.add(st.nextToken());
				}
				utentiCheHoBloccato = cc.getUtentiBloccati();
				if (utentiCheHoBloccato.contains(target))
					JOptionPane.showMessageDialog(null, "Utente già Bloccato");
				else if (listaContatti.contains(target)) {
					cc.inviaMessaggio("L" + target);// richiesta blocco contatto
					JOptionPane.showMessageDialog(null, "Richiesta Inviata");
				} else
					JOptionPane.showMessageDialog(null,
							"Contatto non presente nella Lita Contatti");
				cc.getLockListaContatti().unlock();
				l.unlock();
			}
			if (e.getSource() == sbloccaContatto) {
				String target = JOptionPane
						.showInputDialog("Contatto da Sbloccare");
				l.lock();
				cc.getLockListaContatti().lock();
				utentiCheHoBloccato = cc.getUtentiBloccati();
				if (!utentiCheHoBloccato.contains(target))
					JOptionPane.showMessageDialog(null,
							"Non Hai Bloccato il contatto");
				else {
					cc.inviaMessaggio("U" + target);
					JOptionPane.showMessageDialog(null, "Richiesta Inviata");
				}
				cc.getLockListaContatti().unlock();
				l.unlock();
			}

		}// actionPerformed

	}// classe interna

	class AscoltatoreFinestra extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			if (cc != null)
				if (cc.eConnesso())
					cc.disconnetti();
			// System.out.println("provo a disconnettermi");

		}

	}// classe interna
	class ActionJList extends MouseAdapter{
		  protected JList list;
		    
		  public ActionJList(JList l){
		   list = l;
		   }
		    
		  public void mouseClicked(MouseEvent e){
		   if(e.getClickCount() == 2){
		     int index = list.locationToIndex(e.getPoint());
		     ListModel dlm = list.getModel();
		     Object item = dlm.getElementAt(index);;
		     list.ensureIndexIsVisible(index);
			 chattaWith((String)item);   
		     }
		   }
		}

}
