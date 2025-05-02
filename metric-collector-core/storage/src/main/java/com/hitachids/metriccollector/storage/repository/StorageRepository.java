package com.hitachids.metriccollector.storage.repository;

import java.sql.SQLException;
import java.util.Map;

import com.hitachids.metriccollector.db.repository.CrudRepository;
import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.model.StorageModel;

public interface StorageRepository extends CrudRepository<StorageEntity, Integer> {

	public StorageEntity fetchStorage(int id) throws SQLException;

	public StorageEntity fetchStorageByParams(Map<String, Object> params) throws SQLException;

	public StorageEntity insertStorage(StorageModel storageModel) throws SQLException;

	public int updateStorage(int id, StorageModel storageModel) throws SQLException;

	public int updateStorageByParams(int id, Map<String, Object> params) throws SQLException;

}
