package Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
	   public static String cryptPassword(final String password) {
	        MessageDigest md=null;
	        try {
	            //we use MD5 Algorithm
	            md = MessageDigest.getInstance("SHA-1");
	        } catch (NoSuchAlgorithmException ex) {
	            ex.printStackTrace();
	        }
	        md.update(password.getBytes());
	        return new String(md.digest());
	     }
}
