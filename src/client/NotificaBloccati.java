package client;

import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;

public class NotificaBloccati extends Thread{

   private LinkedList<String> contatti,utentiCheMiHannoBloccato;
   private StringTokenizer st;
   private String candidato,tf;
   private Lock utentiBloccanti,listaContatti;
   private Client c;
   
   public NotificaBloccati(Client c,LinkedList<String> utenti,Lock l){
	   this.c = c;
	   utentiCheMiHannoBloccato = utenti;
	   this.utentiBloccanti = l;
	   listaContatti = c.getLockListaContatti();
   }
   
   public void run(){
	   while(true){
		   utentiBloccanti.lock();
		   listaContatti.lock();
		   utentiCheMiHannoBloccato.clear();
		   contatti = new LinkedList<String>(c.getListaContatti());
		   if(contatti.size()>0){
		   for(String i:contatti){
			   //try{
			   st = new StringTokenizer(i," ");
			   /*
			   }catch(Exception e){
				   System.out.println("lista originale: "+c.getListaContatti());
				   System.out.println("lista appoggio: "+contatti);
				   System.out.println("lista contatti: "+utentiCheMiHannoBloccato);
				   break;
			   }
			   */
			   candidato = st.nextToken();
			   tf = st.nextToken();
			   if(tf.charAt(0) == 't'){
				   utentiCheMiHannoBloccato.add(candidato);
			   }
		   }
		   }//if listaAppoggio.size()>0
		   listaContatti.unlock();
		   utentiBloccanti.unlock();
	   }
   }

}
