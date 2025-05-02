package com.hitachids.metriccollector.storage.test.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hitachids.metriccollector.db.test.DatabaseManagerTest;
import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.mapper.impl.StorageMapperImpl;
import com.hitachids.metriccollector.storage.model.StorageModel;
import com.hitachids.metriccollector.storage.repository.impl.StorageRepositoryImpl;

@ExtendWith(MockitoExtension.class)
class StorageRepositoryTest extends DatabaseManagerTest {

	@Mock
	private StorageMapperImpl storageMapperMock;

	@InjectMocks
	private StorageRepositoryImpl storageRepositoryMock;

	private StorageModel storageModel;

	private StorageEntity storageEntity;

	private Map<String, Object> storageParams;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		LocalDateTime localDateTime = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime();

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		storageModel = new StorageModel();

		storageModel.setId(1);
		storageModel.setStorageId("storageId");
		storageModel.setIpv4ServiceIp("ipv4ServiceIp");
		storageModel.setOrganizationId("organizationId");
		storageModel.setUsername("username");
		storageModel.setEncryptedPassword("encryptedPassword");
		storageModel.setCreatedAt(localDateTime);
		storageModel.setCreatedBy("Admin");
		storageModel.setUpdatedAt(localDateTime);
		storageModel.setUpdatedBy("Admin");

		storageEntity = new StorageEntity();

		storageEntity.setId(1);
		storageEntity.setStorageId("storageId");
		storageEntity.setIpv4ServiceIp("ipv4ServiceIp");
		storageEntity.setOrganizationId("organizationId");
		storageEntity.setUsername("username");
		storageEntity.setEncryptedPassword("encryptedPassword");
		storageEntity.setCreatedAt(timestamp);
		storageEntity.setCreatedBy("Admin");
		storageEntity.setUpdatedAt(timestamp);
		storageEntity.setUpdatedBy("Admin");

		storageParams = new HashMap<>();

		storageParams.put("id", 1);
		storageParams.put("storage_id", "storageId");
		storageParams.put("ipv4_service_ip", "ipv4ServiceIp");
		storageParams.put("organization_id", "organizationId");
		storageParams.put("credential_username", "username");
		storageParams.put("credential_password", "password");
		storageParams.put("created_by", "Admin");
		storageParams.put("updated_by", null);
	}

	@Test
	void testFetchStorage_thenSuccess() throws Exception {
		int id = 1;

		when(
				storageMapperMock.toStorageEntity(any(ResultSet.class)))
				.thenReturn(storageEntity);

		StorageEntity entity = storageRepositoryMock.fetchStorage(id);

		assertNotNull(entity);
	}

	@Test
	void testFetchStorageByParams_thenSuccess() throws Exception {
		when(
				storageMapperMock.toStorageEntity(any(ResultSet.class)))
				.thenReturn(storageEntity);

		StorageEntity entity = storageRepositoryMock.fetchStorageByParams(storageParams);

		assertNotNull(entity);
	}

	@Test
	void testInsertStorage_whenIsEnabled_thenSuccess() throws Exception {
		storageModel.setId(null);
		storageModel.setStorageId(UUID.randomUUID().toString());

		when(
				storageMapperMock.toStorageEntity(any(ResultSet.class)))
				.thenReturn(storageEntity);

		StorageEntity entity = storageRepositoryMock.insertStorage(storageModel);

		assertNotNull(entity);
	}

	@Test
	void testUpdateStorage_thenSuccess() throws Exception {
		int id = 1;
		storageRepositoryMock.updateStorage(id, storageModel);
	}

	@Test
	void testUpdateStorageByParams_withValidData_thenSuccess() throws Exception {
		int id = 1;

		Map<String, Object> params = new HashMap<>();

		params.put("id", 1);
		params.put("storageId", "storageId");
		params.put("ipv4ServiceIp", "ipv4ServiceIp");
		params.put("organizationId", "organizationId");
		params.put("username", "username");
		params.put("encryptedPassword", "password");
		params.put("createdBy", "Admin");
		params.put("createdAt", ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());
		params.put("updatedBy", "Admin");
		params.put("updatedAt", ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());

		storageRepositoryMock.updateStorageByParams(id, params);
	}

	@Test
	void testUpdateStorageByParams_withInvalidData_thenSuccess() throws Exception {
		int id = 1;

		Map<String, Object> params = new HashMap<>();

		params.put("id", 1);
		params.put("storageId", "storageId");
		params.put("ipv4ServiceIp", "ipv4ServiceIp");
		params.put("organizationId", "organizationId");
		params.put("username", "username");
		params.put("encryptedPassword", "password");
		params.put("createdBy", null);
		params.put("createdAt", ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());

		storageRepositoryMock.updateStorageByParams(id, params);
	}

}
