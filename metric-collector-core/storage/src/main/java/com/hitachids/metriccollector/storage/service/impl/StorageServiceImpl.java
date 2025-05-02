package com.hitachids.metriccollector.storage.service.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hitachids.metriccollector.auth.security.encryption.CredentialEncryptor;
import com.hitachids.metriccollector.storage.dto.StorageDTO;
import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.mapper.StorageMapper;
import com.hitachids.metriccollector.storage.mapper.impl.StorageMapperImpl;
import com.hitachids.metriccollector.storage.model.StorageModel;
import com.hitachids.metriccollector.storage.repository.StorageRepository;
import com.hitachids.metriccollector.storage.repository.impl.StorageRepositoryImpl;
import com.hitachids.metriccollector.storage.service.StorageService;

public class StorageServiceImpl implements StorageService {
	private static final Log LOG = LogFactory.getLog(StorageServiceImpl.class);

	private StorageRepository storageRepository;
	private StorageMapper storageMapper;

	private CredentialEncryptor credentialEncryptor;

	public StorageServiceImpl() {
		this.storageRepository = new StorageRepositoryImpl();
		this.storageMapper = new StorageMapperImpl();

		this.credentialEncryptor = new CredentialEncryptor();
	}

	@Override
	public Optional<StorageModel> getStorage(int id) {
		StorageModel storage = null;

		try {
			StorageEntity entity = storageRepository.fetchStorage(id);
			storage = storageMapper.toStorageModel(entity);
		} catch (Exception ex) {
			LOG.error(String.format("Failed to get storage: %d", id), ex);
			throw new RuntimeException(ex);
		}

		return Optional.ofNullable(storage);
	}

	@Override
	public Optional<StorageModel> getStorageByStorageId(String storageId) {
		StorageModel storage = null;

		try {
			Map<String, Object> params = Map.of("storage_id", storageId);

			StorageEntity entity = storageRepository.fetchStorageByParams(params);

			storage = storageMapper.toStorageModel(entity);
		} catch (Exception ex) {
			LOG.error(String.format("Failed to get storage by storage id: %s", storageId), ex);
			throw new RuntimeException(ex);
		}

		return Optional.ofNullable(storage);
	}

	@Override
	public Optional<StorageModel> getStorageByParams(Map<String, Object> params) {
		StorageModel storage = null;

		try {
			StorageEntity entity = storageRepository.fetchStorageByParams(params);
			storage = storageMapper.toStorageModel(entity);
		} catch (Exception ex) {
			LOG.error(String.format("Failed to get storage by params: %s", params), ex);
			throw new RuntimeException(ex);
		}

		return Optional.ofNullable(storage);
	}

	@Override
	public StorageModel insertStorage(StorageDTO storageDTO) {
		StorageModel storage = null;

		try {
			if (!validateStorage(storageDTO)) {
				return storage;
			}

			StorageModel model = new StorageModel();

			model.setStorageId(storageDTO.getStorageId());
			model.setIpv4ServiceIp(storageDTO.getIpv4ServiceIp());
			model.setOrganizationId(storageDTO.getOrganizationId());
			model.setUserId(storageDTO.getUserId());
			model.setEncryptedPassword(credentialEncryptor.encrypt(storageDTO.getPassword()));
			model.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());
			model.setCreatedBy("Admin");

			StorageEntity entity = storageRepository.insertStorage(model);
			storage = storageMapper.toStorageModel(entity);
		} catch (Exception ex) {
			LOG.error("Failed to insert storage: ", ex);
			throw new RuntimeException(ex);
		}

		return storage;
	}

	@Override
	public boolean updateStorage(int id, StorageDTO storageDTO) {
		boolean isStorageUpdated = false;

		try {
			if (!validateStorage(storageDTO)) {
				return isStorageUpdated;
			}

			StorageModel model = new StorageModel();

			model.setId(id);
			model.setStorageId(storageDTO.getStorageId());
			model.setIpv4ServiceIp(storageDTO.getIpv4ServiceIp());
			model.setOrganizationId(storageDTO.getOrganizationId());
			model.setUserId(storageDTO.getUserId());
			model.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());
			model.setUpdatedBy("Admin");

			int affectedRows = storageRepository.updateStorage(id, model);
			isStorageUpdated = affectedRows > 0;
		} catch (Exception ex) {
			LOG.error(String.format("Failed to update storage %d: ", id), ex);
			throw new RuntimeException(ex);
		}

		return isStorageUpdated;
	}

	@Override
	public boolean updateStoragePassword(int id, String password) {
		boolean isStorageUpdated = false;

		try {
			if (password == null) {
				return isStorageUpdated;
			}

			Map<String, Object> params = new HashMap<>();

			params.put("encryptedPassword", credentialEncryptor.encrypt(password));
			params.put("updatedAt", ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());
			params.put("updatedBy", "Admin");

			int affectedRows = storageRepository.updateStorageByParams(id, params);
			isStorageUpdated = affectedRows > 0;
		} catch (Exception ex) {
			LOG.error(String.format("Failed to update storage %d: ", id), ex);
			throw new RuntimeException(ex);
		}

		return isStorageUpdated;
	}

	private boolean validateStorage(StorageDTO storageDTO) {
		return storageDTO != null
				&& storageDTO.getStorageId() != null
				&& storageDTO.getIpv4ServiceIp() != null
				&& storageDTO.getOrganizationId() != null
				&& storageDTO.getUserId() != null
				&& storageDTO.getPassword() != null;
	}

}
