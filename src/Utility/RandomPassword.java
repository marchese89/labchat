package Utility;

import java.util.Random;

public class RandomPassword {
	static final String ALPHABET = "0123456789ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
	static Random rnd = new Random(System.currentTimeMillis());

	public static String newRandomPassword(int lenght) {
		StringBuilder sb = new StringBuilder(lenght);
		for (int i = 0; i < lenght; i++) {
			sb.append(ALPHABET.charAt(rnd.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}
}
