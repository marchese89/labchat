package client;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.JPanel;
/**
 * 
 * @author giovanni
 * 
 * Aggiorna la lista degli utenti connessi di ogni singolo client
 */
public class AggiornaConnessi extends Thread{

   private JList<String> lista;
   private Vector utenti;
   private Client cc;
   private volatile LinkedList<String> contatti, utentiConnessi,utentiCheHoBloccato,
                                       utentiCheMiHannoBloccato;

   
   public AggiornaConnessi(Client cc,JList<String> lista,Vector utenti){
	   this.lista = lista;
	   this.utenti = utenti;
	   this.cc = cc;
	   utentiCheMiHannoBloccato = new LinkedList<String>();
   }
   
   public synchronized void run(){
	   while(true){
		   if(cc.eConnesso()){
		cc.getLockListaContatti().lock();	
		contatti = cc.getListaContatti();
		utentiConnessi = cc.utentiConnessi();
		LinkedList<String> listaContatti = new LinkedList<String>(cc.getListaContatti());
		utentiCheMiHannoBloccato.clear();
		StringTokenizer st;
		for(String k:listaContatti){
			st = new StringTokenizer(k," ");
			String target = st.nextToken();
			String tf = st.nextToken();
			if(tf.charAt(0)=='t'){
				utentiCheMiHannoBloccato.add(target);
			}
		}
		if(utentiConnessi.size()>0){
			 utenti.clear();
			 for(String i : contatti){
				 if(!utentiCheMiHannoBloccato.contains(i) ){
					 i = i.substring(0, i.length()-2); 
					 /*
					  * Sottostringa da 0 a i.length() perchè gli ultimi due caratteri della stringa sono riservati
					  * alla verifica di un utente bloccato o meno (f / t).
					  */
					 JPanel jp1 = new JPanel();
					 ImageIcon icon = utentiConnessi.contains(i) ? new ImageIcon("images/icon_online.gif") : new ImageIcon("images/icon_offline.gif");
					 JLabel x = new JLabel(icon);
					 JLabel y = new JLabel(i);
					 jp1.add(x);
					 jp1.add(y);
					 utenti.addElement(jp1); 
				 }
			 }
		    lista.setListData(utenti);
			 lista.repaint();
			 
		}else{
			utenti.clear();
		    lista.setListData(utenti);
			lista.repaint();
			//System.out.println("ho azzerato la lista");
		}
		cc.getLockListaContatti().unlock();
	   }//if è connesso
		   try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
		   
	   }//while true
   }//run

}
