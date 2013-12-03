package com.evme.logger.tools.storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

/**
 * Common class for internal and external storage tools
 * 
 * @author sromku
 * 
 */
abstract class MemoryStorageTool implements IMemoryStorageTool {
	protected static final String UTF_8 = "UTF-8";

	MemoryStorageTool() {
	}

	protected StorageToolConfiguration getConfiguration() {
		return StorageTool.getConfiguration();
	}

	@Override
	public boolean createDirectory(String name) {
		String path = buildPath(name);

		// Check if the directory already exist
		if (isDirectoryExists(path)) {
			throw new RuntimeException("The direcory already exist. You can't override the existing one. Use createDirectory(String path, boolean override)");
		}

		File directory = new File(path);

		// Create a new directory
		boolean wasCreated = directory.mkdirs();

		// If directory is already exist then wasCreated=false
		if (!wasCreated) {
			throw new RuntimeException("Couldn't create new direcory");
		}

		return true;
	}

	@Override
	public boolean createDirectory(String name, boolean override) {
		// If override==false, then don't override
		if (!override) {
			if (isDirectoryExists(name)) {
				return true;
			} else {
				return createDirectory(name);
			}
		}

		// Check if directory exists. If yes, then delete all directory
		if (isDirectoryExists(name)) {
			deleteDirectory(name);
		}

		// Create new directory
		boolean wasCreated = createDirectory(name);
		// If directory is already exist then wasCreated=false
		if (!wasCreated) {
			throw new RuntimeException("Couldn't create new direcory");
		}

		return true;
	}

	@Override
	public boolean deleteDirectory(String name) {
		String path = buildPath(name);
		return deleteDirectoryImpl(path);
	}

	@Override
	public boolean isDirectoryExists(String name) {
		String path = buildPath(name);
		return new File(path).exists();
	}

	@Override
	public boolean createFile(String directoryName, String fileName, String content) {
		return createFile(directoryName, fileName, content.getBytes());
	}

	@Override
	public boolean createFile(String directoryName, String fileName, Storable storable) {
		return createFile(directoryName, fileName, storable.getBytes());
	}

	@Override
	public boolean createFile(String directoryName, String fileName, byte[] content) {
		String path = buildPath(directoryName, fileName);
		try {
			OutputStream stream = new FileOutputStream(new File(path));

			/*
			 * Check if needs to be encrypted. If yes, then encrypt it.
			 */
			// if (getConfiguration().isEncrypted()) {
			// content = encrypt(content, Cipher.ENCRYPT_MODE);
			// }

			stream.write(content);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create", e);
		}
		return true;
	}

	@Override
	public boolean createFile(String directoryName, String fileName, Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return createFile(directoryName, fileName, byteArray);
	}

	@Override
	public boolean deleteFile(String directoryName, String fileName) {
		String path = buildPath(directoryName, fileName);
		File file = new File(path);
		return file.delete();
	}

	@Override
	public boolean isFileExist(String directoryName, String fileName) {
		String path = buildPath(directoryName, fileName);
		return new File(path).exists();
	}

