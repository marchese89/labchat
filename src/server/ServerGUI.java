package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerGUI extends JFrame{
    public ServerGUI(){
	Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension d = kit.getScreenSize();
    setLocation(d.width/4, d.height/4);
    setSize(d.width/2, d.height/2);
    this.setLayout(new BorderLayout());
	JPanel jp=new JPanel();
	JScrollPane js=new JScrollPane(new JTextArea(19,30));
	jp.add(js);
	add(jp,BorderLayout.CENTER);
	
    }
	public static void main(String[] args) {
		JFrame f= new ServerGUI();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Chat Server");
        f.setVisible(true);
	}

}
