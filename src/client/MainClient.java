package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import sun.awt.RequestFocusController;
import sun.awt.CausedFocusEvent.Cause;
import Utility.JListWithImages;
import Utility.Security;

@SuppressWarnings("all")
public class MainClient extends JFrame {

	private JMenuItem connect, disconnect, datiDimenticati,
			iscriviti, aggiungiContatto, listaContatti,
			stileTesto, aggiornaLista,disiscrizione,cambiaPass,indirizzoServer;
	private JMenu suspendedRequest;
	private JMenuItem[] users;
	protected String usr;
	private ActionListener al;
	private WindowListener wl;
	private Client cc;
	Vector<JPanel> words;
	JListWithImages wordList;
	private JScrollPane scroll;
	private String nomeClient;
	private String password;
	private LinkedList<String> contatti;// i contatti che ho nella mia lista
										// contatti
	private LinkedList<String> utentiCheMiHannoBloccato;
	private LinkedList<String> suspendedUser;
	private volatile LinkedList<String> utentiCheHoBloccato;
	private Lock l;// per gestire la concorrenza su utentiCheMiHannoBloccato e
					// su contatti
	private JMenu opzioni;
	private MainClient mc;
	private Font font;
	private Color colore;
	private JPopupMenu Pmenu;
	private JMenuItem chat, msgOff, lock, unlock, remove;
    private String indServer;
	
