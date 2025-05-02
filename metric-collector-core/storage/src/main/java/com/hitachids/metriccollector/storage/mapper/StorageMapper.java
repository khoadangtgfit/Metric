package com.hitachids.metriccollector.storage.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.model.StorageModel;

public interface StorageMapper {

	public StorageEntity toStorageEntity(ResultSet rs) throws SQLException;

	public StorageModel toStorageModel(StorageEntity entity);

}
