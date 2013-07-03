package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("all")
public class ServerGUI extends JFrame {

	private Server s;
	private JButton b;
	private JScrollPane js;
	private JTextArea ricezione;

	public ServerGUI() {

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		setLocation(d.width / 4, d.height / 4);
		setSize(d.width / 2, d.height / 2);
		this.setLayout(new BorderLayout());

		JPanel jp = new JPanel();
		ricezione = new JTextArea(21, 59);
		ricezione.setEditable(false);// non si può modificare il testo da fuori
		ricezione.setWrapStyleWord(true);// va a capo di parola in parola
		ricezione.setLineWrap(true);// va a capo automaticamente
		js = new JScrollPane(ricezione);
		jp.add(js);
        add(jp, BorderLayout.EAST);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chat Server");
		setVisible(true);

		s = new Server(ricezione);
		Thread t = new Thread(s);
		t.start();

	}
	public void append (String s)  {
		ricezione.append(s);
	}
	public static void main(String[] args) {

		JFrame f = new ServerGUI();

	}


}
