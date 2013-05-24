package Utility;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class JListWithImages extends JList {

 public JListWithImages() { 
   setCellRenderer(new CustomCellRenderer()); 
   }

 class CustomCellRenderer implements ListCellRenderer {
	
   public Component getListCellRendererComponent
    (JList list, Object value, int index, 
     boolean isSelected,boolean cellHasFocus) {
     Component component = (Component)value;
     component.setBackground
      (isSelected ? Color.black : Color.white);
     component.setForeground
      (isSelected ? Color.white : Color.black);
     return component;
     }
   }
}
