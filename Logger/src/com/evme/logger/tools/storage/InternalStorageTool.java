package com.evme.logger.tools.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import com.evme.logger.tools.storage.StorageTool.StorageType;

public class InternalStorageTool extends MemoryStorageTool {
	private Context mContext;

	InternalStorageTool() {
		super();
	}

	/**
	 * Initialize activity before using methods for external storage file
	 * persistence
	 * 
	 * @param context
	 */
	void initActivity(Context context) {
		mContext = context;
	}

	@Override
	public StorageType getStorageType() {
		return StorageType.INTERNAL;
	}

	@Override
	public boolean createDirectory(String name) {
		File dir = mContext.getDir(name, Context.MODE_PRIVATE);
		if (dir.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * Creates file with content on the private internal storage dedicated for
	 * application space. <br>
	 * <br>
	 * <b>Note:</b> You don't need to create any directory before. You can just
	 * use this method without any preparations.<br>
	 * 
	 * <b>Important:</b> The file is deleted once the application is
	 * uninstalled.
	 * 
	 * @param name
	 *            The name of the file, including the extension
	 * @param content
	 *            The content of the file
	 * @return
	 */
	public boolean createFile(String name, String content) {
		try {
			byte[] bytes = content.getBytes();
			/*
			 * Check if needs to be encrypted. If yes, then encrypt it.
			 */
			// if (getConfiguration().isEncrypted()) {
			// bytes = encrypt(bytes, Cipher.ENCRYPT_MODE);
			// }

			FileOutputStream fos = mContext.openFileOutput(name, Context.MODE_PRIVATE);
			fos.write(bytes);
			fos.close();
			return true;
		} catch (IOException e) {
			throw new RuntimeException("Failed to create private file on internal storage", e);
		}
	}

	/**
	 * Read file from the internal storage dedicated for application space.<br>
	 * <br>
	 * <b>Important:</b>The file you read had to be saved before, by using:
	 * {@link InternalStorageTool#createFile(String, String)}
	 * 
	 * @param name
	 * @return
	 */
	public byte[] readFile(String name) {
		try {
			FileInputStream stream = mContext.openFileInput(name);
			byte[] out = readFile(stream);
			return out;
		} catch (IOException e) {
			throw new RuntimeException("Failed to create private file on internal storage", e);
		}
	}

	/**
	 * Build path of directory on the internal storage location. <br>
	 * <b>Note: </b>
	 * <ul>
	 * <li>For directory use regular name</li>
	 * </ul>
	 * <br>
	 * <b>Important:</b> <i>Isn't supported for nested directories. Could be
	 * supported by implementation of this solution:<br>
	 * <br>
	 * http://stackoverflow.com/questions
	 * /1889188/how-to-create-files-hierarchy-in
	 * -anroids-data-data-pkg-files-directory<br>
	 * or: <br>
	 * http://stackoverflow.com/questions/10618425/creating-directory-in-
	 * internal-storage-android<br>
	 * <br>
	 * </i>
	 * 
	 * @param name
	 *            The name of the directory
	 * @param storageLocation
	 * @return
	 */
	protected String buildPath(String directoryName) {
		String path = mContext.getDir(directoryName, Context.MODE_PRIVATE).getAbsolutePath();
		return path;
	}

	/**
	 * Build folder + file on the internal storage location. <br>
	 * <b>Note: </b> <li>For directory use regular name</li> <li>For file name
	 * use name with .extension like <i>abc.png</i></li><br>
	 * <br>
	 * 
	 * @param directoryName
	 *            The directory name
	 * @param fileName
	 *            The file name
	 * @return
	 */
	protected String buildPath(String directoryName, String fileName) {
		String path = mContext.getDir(directoryName, Context.MODE_PRIVATE).getAbsolutePath();
		path = path + File.separator + fileName;
		return path;
	}

}