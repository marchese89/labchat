package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WaitingDialog extends JFrame {
	public WaitingDialog() {
		JPanel p1 = new JPanel(new GridBagLayout());
		this.add(p1);
		this.setTitle("Please wait...");
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d1 = kit.getScreenSize();
		setLocation(d1.width / 4, d1.height / 4);
		setSize(d1.width / 6, d1.height / 20);
		
	}
}
