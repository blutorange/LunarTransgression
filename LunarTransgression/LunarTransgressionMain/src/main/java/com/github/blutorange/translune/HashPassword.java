package com.github.blutorange.translune;

import com.github.blutorange.translune.PasswordStorage.CannotPerformOperationException;

public class HashPassword {
	public static void main(final String[] args) {
		if (args.length != 1 || args[0] == null || args[0].isEmpty()) {
			System.err.println("Give plaintext password as first argument");
			System.exit(0);
		}
		final String plain = args[0];
		try {
			final String hash = PasswordStorage.createHash(plain);
			System.out.println("Plain:  " + plain);
			System.out.println("Hashed: " + hash);
		}
		catch (final CannotPerformOperationException e) {
			System.err.println("Could not hash password");
			e.printStackTrace();
		}
	}
}