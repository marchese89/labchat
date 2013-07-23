package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WaitingDialog extends JFrame {
	public WaitingDialog() {
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d1 = kit.getScreenSize();
		setLocation(d1.width / 4, d1.height / 4);
		setSize(d1.width / 8, d1.height / 8);
		final JDialog d = new JDialog();
		JPanel p1 = new JPanel(new GridBagLayout());
		p1.add(new JLabel("Please Wait..."), new GridBagConstraints());
		d.getContentPane().add(p1);
		d.setSize(100, 100);
		d.setLocationRelativeTo(this);
		d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		d.setModal(true);
		this.add(p1); 
		this.setVisible(true);
	}
}
