package com.hitachids.metriccollector.storage.service;

import java.util.Map;
import java.util.Optional;

import com.hitachids.metriccollector.storage.dto.StorageDTO;
import com.hitachids.metriccollector.storage.model.StorageModel;

public interface StorageService {

	public Optional<StorageModel> getStorage(int id);

	public Optional<StorageModel> getStorageByStorageId(String storageId);

	public Optional<StorageModel> getStorageByParams(Map<String, Object> params);

	public StorageModel insertStorage(StorageDTO StorageDTO);

	public boolean updateStorage(int id, StorageDTO StorageDTO);

	public boolean updateStoragePassword(int id, String password);

}
