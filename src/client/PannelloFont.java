package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class PannelloFont extends JFrame {
	private JComboBox<String> selettoreFont;
	private JComboBox<String> selettoreColore;
	private JComboBox<Integer> selettoreDimCarattere;
	private JPanel p;
	private JButton conferma;
	private JLabel font;
	private JLabel testoProva;
	private JLabel colore;
	private String pbi = "Plain";
	private int dimensioneCarattere;
	private JRadioButton plain;
	private JRadioButton bold;
	private JRadioButton italic;
	private MainClient mc;

	public PannelloFont(MainClient mc) {
		this.mc = mc;
		dimensioneCarattere = 10;

		Ascoltatore al = new Ascoltatore();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		setLocation(d.width / 4, d.height / 4);
		setSize(d.width / 4 - 50, d.height / 2);
		setTitle("Editing Testo");
		p = new JPanel();
		p.setLayout(new FlowLayout());
		conferma = new JButton("Conferma");
		conferma.addActionListener(al);
		font = new JLabel("Font");
		testoProva = new JLabel("Testo di Prova");
		colore = new JLabel("Colore");
		selettoreFont = new JComboBox<String>();
		selettoreFont.setPreferredSize(new Dimension(130, 20));
		selettoreFont.addActionListener(al);
		selettoreColore = new JComboBox<String>();
		selettoreColore.setPreferredSize(new Dimension(100, 20));
		selettoreColore.addActionListener(al);
		selettoreColore.addItem("Nero");
		selettoreColore.addItem("Blu");
		selettoreColore.addItem("Ciano");
		selettoreColore.addItem("Grigio Scuro");
		selettoreColore.addItem("Grigio");
		selettoreColore.addItem("Verde");
		selettoreColore.addItem("Grigio Chiaro");
		selettoreColore.addItem("Magenta");
		selettoreColore.addItem("Arancione");
		selettoreColore.addItem("Rosa");
		selettoreColore.addItem("Rosso");
		selettoreColore.addItem("Bianco");
		selettoreColore.addItem("Giallo");

		selettoreDimCarattere = new JComboBox<Integer>();
		selettoreDimCarattere.setEditable(true);
		selettoreDimCarattere.addItem(8);
		selettoreDimCarattere.addItem(10);
		selettoreDimCarattere.addItem(12);
		selettoreDimCarattere.addItem(14);
		selettoreDimCarattere.addItem(16);
		selettoreDimCarattere.addItem(18);
		selettoreDimCarattere.addItem(20);
		selettoreDimCarattere.addActionListener(al);

		ButtonGroup gruppo = new ButtonGroup();
		plain = new JRadioButton("Plain");
		bold = new JRadioButton("Bold");
		italic = new JRadioButton("Italic");
		gruppo.add(plain);
		gruppo.add(bold);
		gruppo.add(italic);
		plain.addActionListener(al);
		bold.addActionListener(al);
		italic.addActionListener(al);

		JLabel dimensione = new JLabel("Dimensione Carattere");

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		String[] fonts = ge.getAvailableFontFamilyNames();
		for (int i = 0; i < fonts.length; i++)
			selettoreFont.addItem(fonts[i]);
		p.add(font);
		p.add(selettoreFont);
		p.add(conferma);
		p.add(colore);
		p.add(selettoreColore);
		p.add(dimensione);
		p.add(selettoreDimCarattere);
		p.add(plain);
		p.add(bold);
		p.add(italic);
		p.add(testoProva);

		add(p);
		setVisible(true);
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Font getFont() {
		return testoProva.getFont();
	}

	public Color getForeground() {
		return testoProva.getForeground();
	}

	public void setFont(Font f) {
		mc.setFont(f);
	}

	public void setForeground(Color c) {
		mc.setForeground(c);
	}

	class Ascoltatore implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == selettoreFont) {
				testoProva.setFont(new Font((String) selettoreFont
						.getSelectedItem(), Font.ITALIC, dimensioneCarattere));

			}
			if (e.getSource() == selettoreColore) {
				String colore = (String) selettoreColore.getSelectedItem();
				switch (colore) {
				case "Nero":
					testoProva.setForeground(Color.BLACK);
					break;
				case "Blu":
					testoProva.setForeground(Color.BLUE);
					break;
				case "Ciano":
					testoProva.setForeground(Color.CYAN);
					break;
				case "Grigio Scuro":
					testoProva.setForeground(Color.DARK_GRAY);
					break;
				case "Grigio":
					testoProva.setForeground(Color.GRAY);
					break;
				case "Verde":
					testoProva.setForeground(Color.GREEN);
					break;
				case "Grigio Chiaro":
					testoProva.setForeground(Color.LIGHT_GRAY);
					break;
				case "Magenta":
					testoProva.setForeground(Color.MAGENTA);
					break;
				case "Arancione":
					testoProva.setForeground(Color.ORANGE);
					break;
				case "Rosa":
					testoProva.setForeground(Color.PINK);
					break;
				case "Rosso":
					testoProva.setForeground(Color.RED);
					break;
				case "Bianco":
					testoProva.setForeground(Color.WHITE);
					break;
				case "Giallo":
					testoProva.setForeground(Color.YELLOW);
					break;
				}
			}
			if (e.getSource() == selettoreDimCarattere) {
				dimensioneCarattere = (int) selettoreDimCarattere
						.getSelectedItem();
				switch (pbi) {
				case "Plain":
					testoProva
							.setFont(new Font((String) selettoreFont
									.getSelectedItem(), Font.PLAIN,
									dimensioneCarattere));
					break;
				case "Bold":
					testoProva
							.setFont(new Font((String) selettoreFont
									.getSelectedItem(), Font.BOLD,
									dimensioneCarattere));

					break;
				case "Italic":
					testoProva.setFont(new Font((String) selettoreFont
							.getSelectedItem(), Font.ITALIC,
							dimensioneCarattere));

					break;
				}
			}

			if (e.getSource() == plain) {
				pbi = new String("Plain");
				testoProva.setFont(new Font((String) selettoreFont
						.getSelectedItem(), Font.PLAIN, dimensioneCarattere));

			}

			if (e.getSource() == bold) {
				pbi = new String("Bold");
				testoProva.setFont(new Font((String) selettoreFont
						.getSelectedItem(), Font.BOLD, dimensioneCarattere));

			}

			if (e.getSource() == italic) {
				pbi = new String("Italic");
				testoProva.setFont(new Font((String) selettoreFont
						.getSelectedItem(), Font.ITALIC, dimensioneCarattere));

			}

			if (e.getSource() == conferma) {
				setFont(getFont());
				setForeground(getForeground());
			}
		}// actionPerformed

	}

}
