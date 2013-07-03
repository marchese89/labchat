package Utility;

import java.util.Date;
import javax.mail.Authenticator;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;



class SMTPAuthenticator extends Authenticator
{
   protected String username;
   protected String password;
   
 

   public SMTPAuthenticator(String username, String password)
   {
	   super();//riga aggiunta
       this.username = username;
       this.password = password;
   }
   

   @Override
   protected PasswordAuthentication getPasswordAuthentication()
   {
       return new PasswordAuthentication(this.username, this.password);
   }
}
public class Send {
	
	  public static void main(String[]args){
			try {
				send("prolagd1@gmail.com","marchese.antoniogiovanni@gmail.com","ciao");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }

   public static void send(String fromAddr , String toAddr , String content) throws MessagingException, Exception
   {
       Properties props = System.getProperties();
       // Imposto manualmente host e username
       props.put("mail.smtp.host" , "smtp.gmail.com");
       props.put("mail.user" , "prolagd1@gmail.com");
       
       props.put("mail.smtp.port", "465");
       props.put("mail.smtp.starttls.enable", "true");
       // Da impostare in base al sistema di autenticazione del server SMTP
       props.put("mail.smtp.auth" , "true");
       props.put("mail.smtp.socketFactory.port", "465");
       props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory"); 
       props.put("mail.smtp.socketFactory.fallback", "false"); 
     
       // Creo un autenticator di tipo SMTPAuthenticator con nome utente e
       // password (di seguito nel documento)
       Authenticator auth = new SMTPAuthenticator("prolagd1@gmail.com" , "11071989");
       
       // Creo una sessione con le informazioni appena inserite
       Session session = Session.getDefaultInstance(props , auth);
       session.setDebug(false);
       
       Message message = new MimeMessage(session);

       // Creo un InternetAddress con il mittente
       InternetAddress from = new InternetAddress(fromAddr);
       
       // Ciclo i toAddress e i ccAddress e li trasformo in un'array di
       // InternetAddress
       InternetAddress to = new InternetAddress(toAddr);
 
      
         
       // imposto il mittente del messaggio
       message.setFrom(from);
       // imposto i destinatari e i cc
       message.setRecipient(Message.RecipientType.TO,to);

       // imposto l'oggetto della mail
       message.setSubject("Recovery password");
       // imposto la data di invio
       message.setSentDate(new Date());
       // imposto il corpo della mail
       
       message.setText("Nuova password\n"+content);
   
       // Invio la mail
      Transport.send(message); 

   }
}