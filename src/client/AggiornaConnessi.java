package client;

import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JList;
/**
 * 
 * @author giovanni
 * 
 * Aggiorna la lista degli utenti connessi di ogni singolo client
 */
public class AggiornaConnessi extends Thread{

   private JList<String> lista;
   private Vector<String>utenti;
   private Client cc;
   private volatile LinkedList<String> utentiConnessi,utentiCheHoBloccato,
                                       utentiCheMiHannoBloccato;

   
   public AggiornaConnessi(Client cc,JList<String> lista,Vector<String> utenti){
	   this.lista = lista;
	   this.utenti = utenti;
	   this.cc = cc;
	   utentiCheMiHannoBloccato = new LinkedList<String>();
   }
   
   public synchronized void run(){
	   while(true){
		   if(cc.eConnesso()){
		cc.getLockListaContatti().lock();	 
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
			 for(String i : utentiConnessi){
				 if(!utentiCheMiHannoBloccato.contains(i))
					 utenti.add(i);
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
