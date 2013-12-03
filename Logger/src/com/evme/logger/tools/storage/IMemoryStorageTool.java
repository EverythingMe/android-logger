package com.evme.logger.tools.storage;

import java.io.File;
import java.util.List;

import android.graphics.Bitmap;

/**
 * Interface of CRUD methods on the file system
 * 
 * @author sromku
 */
public interface IMemoryStorageTool extends IStorageTool
{
	/**
	 * Create directory with given path. <br>
	 * If the directory with given name <b>exists</b>, then {@link StorageToolException} will be thrown. <br>
	 * <br>
	 * 
	 * <b>For External Storage:</b> The name should be as following format:
	 * Directory_Name_0/Directory_Name_1/Directory_Name_2<br>
	 * <br>
	 * <b>For Internal Storage:</b> No separators are acceptable
	 * 
	 * @param name The name of the directory.
	 * @return <code>True</code> if directory was created, otherwise return <code>False</code>
	 * @throws StorageToolException
	 */
	boolean createDirectory(String name);

	/**
	 * Create directory with given path. <br>
	 * If the directory with given name exists and the <code>override</code> parameter is <code>True</code>
	 * then it will be removed and a new directory will be created instead. <br>
	 * <br>
	 * 
	 * <b>Note:</b> if <code>override=false</code>, then it do nothing and return true.
	 * 
	 * @param name The name of the directory.
	 * @param override Set <code>True</code> if you want to override the directory if such exists. The default
	 *            is <code>False</code>.<br>
	 *            Set <code>False</code> then it checks if directory already exist, if yes then do nothing and
	 *            return true, otherwise it creates a new directory
	 * @return <code>True</code> if directory was created, otherwise return <code>False</code>.
	 * 
	 * @throws StorageToolException
	 */
	boolean createDirectory(String name, boolean override);

	/**
	 * Delete the directory and all sub content.
	 * 
	 * @param name The name of the directory.
	 * @return <code>True</code> if the directory was deleted, otherwise return <code>False</code>
	 */
	boolean deleteDirectory(String name);

	/**
	 * Check if the directory is already exist.
	 * 
	 * @param name The name of the directory.
	 * @return <code>True</code> if exists, otherwise return <code>False</code>
	 */
	boolean isDirectoryExists(String name);

	/**
	 * Creating file with given name and with content in string format. <br>
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @param content The content which will filled the file
	 */
	boolean createFile(String directoryName, String fileName, String content);

	/**
	 * Creating file with given name and by using Storable format. <br>
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @param content The content which will filled the file
	 */
	boolean createFile(String directoryName, String fileName, Storable storable);
	
	/**
	 * Creating file with given name and by using Bitmap format. <br>
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @param content The content which will filled the file
	 */
	boolean createFile(String directoryName, String fileName, Bitmap bitmap);

	/**
	 * Creating the file with given name and with content in byte array format.<br>
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @param content The content which will filled the file
	 */
	boolean createFile(String directoryName, String fileName, byte[] content);

	/**
	 * Delete file
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @return
	 */
	boolean deleteFile(String directoryName, String fileName);

	/**
	 * Is file exists
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @return
	 */
	boolean isFileExist(String directoryName, String fileName);

	/**
	 * Read file from storage
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @return
	 */
	byte[] readFile(String directoryName, String fileName);

	/**
	 * Read string from external storage
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @return
	 */
	String readTextFile(String directoryName, String fileName);

	/**
	 * Append content to the existing file
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @param content
	 */
	void appendFile(String directoryName, String fileName, String content);

	/**
	 * Append content to the existing file
	 * 
	 * @param directoryName The directory name
	 * @param fileName The file name
	 * @param bytes
	 */
	void appendFile(String directoryName, String fileName, byte[] bytes);

	/**
	 * Get list of all nested files only (without directories) under the directories.
	 */
	List<File> getNestedFiles(String directoryName);

	/**
	 * Get {@link File} object by name of directory or file
	 * 
	 * @param name
	 * @return
	 */
	File getFile(String name);
}