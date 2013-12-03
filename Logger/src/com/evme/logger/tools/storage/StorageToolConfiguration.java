package com.evme.logger.tools.storage;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

/**
 * Each of the specific storage tools (like External storage tool) need its own
 * configurations. This configuration class is build and used in the storage
 * tools classes.<br>
 * <br>
 * 
 * <b>Examples:</b><br>
 * <br>
 * 
 * Default unsecured configuration:<br>
 * 
 * <pre>
 * {@code
 *  StorageToolConfiguration configuration = new StorageToolConfiguration.Builder()
 *  	.build()
 * }
 * </pre>
 * 
 * Secured configuration:
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	final int CHUNK_SIZE = 16 * 1024;
 * 	final String IVX = &quot;1234567890123456&quot;;
 * 	final String SECRET_KEY = &quot;secret1234567890&quot;;
 * 
 * 	StorageToolConfiguration configuration = new StorageToolConfiguration.Builder().setChuckSize(CHUNK_SIZE).doEncryptContent(IVX, SECRET_KEY).build();
 * }
 * </pre>
 * 
 * @author sromku
 * 
 */
public class StorageToolConfiguration {
	/**
	 * The best chunk size: <i>http://stackoverflow.com/a/237495/334522</i>
	 */
	private int mChunkSize;
	private boolean mIsEncrypted;
	private byte[] mIvParameter;
	private byte[] mSecretKey;

	private StorageToolConfiguration(Builder builder) {
		mChunkSize = builder._chunkSize;
		mIsEncrypted = builder._isEncrypted;
		mIvParameter = builder._ivParameter;
		mSecretKey = builder._secretKey;
	}

	/**
	 * Get chunk size. The chuck size is used while reading the file by chunks
	 * {@link FileInputStream#read(byte[], int, int)}.
	 * 
	 * @return The chunk size
	 */
	public int getChuckSize() {
		return mChunkSize;
	}

	/**
	 * Encrypt the file content.<br>
	 * 
	 * @see <a
	 *      href="https://en.wikipedia.org/wiki/Block_cipher_modes_of_operation">Block
	 *      cipher mode of operation</a>
	 */
	public boolean isEncrypted() {
		return mIsEncrypted;
	}

	/**
	 * Get secret key
	 * 
	 * @return
	 */
	public byte[] getSecretKey() {
		return mSecretKey;
	}

	/**
	 * Get iv parameter
	 * 
	 * @return
	 */
	public byte[] getIvParameter() {
		return mIvParameter;
	}

	/**
	 * Configuration Builder class. <br>
	 * Following Builder design pattern.
	 * 
	 * @author sromku
	 */
	public static class Builder {
		private int _chunkSize = 8 * 1024; // 8kbits = 1kbyte;
		private boolean _isEncrypted = false;
		private byte[] _ivParameter = null;
		private byte[] _secretKey = null;

		private static final String UTF_8 = "UTF-8";
		private static final String NAME_HASH_ALGORITHM = "SHA-1";

		public Builder() {
		}

		/**
		 * Build the configuration for storage tool.
		 * 
		 * @return
		 */
		public StorageToolConfiguration build() {
			return new StorageToolConfiguration(this);
		}

		/**
		 * Set chunk size. The chuck size is used while reading the file by
		 * chunks {@link FileInputStream#read(byte[], int, int)}. The preferable
		 * value is 1024xN bits. While N is power of 2 (like 1,2,4,8,16,...)<br>
		 * <br>
		 * 
		 * The default: <b>8 * 1024</b> = 8192 bits
		 * 
		 * @param chunkSize
		 *            The chunk size in bits
		 * @return The {@link Builder}
		 */
		public Builder setChuckSize(int chunkSize) {
			_chunkSize = chunkSize;
			return this;
		}

		/**
		 * Encrypt and descrypt the file content while writing and reading
		 * to/from disc.<br>
		 * 
		 * 
		 * @param ivx
		 *            This is not have to be secret. It used just for better
		 *            randomizing the cipher. You have to use the same IV
		 *            parameter within the same encrypted and written files.
		 *            Means, if you want to have the same content after
		 *            descryption then the same IV must be used.<br>
		 * <br>
		 * 
		 *            <b>Important: The length must be 16 long</b><br>
		 * 
		 *            <i>About this parameter from wiki:
		 *            https://en.wikipedia.org
		 *            /wiki/Block_cipher_modes_of_operation
		 *            #Initialization_vector_.28IV.29</i><br>
		 * <br>
		 * @param secretKey
		 *            Set the secret key for encryption of file content. <br>
		 * <br>
		 * 
		 *            <b>Important: The length must be 16 long</b> <br>
		 * 
		 *            <i>Uses SHA-1 to generate a hash from your key and trim
		 *            the result to 128 bit (16 bytes)</i><br>
		 * <br>
		 * @see <a
		 *      href="https://en.wikipedia.org/wiki/Block_cipher_modes_of_operation">Block
		 *      cipher mode of operation</a>
		 * 
		 */
		public Builder doEncryptContent(String ivx, String secretKey) {
			_isEncrypted = true;

			// Set IV parameter
			try {
				_ivParameter = ivx.getBytes(UTF_8);
			} catch (UnsupportedEncodingException e) {
				Log.e("StorageToolConfiguration", "UnsupportedEncodingException", e);
			}

			// Set secret key
			try {
				_secretKey = secretKey.getBytes(UTF_8);

				// One more security
				MessageDigest sha = MessageDigest.getInstance(NAME_HASH_ALGORITHM);
				_secretKey = sha.digest(_secretKey);

				// Better implementation would be using Arrays.copy
				byte[] shaKey = new byte[16];
				for (int i = 0; i < 16; i++) {
					shaKey[i] = _secretKey[i];
				}
				_secretKey = shaKey;
			} catch (UnsupportedEncodingException e) {
				Log.e("StorageToolConfiguration", "UnsupportedEncodingException", e);
			} catch (NoSuchAlgorithmException e) {
				Log.e("StorageToolConfiguration", "NoSuchAlgorithmException", e);
			}

			return this;
		}

	}

}