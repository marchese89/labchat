package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class TextPaneH extends JTextPane {
	public TextPaneH(){
		super();
		this.setContentType("text/html");
	}
	public void appendWho(String s) {
		StyledDocument doc = this.getStyledDocument();

	//  Define a keyword attribute

	SimpleAttributeSet keyWord = new SimpleAttributeSet();
	StyleConstants.setForeground(keyWord, Color.RED);
	StyleConstants.setBackground(keyWord, new Color(0xFFFF99));
	StyleConstants.setBold(keyWord, true);

	//  Add some text

	try
	{
	   // doc.insertString(0, s, null );
	    doc.insertString(doc.getLength(), s+"\n", keyWord );
	}
	catch(Exception e) { System.out.println(e); }
		}
	public void appendWhoF(String s) {
		StyledDocument doc = this.getStyledDocument();

	//  Define a keyword attribute

	SimpleAttributeSet keyWord = new SimpleAttributeSet();
	StyleConstants.setForeground(keyWord, Color.RED);
	StyleConstants.setBackground(keyWord, new Color(0x99FF99));
	StyleConstants.setBold(keyWord, true);

	//  Add some text

	try
	{
	   // doc.insertString(0, s, null );
	    doc.insertString(doc.getLength(), s+"\n", keyWord );
	}
	catch(Exception e) { System.out.println(e); }
		}
	public void appendThat(String s) {
		StyledDocument doc = this.getStyledDocument();

	//  Define a keyword attribute

	SimpleAttributeSet keyWord = new SimpleAttributeSet();
	//StyleConstants.setForeground(keyWord, Color.RED);
	StyleConstants.setBackground(keyWord, Color.WHITE);
	StyleConstants.setBold(keyWord, false);

	//  Add some text

	try
	{
	   // doc.insertString(0, s, null );
	    doc.insertString(doc.getLength(), s+"\n", keyWord );
	}
	catch(Exception e) { System.out.println(e); }
		}
	public static void main(String[]args){
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		TextPaneH t = new TextPaneH();
		JScrollPane jp = new JScrollPane(t);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		jp.setPreferredSize(new Dimension(d.width / 2, d.height / 2));
		p.add(jp);
		f.add(p);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
        t.appendWho("Tu:");
        t.appendThat("ciao");
        t.appendThat("come va");
        System.out.println(t.getText());
        //t.setText("<b><font color = red>Tu:\n</font></b>");
		f.setVisible(true);
		
	}
}
