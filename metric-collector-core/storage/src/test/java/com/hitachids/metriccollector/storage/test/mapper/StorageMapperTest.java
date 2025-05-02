package com.hitachids.metriccollector.storage.test.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.mapper.impl.StorageMapperImpl;
import com.hitachids.metriccollector.storage.model.StorageModel;

@ExtendWith(MockitoExtension.class)
class StorageMapperTest {

	@Mock
	private ResultSet resultSetMock;

	@InjectMocks
	private StorageMapperImpl storageMapperMock;

	private StorageEntity storageEntity;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		storageEntity = new StorageEntity();

		storageEntity.setId(1);
		storageEntity.setStorageId("storageId");
		storageEntity.setIpv4ServiceIp("ipv4ServiceIp");
		storageEntity.setOrganizationId("organizationId");
		storageEntity.setUsername("username");
		storageEntity.setEncryptedPassword("encryptedPassword");
		storageEntity.setCreatedBy("Admin");
		storageEntity.setUpdatedBy("Admin");
	}

	@Test
	void testToStorageEntity_thenSuccess() throws Exception {
		when(resultSetMock.next()).thenReturn(true, false);

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		when(resultSetMock.getInt("id")).thenReturn(1);
		when(resultSetMock.getString("storage_id")).thenReturn("storageId");
		when(resultSetMock.getString("ipv4_service_ip")).thenReturn("ipv4ServiceIp");
		when(resultSetMock.getString("organization_id")).thenReturn("organizationId");
		when(resultSetMock.getString("credential_username")).thenReturn("username");
		when(resultSetMock.getString("credential_password")).thenReturn("encryptedPassword");
		when(resultSetMock.getTimestamp("created_at")).thenReturn(timestamp);
		when(resultSetMock.getString("created_by")).thenReturn("Admin");
		when(resultSetMock.getTimestamp("updated_at")).thenReturn(timestamp);
		when(resultSetMock.getString("updated_by")).thenReturn("Admin");

		StorageEntity entity = storageMapperMock.toStorageEntity(resultSetMock);

		assertNotNull(entity);
		assertEquals(1, entity.getId());
		assertEquals("storageId", entity.getStorageId());
		assertEquals("ipv4ServiceIp", entity.getIpv4ServiceIp());
		assertEquals("organizationId", entity.getOrganizationId());
		assertEquals("username", entity.getUsername());
		assertEquals("encryptedPassword", entity.getEncryptedPassword());
		assertEquals(timestamp, entity.getCreatedAt());
		assertEquals("Admin", entity.getCreatedBy());
		assertEquals(timestamp, entity.getUpdatedAt());
		assertEquals("Admin", entity.getUpdatedBy());
	}

	@Test
	void testToStorageModel_whenValidDates_thenSuccess() throws Exception {
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		storageEntity.setCreatedAt(timestamp);
		storageEntity.setUpdatedAt(timestamp);

		StorageModel model = storageMapperMock.toStorageModel(storageEntity);

		assertNotNull(model);
		assertEquals(1, model.getId());
		assertEquals("storageId", model.getStorageId());
		assertEquals("ipv4ServiceIp", model.getIpv4ServiceIp());
		assertEquals("organizationId", model.getOrganizationId());
		assertEquals("username", model.getUsername());
		assertEquals("encryptedPassword", model.getEncryptedPassword());
		assertEquals(timestamp, Timestamp.valueOf(model.getCreatedAt()));
		assertEquals("Admin", model.getCreatedBy());
		assertEquals(timestamp, Timestamp.valueOf(model.getUpdatedAt()));
		assertEquals("Admin", model.getUpdatedBy());
	}

	@Test
	void testToStorageModel_whenInvalidDates_thenSuccess() throws Exception {
		Timestamp timestamp = null;

		storageEntity.setCreatedAt(timestamp);
		storageEntity.setUpdatedAt(timestamp);

		StorageModel model = storageMapperMock.toStorageModel(storageEntity);

		assertNotNull(model);
		assertEquals(1, model.getId());
		assertEquals("storageId", model.getStorageId());
		assertEquals("ipv4ServiceIp", model.getIpv4ServiceIp());
		assertEquals("organizationId", model.getOrganizationId());
		assertEquals("username", model.getUsername());
		assertEquals("encryptedPassword", model.getEncryptedPassword());
		assertEquals(timestamp, model.getCreatedAt());
		assertEquals("Admin", model.getCreatedBy());
		assertEquals(timestamp, model.getUpdatedAt());
		assertEquals("Admin", model.getUpdatedBy());
	}

	@Test
	void testToStorageModel_whenEmptyDto_thenReturnNull() throws Exception {
		StorageModel model = storageMapperMock.toStorageModel(null);
		assertNull(model);
	}

}
