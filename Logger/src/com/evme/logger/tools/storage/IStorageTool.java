package com.evme.logger.tools.storage;

import com.evme.logger.tools.storage.StorageTool.StorageType;

/**
 * Top interface of available storage tools.<br>
 * <br>
 * <i>Reference:
 * http://developer.android.com/guide/topics/data/data-storage.html</i>
 * 
 * @author sromku
 * 
 */
public interface IStorageTool {
	/**
	 * Get the type of the storage
	 * 
	 * @return {@link StorageType}
	 */
	StorageType getStorageType();
}