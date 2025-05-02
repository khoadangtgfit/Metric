package com.hitachids.metriccollector.storage.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hitachids.metriccollector.auth.security.encryption.CredentialEncryptor;
import com.hitachids.metriccollector.db.test.DatabaseManagerTest;
import com.hitachids.metriccollector.storage.dto.StorageDTO;
import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.model.StorageModel;
import com.hitachids.metriccollector.storage.repository.impl.StorageRepositoryImpl;
import com.hitachids.metriccollector.storage.service.impl.StorageServiceImpl;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest extends DatabaseManagerTest {

	@Mock
	private CredentialEncryptor credentialEncryptor;

	@Mock
	private StorageRepositoryImpl storageRepositoryMock;

	@InjectMocks
	private StorageServiceImpl storageServiceMock;

	private StorageDTO storageDTO;

	private StorageEntity storageEntity;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		storageDTO = new StorageDTO();

		storageDTO.setStorageId("storageId");
		storageDTO.setIpv4ServiceIp("ipv4ServiceIp");
		storageDTO.setOrganizationId("organizationId");
		storageDTO.setUsername("username");
		storageDTO.setPassword("password");

		storageEntity = new StorageEntity();

		storageEntity.setId(1);
		storageEntity.setStorageId("storageId");
		storageEntity.setIpv4ServiceIp("ipv4ServiceIp");
		storageEntity.setOrganizationId("organizationId");
		storageEntity.setUsername("username");
		storageEntity.setEncryptedPassword("password");
		storageEntity.setCreatedAt(timestamp);
		storageEntity.setCreatedBy("Admin");
		storageEntity.setUpdatedAt(timestamp);
		storageEntity.setUpdatedBy("Admin");
	}

	@Test
	void testGetStorage_thenSuccess() throws Exception {
		int storageId = 1;

		when(storageRepositoryMock.fetchStorage(anyInt())).thenReturn(storageEntity);

		Optional<StorageModel> optionalStorage = storageServiceMock.getStorage(storageId);

		StorageModel storage = optionalStorage.orElse(null);

		assertNotNull(storage);
	}

	@Test
	void testGetStorage_thenThrowException() throws Exception {
		int id = 1;

		when(storageRepositoryMock.fetchStorage(anyInt())).thenThrow(new SQLException());

		assertThrows(RuntimeException.class, () -> storageServiceMock.getStorage(id));
	}

	@Test
	void testGetStorageByStorageId_thenSuccess() throws Exception {
		String storageId = "storageId";

		when(storageRepositoryMock.fetchStorageByParams(anyMap())).thenReturn(storageEntity);

		Optional<StorageModel> optionalStorage = storageServiceMock.getStorageByStorageId(storageId);

		StorageModel storage = optionalStorage.orElse(null);

		assertNotNull(storage);
	}

	@Test
	void testGetStorageByStorageId_thenThrowException() throws Exception {
		String storageId = "storageId";

		when(storageRepositoryMock.fetchStorageByParams(anyMap())).thenThrow(new SQLException());

		assertThrows(RuntimeException.class, () -> storageServiceMock.getStorageByStorageId(storageId));
	}

	@Test
	void testGetStorageByParams_thenSuccess() throws Exception {
		Map<String, Object> params = new HashMap<>();

		when(storageRepositoryMock.fetchStorageByParams(anyMap())).thenReturn(storageEntity);

		Optional<StorageModel> optionalStorage = storageServiceMock.getStorageByParams(params);

		StorageModel storage = optionalStorage.orElse(null);

		assertNotNull(storage);
	}

	@Test
	void testGetStorageByParams_thenThrowException() throws Exception {
		Map<String, Object> params = new HashMap<>();

		when(storageRepositoryMock.fetchStorageByParams(anyMap())).thenThrow(new SQLException());

		assertThrows(RuntimeException.class, () -> storageServiceMock.getStorageByParams(params));
	}

	@Test
	void testInsertStorage_thenSuccess() throws Exception {
		when(credentialEncryptor.encrypt(anyString())).thenReturn("encryptedPassword");

		when(
				storageRepositoryMock.insertStorage(any(StorageModel.class)))
				.thenReturn(storageEntity);

		StorageModel storage = storageServiceMock.insertStorage(storageDTO);

		assertEquals(storageDTO.getStorageId(), storage.getStorageId());
		assertEquals(storageDTO.getIpv4ServiceIp(), storage.getIpv4ServiceIp());
		assertEquals(storageDTO.getOrganizationId(), storage.getOrganizationId());
	}

	@Test
	void testInsertStorage_thenReturnNull() throws Exception {
		storageDTO.setStorageId(null);

		StorageModel storage = storageServiceMock.insertStorage(storageDTO);

		assertNull(storage);
	}

	@Test
	void testInsertStorage_thenThrowException() throws Exception {
		when(storageRepositoryMock.insertStorage(any(StorageModel.class))).thenThrow((new SQLException()));
		assertThrows(RuntimeException.class, () -> storageServiceMock.insertStorage(storageDTO));
	}

	@Test
	void testUpdateStorage_thenSuccess() throws Exception {
		int id = 1;

		when(
				storageRepositoryMock.updateStorage(anyInt(), any(StorageModel.class)))
				.thenReturn(1);

		boolean isUpdated = storageServiceMock.updateStorage(id, storageDTO);

		assertTrue(isUpdated);
	}

	@Test
	void testUpdateStorage_whenValidateFailed_thenReturnFalse() throws Exception {
		int id = 1;

		storageDTO.setStorageId(null);

		boolean isUpdated = storageServiceMock.updateStorage(id, storageDTO);

		assertFalse(isUpdated);
	}

	@Test
	void testUpdateStorage_whenValidateSuccess_thenReturnFalse() throws Exception {
		int id = 1;

		when(
				storageRepositoryMock.updateStorage(anyInt(), any(StorageModel.class)))
				.thenReturn(0);

		boolean isUpdated = storageServiceMock.updateStorage(id, storageDTO);

		assertFalse(isUpdated);
	}

	@Test
	void testUpdateStorage_thenThrowException() throws Exception {
		int id = 1;

		when(storageRepositoryMock.updateStorage(anyInt(), any(StorageModel.class))).thenThrow((new SQLException()));

		assertThrows(RuntimeException.class, () -> storageServiceMock.updateStorage(id, storageDTO));
	}

	@Test
	void testUpdateStoragePassword_thenSuccess() throws Exception {
		int id = 1;
		String password = "password";

		when(credentialEncryptor.encrypt(anyString())).thenReturn("encryptedPassword");

		when(
				storageRepositoryMock.updateStorageByParams(anyInt(), anyMap()))
				.thenReturn(1);

		boolean isUpdated = storageServiceMock.updateStoragePassword(id, password);

		assertTrue(isUpdated);
	}

	@Test
	void testUpdateStoragePassword_whenValidateFailed_thenReturnFalse() throws Exception {
		int id = 1;
		String password = null;

		boolean isUpdated = storageServiceMock.updateStoragePassword(id, password);

		assertFalse(isUpdated);
	}

	@Test
	void testUpdateStoragePassword_whenValidateSuccess_thenReturnFalse() throws Exception {
		int id = 1;
		String password = "password";

		when(credentialEncryptor.encrypt(anyString())).thenReturn("encryptedPassword");

		when(
				storageRepositoryMock.updateStorageByParams(anyInt(), anyMap()))
				.thenReturn(0);

		boolean isUpdated = storageServiceMock.updateStoragePassword(id, password);

		assertFalse(isUpdated);
	}

	@Test
	void testUpdateStoragePassword_thenThrowException() throws Exception {
		int id = 1;
		String password = "password";

		when(storageRepositoryMock.updateStorageByParams(anyInt(), anyMap())).thenThrow((new SQLException()));

		assertThrows(RuntimeException.class, () -> storageServiceMock.updateStoragePassword(id, password));
	}

	@Test
	void testValidateStorage_whenEmptyDto_thenReturnFalse() throws Exception {
		boolean result = invokeValidateStorageDTO(null);
		assertFalse(result);
	}

	@Test
	void testValidateStorage_whenDtoHasEmptyIpv4ServiceIp_thenReturnFalse() throws Exception {
		storageDTO.setIpv4ServiceIp(null);

		boolean result = invokeValidateStorageDTO(storageDTO);
		assertFalse(result);
	}

	@Test
	void testValidateStorage_whenDtoHasEmptyOrganizationId_thenReturnFalse() throws Exception {
		storageDTO.setOrganizationId(null);

		boolean result = invokeValidateStorageDTO(storageDTO);
		assertFalse(result);
	}

	@Test
	void testValidateStorage_whenDtoHasEmptyUsername_thenReturnFalse() throws Exception {
		storageDTO.setUsername(null);

		boolean result = invokeValidateStorageDTO(storageDTO);
		assertFalse(result);
	}

	@Test
	void testValidateStorage_whenDtoHasEmptyPassword_thenReturnFalse() throws Exception {
		storageDTO.setPassword(null);

		boolean result = invokeValidateStorageDTO(storageDTO);
		assertFalse(result);
	}

	private boolean invokeValidateStorageDTO(StorageDTO storageDTO) throws Exception {
		Class<?> classWithMethod = storageServiceMock.getClass();

		if (classWithMethod.getName().contains("$MockitoMock$")) {
			classWithMethod = classWithMethod.getSuperclass();
		}

		Method method = classWithMethod.getDeclaredMethod("validateStorage", StorageDTO.class);
		method.setAccessible(true);

		return (boolean) method.invoke(storageServiceMock, storageDTO);
	}

}
