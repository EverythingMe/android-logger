package com.evme.logger.tools.storage;

import android.content.Context;

/**
 * Singleton class that supply all possible storage options.<br>
 * <br>
 * <b>Permissions:</b>
 * <ul>
 * <li>android.permission.WRITE_EXTERNAL_STORAGE</li>
 * <li>android.permission.READ_EXTERNAL_STORAGE</li>
 * </ul>
 * 
 * @author sromku
 */
public class StorageTool
{

	private static InternalStorageTool mInternalStorageTool = null;
	private static ExternalStorageTool mExternalStorageTool = null;

	private static StorageTool mStorageTool = null;
	private static StorageToolConfiguration mStorageToolConfiguration;

	private StorageTool()
	{
		// set default configuration
		mStorageToolConfiguration = new StorageToolConfiguration.Builder().build();

		mInternalStorageTool = new InternalStorageTool();
		mExternalStorageTool = new ExternalStorageTool();
	}

	private static StorageTool init()
	{
		if (mStorageTool == null)
		{
			mStorageTool = new StorageTool();
		}
		return mStorageTool;
	}

	/**
	 * The type of the storage tools. <br>
	 * Possible options:
	 * <ul>
	 * <li>{@link StorageType#INTERNAL}</li>
	 * <li>{@link StorageType#EXTERNAL}</li>
	 * <li>{@link StorageType#DATABASE}</li>
	 * <li>{@link StorageType#PREFERENCES}</li>
	 * </ul>
	 * 
	 * @author sromku
	 * 
	 */
	public enum StorageType
	{
		INTERNAL,
		EXTERNAL,
		DATABASE,
		PREFERENCES
	}

	/**
	 * Get internal storage tool. The files and folders will be persisted on device memory. The internal
	 * storage is good for saving <b>private and secure</b> data.<br>
	 * <br>
	 * <b>Important:
	 * <ul>
	 * <li>When the device is low on internal storage space, Android may delete these cache files to recover
	 * space.</li>
	 * <li>You should always maintain the cache files yourself and stay within a reasonable limit of space
	 * consumed, such as 1MB.</li>
	 * <li>When the user uninstalls your application, these files are removed.</li>
	 * </b>
	 * </ul>
	 * <i>http://developer.android.com/guide/topics/data/data-storage.html# filesInternal</i>
	 * 
	 * @return {@link InternalStorageTool}
	 * 
	 */
	public static InternalStorageTool getInternalStorageTool(Context context)
	{
		init();
		mInternalStorageTool.initActivity(context);
		return mInternalStorageTool;
	}

	/**
	 * Get external storage tool. <br>
	 * 
	 * @return {@link ExternalStorageTool}
	 */
	public static ExternalStorageTool getExternalStorageTool()
	{
		init();
		return mExternalStorageTool;
	}

	/**
	 * Check whereas the external storage is writable. <br>
	 * 
	 * @return <code>True</code> if external storage writable, otherwise return <code>False</code>
	 */
	public static boolean isExternalStorageWritable()
	{
		init();
		return mExternalStorageTool.isWritable();
	}

	public static StorageToolConfiguration getConfiguration()
	{
		return mStorageToolConfiguration;
	}

	/**
	 * Set and update the storage configuration
	 * 
	 * @param storageToolConfiguration
	 */
	public static void updateConfiguration(StorageToolConfiguration storageToolConfiguration)
	{
		if (mStorageTool == null)
		{
			throw new RuntimeException(
				"First instantiate the StorageTool and then you can update the configuration");
		}
		mStorageToolConfiguration = storageToolConfiguration;
	}

}