	@Override
	public byte[] readFile(String directoryName, String fileName) {
		String path = buildPath(directoryName, fileName);
		final FileInputStream stream;
		try {
			stream = new FileInputStream(new File(path));
			return readFile(stream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to read file to input sream", e);
		}
	}

	@Override
	public String readTextFile(String directoryName, String fileName) {
		byte[] bytes = readFile(directoryName, fileName);
		String content = new String(bytes);
		return content;
	}

	@Override
	public void appendFile(String directoryName, String fileName, String content) {
		appendFile(directoryName, fileName, content.getBytes());
	}

	@Override
	public void appendFile(String directoryName, String fileName, byte[] bytes) {
		if (!isFileExist(directoryName, fileName)) {
			throw new RuntimeException("Impossible to append content, because such file doesn't exist");
		}

		try {
			String path = buildPath(directoryName, fileName);
			FileOutputStream stream = new FileOutputStream(new File(path), true);
			stream.write(bytes);
			stream.write(System.getProperty("line.separator").getBytes());
			stream.flush();
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to append content to file", e);
		}
	}

	@Override
	public List<File> getNestedFiles(String directoryName) {
		String buildPath = buildPath(directoryName);
		File file = new File(buildPath);
		List<File> out = new ArrayList<File>();
		getDirectoryFilesImpl(file, out);
		return out;
	}

	@Override
	public File getFile(String name) {
		String path = buildPath(name);
		File file = new File(path);
		return file;
	}

	protected byte[] readFile(final FileInputStream stream) {
		class Reader extends Thread {
			byte[] array = null;
		}

		Reader reader = new Reader() {
			public void run() {
				LinkedList<ImmutablePair<byte[], Integer>> chunks = new LinkedList<ImmutablePair<byte[], Integer>>();

				// read the file and build chunks
				int size = 0;
				int globalSize = 0;
				do {
					try {
						int chunkSize = getConfiguration().getChuckSize();
						// read chunk
						byte[] buffer = new byte[chunkSize];
						size = stream.read(buffer, 0, chunkSize);
						if (size > 0) {
							globalSize += size;

							// add chunk to list
							chunks.add(new ImmutablePair<byte[], Integer>(buffer, size));
						}
					} catch (Exception e) {
						// Auxiliary.logging(e);
					}
				} while (size > 0);

				try {
					stream.close();
				} catch (Exception e) {
					// Auxiliary.logging(e);
				}

				array = new byte[globalSize];

				// append all chunks to one array
				int offset = 0;
				for (ImmutablePair<byte[], Integer> chunk : chunks) {
					// flush chunk to array
					System.arraycopy(chunk.element1, 0, array, offset, chunk.element2);
					offset += chunk.element2;
				}
			};
		};

		reader.start();
		try {
			reader.join();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed on reading file from storage while the locking Thread", e);
		}

		// if (getConfiguration().isEncrypted()) {
		// return encrypt(reader.array, Cipher.DECRYPT_MODE);
		// } else {
		return reader.array;
		// }
	}

	protected abstract String buildPath(String name);

	protected abstract String buildPath(String directoryName, String fileName);

	/**
	 * Encrypt or Descrypt the content. <br>
	 * 
	 * @param content
	 *            The content to encrypt or descrypt.
	 * @param encryptionMode
	 *            Use: {@link Cipher#ENCRYPT_MODE} or
	 *            {@link Cipher#DECRYPT_MODE}
	 * @return
	 */
	// protected synchronized byte[] encrypt(byte[] content, int encryptionMode)
	// {
	// final byte[] secretKey = getConfiguration().getSecretKey();
	// final byte[] ivx = getConfiguration().getIvParameter();
	// return SecurityTool.getInstance().encrypt(content, encryptionMode,
	// secretKey, ivx);
	// }

	/**
	 * Delete the directory and all sub content.
	 * 
	 * @param path
	 *            The absolute directory path. For example:
	 *            <i>mnt/sdcard/NewFolder/</i>.
	 * @return <code>True</code> if the directory was deleted, otherwise return
	 *         <code>False</code>
	 */
	private boolean deleteDirectoryImpl(String path) {
		File directory = new File(path);

		// If the directory exists then delete
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files == null) {
				return true;
			}
			// Run on all sub files and folders and delete them
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectoryImpl(files[i].getAbsolutePath());
				} else {
					files[i].delete();
				}
			}
		}
		return directory.delete();
	}

	/**
	 * Get all files under the directory
	 * 
	 * @param directory
	 * @param out
	 * @return
	 */
	private void getDirectoryFilesImpl(File directory, List<File> out) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files == null) {
				return;
			} else {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						getDirectoryFilesImpl(files[i], out);
					} else {
						out.add(files[i]);
					}
				}
			}
		}
	}

}
