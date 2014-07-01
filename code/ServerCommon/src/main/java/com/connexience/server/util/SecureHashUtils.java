package com.connexience.server.util;

import com.connexience.server.ConnexienceException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Generates and validates secure hashes using PDKDF2 (w/ SHA256)
 *
 * @author Derek Mortimer
 */
public class SecureHashUtils
{
	private static final int ITERATIONS = 10 * 1024;

	private static final int SALT_LENGTH = 32;

	private static final int DESIRED_KEY_LENGTH = 256;

	/**
	 * Computes a salted PBKDF2 hash of given plain text suitable for storage. Empty passwords are not supported.
	 *
	 * <em>Warning:</em> two successive calls to this method will result in different salts, you <em>must</em> use the
	 * {@link #check(String, String) check} method to verify a plain text against a stored salt/hash.
	 */
	public static String generateSaltedHash(final String plainText) throws ConnexienceException
	{
		try
		{
			final byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(SALT_LENGTH);

			// store the salt with the plain text
			return Base64.encodeBase64String(salt) + "$" + hash(plainText, salt);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new ConnexienceException("Problem while generating secure hash.", e);
		}
	}

	/**
	 * Checks whether given plain text corresponds to a stored salted hash.
	 */
	public static boolean check(final String plainText, final String saltAndHash) throws ConnexienceException
	{
		// 0 = salt, 1 = hash
		String[] parts = saltAndHash.split("\\$");

		if (parts.length != 2)
		{
			return false;
		}

		String testHash = hash(plainText, Base64.decodeBase64(parts[0]));
		return testHash.equals(parts[1]);
	}

	// Hash using PBKDF2 (with SHA256) from Sun
	private static String hash(String plainText, byte[] salt) throws ConnexienceException
	{
		try
		{
			if (plainText == null || plainText.length() == 0)
			{
				throw new IllegalArgumentException("Empty plaintext is not supported.");
			}

			// would prefer 256 but Java 6 only supports SHA1
			final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			final PBEKeySpec pbeKeySpec = new PBEKeySpec(plainText.toCharArray(), salt, ITERATIONS, DESIRED_KEY_LENGTH);

			final SecretKey key = secretKeyFactory.generateSecret(pbeKeySpec);

			return Base64.encodeBase64String(key.getEncoded());
		}
		catch (InvalidKeySpecException e)
		{
			throw new ConnexienceException("Problem while generating secure hash.", e);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new ConnexienceException("Problem while generating secure hash.", e);
		}
	}
}