	public MainClient() {
		
		indServer = "localhost";
		
		Pmenu = new JPopupMenu();
		chat = new JMenuItem("Chatta");
		chat.addMouseListener(new ActionJList(wordList));
		Pmenu.add(chat);
		msgOff = new JMenuItem("Invia messaggio offline");
		msgOff.addMouseListener(new ActionJList(wordList));
		Pmenu.add(msgOff);
		lock = new JMenuItem("Blocca contatto");
		lock.addMouseListener(new ActionJList(wordList));
		Pmenu.add(lock);
		unlock = new JMenuItem("Sblocca contatto");
		unlock.addMouseListener(new ActionJList(wordList));
		Pmenu.add(unlock);
		remove = new JMenuItem("Rimuovi contatto");
		remove.addMouseListener(new ActionJList(wordList));
		Pmenu.add(remove);
		Pmenu.addMouseListener(new ActionJList(wordList));
		l = new ReentrantLock();
		utentiCheMiHannoBloccato = new LinkedList<String>();

		this.font = new Font("Verdana", Font.BOLD, 12);
		this.colore = Color.BLACK;

		this.mc = this;
		contatti = new LinkedList<String>();
		//finestreUtenti = new HashMap<String, JFrame>();

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
		opzioni = new JMenu("Opzioni");
		JMenu personalizza = new JMenu("Personalizza");
		indirizzoServer = new JMenuItem("Indirizzo Server");
		indirizzoServer.addActionListener(al);
		datiDimenticati = new JMenuItem("Dati Dimenticati");
		datiDimenticati.addActionListener(al);
		iscriviti = new JMenuItem("Iscrizione");
		iscriviti.addActionListener(al);
		disiscrizione = new JMenuItem("Disiscriviti");
		disiscrizione.addActionListener(al);
		cambiaPass = new JMenuItem("Cambia Password");
		cambiaPass.addActionListener(al);
		connect = new JMenuItem("Connetti");
		connect.addActionListener(al);
		disconnect = new JMenuItem("Disconnetti");
		disconnect.addActionListener(al);

		aggiungiContatto = new JMenuItem("Aggiungi Contatto");
		aggiungiContatto.addActionListener(al);
		aggiungiContatto.setEnabled(false);
		// chattare
		suspendedRequest = new JMenu("Richieste in sospeso ()");
		opzioni.add(suspendedRequest);
		suspendedRequest.setEnabled(false);
		disconnect.setEnabled(false);

		listaContatti = new JMenuItem("Lista Contatti");
		listaContatti.addActionListener(al);
		listaContatti.setEnabled(false);
		stileTesto = new JMenuItem("Stile Testo");
		stileTesto.addActionListener(al);

		aggiornaLista = new JMenuItem("Aggiorna richieste");
		opzioni.add(aggiornaLista);
		aggiornaLista.addActionListener(al);
		aggiornaLista.setEnabled(false);
		fileMenu.add(connect);
		fileMenu.add(disconnect);
		fileMenu.add(indirizzoServer);
		aiuto.add(datiDimenticati);// se l'utente ha dimenticato i suoi dati
		aiuto.add(iscriviti);// per la registrazione di un nuovo utente
		aiuto.add(disiscrizione);
		aiuto.add(cambiaPass);
		disiscrizione.setEnabled(false);//disabilito queste 2 funzioni da principio
		cambiaPass.setEnabled(false);
		opzioni.add(listaContatti);
		opzioni.add(aggiungiContatto);
		personalizza.add(stileTesto);
		menuBar.add(fileMenu);
		menuBar.add(aiuto);
		menuBar.add(opzioni);
		menuBar.add(personalizza);
		words = new Vector();
		wordList = new JListWithImages();// Lista utenti connessi
		wordList.setListData(words);
		wordList.addMouseListener(new ActionJList(wordList));
		//wordList.setMinimumSize(new Dimension(HEIGHT, WIDTH));
		//wordList.setPreferredSize(new Dimension(HEIGHT, WIDTH));
		//wordList.setVisibleRowCount(8);
		scroll = new JScrollPane(wordList);
		JPanel jp = new JPanel();
		jp.add(scroll);
		JLabel utenti = new JLabel("Utenti Amici");
		JPanel pannello2 = new JPanel();
		pannello2.add(utenti);

		add(pannello2, BorderLayout.NORTH);
		add(jp, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Main Client");
		setVisible(true);

	}// costruttore

	private void contactRemove(String toRemove) {
		cc.inviaMessaggio("R" + toRemove);
		JOptionPane.showMessageDialog(null, "Richiesta Inviata");
	}

	private void contactUnlock(String target) {
		l.lock();
		cc.getLockListaContatti().lock();
		utentiCheHoBloccato = cc.getUtentiBloccati();
		if (!utentiCheHoBloccato.contains(target))
			JOptionPane.showMessageDialog(null, "Non Hai Bloccato il contatto");
		else {
			cc.inviaMessaggio("U" + target);
			utentiCheHoBloccato.remove(target);
			JOptionPane.showMessageDialog(null, "Richiesta Inviata");
		}
		cc.getLockListaContatti().unlock();
		l.unlock();
	}

	private void contactLock(String target) {
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
			utentiCheHoBloccato.add(target);
			JOptionPane.showMessageDialog(null, "Richiesta Inviata");
		} else
			JOptionPane.showMessageDialog(null,
					"Contatto non presente nella Lita Contatti");
		cc.getLockListaContatti().unlock();
		l.unlock();
	}



	public void setFont(Font f) {
	
		if (!(cc == null))
			cc.setFont(f);// modifichiamo il font delle finestre di NewClient
		this.font = f;// modifichiamo il font delle future finestre
	}

	public void setForeground(Color c) {

		if (!(cc == null))
			cc.setForeground(c);// modifichiamo il colore delle finestre di
								// NewClient
		this.colore = c;
		
	}

	public static void main(String[] args) {
		JFrame f = new MainClient();
	}

	public void chattaWith(String dest) {
		l.lock();// per la linkedList utentiCheMiHannoBloccato
		if (!utentiCheMiHannoBloccato.contains(dest))
			cc.addDest(dest, font, colore, false);
		else
			// creiamo una finestra che non invia i messaggi
			cc.addDest(dest, font, colore, true);
		l.unlock();
	}

