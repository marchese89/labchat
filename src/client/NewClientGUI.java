package client;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class NewClientGUI extends JFrame{

	private JButton b;
	private JScrollPane js;
	private JTextArea ricezione;
	private JTextArea invio;
	private NewClient cc;
	private ActionListener al;
	private String destinatario;
    private RicezioneClient rc;
    
	public NewClientGUI(NewClient cc) {
        
	    this.cc = cc;
	
		al = new Ascoltatore();
        //dimensione, posizione e layout del frame
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		setLocation(d.width / 4, d.height / 4);
		setSize(d.width / 2, d.height / 2);
		this.setLayout(new BorderLayout());
        //pannelli vari
		JPanel jp = new JPanel();
		ricezione = new JTextArea(13, 30);
		js = new JScrollPane(ricezione);
		jp.add(js);
		invio = new JTextArea(5, 30);
		JScrollPane js2 = new JScrollPane(invio);
		JPanel jp2 = new JPanel();
		jp2.add(js2);
		add(jp, BorderLayout.NORTH);
		add(jp2, BorderLayout.CENTER);

		b = new JButton("Invia");

		b.addActionListener(al);
		add(b, BorderLayout.AFTER_LAST_LINE);
		
		
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		destinatario = cc.getDestinatario();
		setTitle("Conversazione con "+destinatario);
	    setVisible(true);
	    rc = new RicezioneClient(cc, ricezione);
	    rc.start();
       
	}
   

	public class Ascoltatore implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == b) {
				if (!destinatario.equals("server")) {
					Integer d = Integer.parseInt(destinatario);
					cc.inviaMessaggio(d + ":" + invio.getText());
					ricezione.append("Hai scritto:\n" + invio.getText() + "\n");
				} else {
					cc.inviaMessaggio(destinatario + ":" + invio.getText());
					ricezione.append("Hai scritto:\n" + invio.getText() + "\n");
				}
				invio.setText("");
			}

		

		}

	}


}
