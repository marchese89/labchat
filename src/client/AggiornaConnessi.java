package client;

import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.locks.Lock;

import javax.swing.JList;
import javax.swing.JOptionPane;

public class AggiornaConnessi extends Thread{

   private JList<String> lista;
   private Vector<String>utenti;
   private NewClient cc;
   private volatile LinkedList<String> utentiConnessi;

   
   public AggiornaConnessi(NewClient cc,JList<String> lista,Vector<String> utenti){
	   this.lista = lista;
	   this.utenti = utenti;
	   this.cc = cc;
	  
   }
   
   public synchronized void run(){
	   while(true){
		   if(cc.eConnesso()){
			 
		utentiConnessi = cc.utentiConnessi();
		if(utentiConnessi.size()>0){
			 utenti.clear();
			 for(String i : utentiConnessi)
				 utenti.add(i);
			 
		     lista.setListData(utenti);
			 lista.repaint();
			 
			 System.out.println("utennti connessi: "+utenti);
			 
		}
		
	   }//if è connesso
		   try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
		   
	   }//while true
   }//run

}