	public class Ascoltatore implements ActionListener {
		WaitingDialog wd = new WaitingDialog();
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == connect) {
				
				String ip = indServer;
					 
                
				// login con user name e password

				String nome = JOptionPane.showInputDialog("login con nome");
				if (nome == null || nome == "")
					return;
				if(!stringControl(nome)){
					JOptionPane.showMessageDialog(null,"caratteri non consentiti:\n¦&{[*^ç<:(§"
							,null,JOptionPane.ERROR_MESSAGE);
					return;
				}
				nomeClient = new String(nome);
				if (!nomeClient.equals("")) {
					JPasswordField pf = new JPasswordField();
					
					pf.addAncestorListener(new RequestFocusListener()); //diamo il focus
					
                    int okCxl = JOptionPane.showConfirmDialog(null, pf,
							"Password", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
                    wd.setVisible(true);
                
                    
					if (okCxl == JOptionPane.OK_OPTION) {
						
						password = new String(pf.getPassword());
						
					}
					
					
					if (password != null && !password.equals("")) {
						cc = new Client(nomeClient,
								Security.cryptPassword(password), false);
						
						cc.start();
						
						utentiCheHoBloccato = cc.getUtentiBloccati();

						AggiornaConnessi ac = new AggiornaConnessi(cc,
								wordList, words,utentiCheHoBloccato);
						ac.start();
						// per avere sempre aggiornata la lista dei contatti che
						// ci hanno bloccato
						NotificaBloccati nb = new NotificaBloccati(cc,
								utentiCheMiHannoBloccato, l);
						
						nb.start();
						//finestra di inizio attesa;
						
						boolean ris = cc.connetti(ip);
						//chiudere la finestra di attesa..
						
						if (ris) {// se tutto ha funzionato
							suspendedUser = cc.getSuspendedList();
							initializeSuspendedUser();
							
							connect.setEnabled(false);
							disconnect.setEnabled(true);
							aggiungiContatto.setEnabled(true);
							listaContatti.setEnabled(true);
							iscriviti.setEnabled(false);
							datiDimenticati.setEnabled(false);
							disiscrizione.setEnabled(true);
							cambiaPass.setEnabled(true);
							setFont(font);
							setForeground(colore);
							wd.setVisible(false);
							JOptionPane.showMessageDialog(null, null,
									"Connessione riuscita!", 1);
							mc.setTitle("Connesso Come: "+nomeClient);
						} else {
							JOptionPane.showMessageDialog(null, null,
									"Dati errati o sessione già attiva!",
									JOptionPane.ERROR_MESSAGE);
						}
						wd.setVisible(false);

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
			/*
			 * if (e.getSource() == chatWith) { String dest = cc.login();
			 * chattaWith(dest); }
			 */

			if (users != null) {
				for (int i = 0; i < users.length; i++) {
					if (e.getSource() == users[i]) {
						String mittente = users[i].getText();
						int ris = JOptionPane
								.showConfirmDialog(
										null,
										"L'utente "
												+ mittente
												+ " vuole aggiungerti come contatto. Accetti?");
						if (ris == JOptionPane.OK_OPTION) {
							cc.inviaMessaggio("[¦Y¦" + mittente);// conferma richiesta
							suspendedUser = cc.getSuspendedList();
							initializeSuspendedUser();
						}
						if (ris == JOptionPane.NO_OPTION) {
							cc.inviaMessaggio("[¦N¦" + mittente);// nega richiesta
							suspendedUser = cc.getSuspendedList();
							initializeSuspendedUser();
						}
					}
				}
			}

			if (e.getSource() == datiDimenticati) {
				String ip = indServer;
				String user = JOptionPane
						.showInputDialog("Inserisci il tuo nome utente");
				if (user == null || user == "") return;
				if(!stringControl(user)){
					JOptionPane.showMessageDialog(null,"caratteri non consentiti:\n¦&{[*^ç<:(§"
							,null,JOptionPane.ERROR_MESSAGE);
					return;
				}
				String email = JOptionPane
						.showInputDialog("Inserisci la tua mail");
				if ( email== null || email == "") return;
				if(!stringControl(email)){
					JOptionPane.showMessageDialog(null,"caratteri non consentiti:\n¦&{[*^ç<:(§"
							,null,JOptionPane.ERROR_MESSAGE);
					return;
				}
				cc = new Client(user, email);
				String ris = cc.forgetPassword(ip);
				if (ris != null) {
					JOptionPane.showMessageDialog(null,
							"La tua password è stata reimpostata e inviata al tuo indirizzo email ed è: "
									+ ris);
				} else {
					JOptionPane.showMessageDialog(null, null,
							"Dati non corretti", JOptionPane.ERROR_MESSAGE);
				}
			}
			if (e.getSource() == disconnect) {
				cc.disconnetti();
				
				connect.setEnabled(true);
				iscriviti.setEnabled(true);
				datiDimenticati.setEnabled(true);
				disiscrizione.setEnabled(false);
				cambiaPass.setEnabled(false);
				// cancello l'elenco degli utenti connessi
				words = new Vector<JPanel>();
				wordList.setListData(words);
				wordList.repaint();
                mc.setTitle("Main Client");
			}
			if (e.getSource() == iscriviti) {
				String ip = indServer;
				String user_name = JOptionPane.showInputDialog("username");
				if(user_name == null || user_name == "")
					return;
				if(!stringControl(user_name)){
					JOptionPane.showMessageDialog(null,"caratteri non consentiti:\n¦&{[*^ç<:(§"
							,null,JOptionPane.ERROR_MESSAGE);
					return;
				}
				String pass = "";
				JPasswordField pf2 = new JPasswordField();
				pf2.addAncestorListener(new RequestFocusListener());//diamo il focus
				
				int okCxl = JOptionPane.showConfirmDialog(null, pf2,
						"Password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (okCxl == JOptionPane.OK_OPTION) {
					pass = new String(pf2.getPassword());
				}
				if(pass == null || pass == "")
					return;
				String email = JOptionPane.showInputDialog("Email");
				if (email == null || email =="")
					return;
				if(!stringControl(email)){
					JOptionPane.showMessageDialog(null,"caratteri non consentiti:\n¦&{[*^ç<:(§"
							,null,JOptionPane.ERROR_MESSAGE);
					return;
				}
				cc = new Client(user_name, Security.cryptPassword(pass), email,
						true);
				cc.start();
				boolean ris = cc.connetti(ip);
				if (ris) {
					JOptionPane.showMessageDialog(null,null,
							"Iscrizione avvenuta con succeso",JOptionPane.PLAIN_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(null,null,
							"Iscrizione non riuscita",JOptionPane.ERROR_MESSAGE);
				}
				
			}

			if (e.getSource() == aggiungiContatto) {
				if (cc.eConnesso()) {
					String nomeContatto = JOptionPane
							.showInputDialog("Nome Contatto");
					if(nomeContatto == null || nomeContatto.equals("")){
						JOptionPane.showMessageDialog(null,"Nome non valido!"
								,null,JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(!stringControl(nomeContatto)){
						JOptionPane.showMessageDialog(null,"caratteri non consentiti:\n¦&{[*^ç<:(§"
								,null,JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					l.lock();// sincronizzazione lista contatti
					cc.getLockListaContatti().lock();
					contatti = cc.getListaContatti();
					boolean contains = false;
					for (String i : contatti){
						if (nomeContatto.equals(i.substring(0,i.length()-2))) {
							contains = true;
							break;
						}
					}
					if (!contains 
							&& (!nomeContatto.equals(nomeClient))) {
						boolean r =cc.aggiungiContatto(nomeContatto);// richiesta aggiunta contatto
						
						if(r)
						JOptionPane
								.showMessageDialog(null, "Richiesta Inviata");
						else
							JOptionPane.showMessageDialog(null, "L'utente non esiste",null,
									JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, null,
								"Contatto già presente",
								JOptionPane.ERROR_MESSAGE);
					}
					cc.getLockListaContatti().unlock();
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

			if (e.getSource() == stileTesto) {
				PannelloFont pf = new PannelloFont(mc);
			}
			if (e.getSource() == aggiornaLista) {
				wd.setVisible(true);
				suspendedUser = cc.getSuspendedList();
				initializeSuspendedUser();
				wd.setVisible(false);
			}
			if(e.getSource() == disiscrizione){//rimuove l'utente dal DB
				JPasswordField pf = new JPasswordField();
				pf.addAncestorListener(new RequestFocusListener()); //diamo il focus
                int okCxl = JOptionPane.showConfirmDialog(null, pf,
						"Password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				
				if (okCxl == JOptionPane.OK_OPTION) {
					
					password = new String(pf.getPassword());
					
				}
				boolean risultato = cc.Disiscrizione(Security.cryptPassword(password));
				
				if(risultato){
					cc.disconnetti();
					connect.setEnabled(true);
					iscriviti.setEnabled(true);
					datiDimenticati.setEnabled(true);
					disiscrizione.setEnabled(false);
					cambiaPass.setEnabled(false);
					// cancello l'elenco degli utenti connessi
					words = new Vector<JPanel>();
					wordList.setListData(words);
					wordList.repaint();
	                mc.setTitle("Main Client");
					JOptionPane.showMessageDialog(null, "Disiscrizione Avvenuta con Successo");
				}
				else
					JOptionPane.showMessageDialog(null, "Disiscrizione non Avvenuta",null,JOptionPane.ERROR_MESSAGE);
			
				
				
				
			}
            if(e.getSource() == cambiaPass){
            	String oldPw = null;
                String newPw = null;
            	//prendiamo la vecchia password
            	JPasswordField pf = new JPasswordField();
				pf.addAncestorListener(new RequestFocusListener()); //diamo il focus
                int okCxl = JOptionPane.showConfirmDialog(null, pf,
						"Vecchia Password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				
				if (okCxl == JOptionPane.OK_OPTION) {
					
					oldPw = new String(pf.getPassword());
					
				}
				if(oldPw == null || oldPw.equals("")){
					JOptionPane.showMessageDialog(null, "Password non valida", null, JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				JPasswordField pf2 = new JPasswordField();
				pf2.addAncestorListener(new RequestFocusListener()); //diamo il focus
                int okCxl2 = JOptionPane.showConfirmDialog(null, pf2,
						"Nuova Password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				
				if (okCxl2 == JOptionPane.OK_OPTION) {
					
					newPw = new String(pf2.getPassword());
					
				}
                if(newPw == null || newPw.equals("")){
                	JOptionPane.showMessageDialog(null, "Password non valida", null, JOptionPane.ERROR_MESSAGE);
                	return;
                }
                wd.setVisible(true);
                boolean r =cc.modificaPass(Security.cryptPassword(oldPw), 
                		                   Security.cryptPassword(newPw));
                if(r){
                	JOptionPane.showMessageDialog(null, "Password Modificata");
                }else{
                	JOptionPane.showMessageDialog(null, "Password non Modificata",null,JOptionPane.ERROR_MESSAGE);
                }
                wd.setVisible(false);
            }
            if (e.getSource() == indirizzoServer){
            	indServer = JOptionPane.showInputDialog(null, "Indirizzo del Server", indServer);
            }

		}// actionPerformed

		private void initializeSuspendedUser() {
			if (suspendedUser != null) {
				aggiornaLista.setEnabled(true);
				opzioni.remove(suspendedRequest);
				int size = suspendedUser.size();
				suspendedRequest = new JMenu();
				suspendedRequest.setEnabled(true);
				suspendedRequest.setText("Richieste in sospeso (" + size + ")");
				opzioni.add(suspendedRequest);
				users = new JMenuItem[size];
				for (int i = 0; i < users.length; i++) {
					users[i] = new JMenuItem(suspendedUser.get(i));
					suspendedRequest.add(users[i]);
					users[i].addActionListener(al);
				}
			}
		}// initializeSuspendedUser
		
		private boolean stringControl(String s){
			
			char[] stringa = s.toCharArray();
			for(char i:stringa){
				switch(i){
				case '¦':return false;
				case '&':return false;
				case '{':return false;
				case '[':return false;
				case '*':return false;
				case '^':return false;
				case 'ç':return false;
				case '<':return false;
				case ':':return false;
				case '(':return false;
				case '§' : return false;
                
				}
			}
			return true;
		}

	}// classe interna

	class AscoltatoreFinestra extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			if (cc != null)
				if (cc.eConnesso())
					cc.disconnetti();

		}

	}// classe interna

	class ActionJList extends MouseAdapter {
		protected JList list;

		public ActionJList(JList l) {
			list = l;
		}

		public void mouseClicked(MouseEvent e) {
			// Da modificare togliendo la parte che non serve.
			if (e.getClickCount() == 2) {
				int index = list.locationToIndex(e.getPoint());
				if (index >= 0) {
					ListModel dlm = list.getModel();
					Object item = dlm.getElementAt(index);
					list.ensureIndexIsVisible(index);
					JPanel x = (JPanel) item;
					JLabel l = (JLabel) x.getComponent(1);
					JLabel icoL = (JLabel) x.getComponent(0);
					ImageIcon im = (ImageIcon) icoL.getIcon();
					// if (im.toString().contains("online")) //se l'utente è
					// online
					chattaWith(l.getText());
					// else {
					// chattaOff(l.getText());
					// }
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				int index = list.locationToIndex(e.getPoint());
				if (index >= 0) {
					ListModel dlm = list.getModel();
					Object item = dlm.getElementAt(index);
					list.ensureIndexIsVisible(index);
					JPanel x = (JPanel) item;
					JLabel l = (JLabel) x.getComponent(1);
					usr = new String(l.getText());
					if (utentiCheHoBloccato != null
							&& utentiCheHoBloccato.contains(usr)) {
						chat.setEnabled(false);
						msgOff.setEnabled(false);
						lock.setEnabled(false);
						unlock.setEnabled(true);
						remove.setEnabled(true);
					} else {
						if (cc.isOnline(usr)) {
							chat.setEnabled(true);
							msgOff.setEnabled(false);
							lock.setEnabled(true);
							unlock.setEnabled(false);
							remove.setEnabled(true);
						} else {
							chat.setEnabled(false);
							msgOff.setEnabled(true);
							lock.setEnabled(true);
							unlock.setEnabled(false);
							remove.setEnabled(true);
						}
					}
					Pmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			if (e.getSource() == chat) {
				if (usr != null) {
					chattaWith(usr);
					usr = null;
				}
			}
			if (e.getSource() == msgOff) {
				if (usr != null) {
					chattaWith(usr);
					usr = null;
				}
			}
			if (e.getSource() == lock) {
				if (usr != null) {
				contactLock(usr);
				usr = null;
				}
			}
			if (e.getSource() == unlock) {
				if (usr != null) {
				contactUnlock(usr);
				usr = null;
				}
			}
			if (e.getSource() == remove) {
				if (usr != null) {
				contactRemove(usr);
				usr = null;
			}
		}
	}
	}
}



class RequestFocusListener implements AncestorListener
{
	private boolean removeListener;

	/*
	 *  Convenience constructor. The listener is only used once and then it is
	 *  removed from the component.
	 */
	public RequestFocusListener()
	{
		this(true);
	}

	/*
	 *  Constructor that controls whether this listen can be used once or
	 *  multiple times.
	 *
	 *  @param removeListener when true this listener is only invoked once
	 *                        otherwise it can be invoked multiple times.
	 */
	public RequestFocusListener(boolean removeListener)
	{
		this.removeListener = removeListener;
	}

	@Override
	public void ancestorAdded(AncestorEvent e)
	{
		JComponent component = e.getComponent();
		component.requestFocusInWindow();

		if (removeListener)
			component.removeAncestorListener( this );
	}

	@Override
	public void ancestorMoved(AncestorEvent e) {}

	@Override
	public void ancestorRemoved(AncestorEvent e) {}
}

