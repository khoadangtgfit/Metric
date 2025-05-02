package com.hitachids.metriccollector.metric.config.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hitachids.metriccollector.db.test.DatabaseManagerTest;
import com.hitachids.metriccollector.metric.config.dto.MetricConfigDTO;
import com.hitachids.metriccollector.metric.config.entity.MetricConfigEntity;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;
import com.hitachids.metriccollector.metric.config.repository.impl.MetricConfigRepositoryImpl;
import com.hitachids.metriccollector.metric.config.service.impl.MetricConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
class MetricConfigServiceTest extends DatabaseManagerTest {

	@Mock
	private MetricConfigRepositoryImpl metricConfigRepositoryMock;

	@InjectMocks
	private MetricConfigServiceImpl metricConfigServiceMock;

	private MetricConfigDTO metricConfigDTO;

	private MetricConfigEntity metricConfigEntity;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		metricConfigDTO = new MetricConfigDTO();

		metricConfigDTO.setMetricType("metricType");
		metricConfigDTO.setInterval(0);
		metricConfigDTO.setGranularity("granularity");
		metricConfigDTO.setEnabled(true);
		metricConfigDTO.setStorageId(1);

		metricConfigEntity = new MetricConfigEntity();

		metricConfigEntity.setId(1);
		metricConfigEntity.setMetricType("metricType");
		metricConfigEntity.setInterval(0);
		metricConfigEntity.setGranularity("granularity");
		metricConfigEntity.setIsEnabled(1);
		metricConfigEntity.setStorageId(1);
		metricConfigEntity.setCreatedAt(timestamp);
		metricConfigEntity.setCreatedBy("Admin");
		metricConfigEntity.setUpdatedAt(timestamp);
		metricConfigEntity.setUpdatedBy("Admin");
	}

	@Test
	void testGetMetricConfigs_thenSuccess() throws Exception {
		int storageId = 1;

		List<MetricConfigEntity> metricConfigEntities = Arrays.asList(metricConfigEntity);

		when(metricConfigRepositoryMock.fetchMetricConfigs(anyInt())).thenReturn(metricConfigEntities);

		List<MetricConfigModel> metricConfigs = metricConfigServiceMock.getMetricConfigs(storageId);

		assertEquals(1, metricConfigs.size());
	}

	@Test
	void testGetMetricConfigs_thenThrowException() throws Exception {
		int storageId = 1;

		when(metricConfigRepositoryMock.fetchMetricConfigs(anyInt())).thenThrow(new SQLException());

		assertThrows(RuntimeException.class, () -> metricConfigServiceMock.getMetricConfigs(storageId));

	}

	@Test
	void testGetMetricConfig_thenSuccess() throws Exception {
		int storageId = 1, metricConfigId = 1;

		when(metricConfigRepositoryMock.fetchMetricConfig(anyInt(), anyInt())).thenReturn(metricConfigEntity);

		Optional<MetricConfigModel> optionalMetricConfig = metricConfigServiceMock.getMetricConfig(
				storageId,
				metricConfigId);

		MetricConfigModel metricConfig = optionalMetricConfig.orElse(null);

		assertNotNull(metricConfig);
	}

	@Test
	void testGetMetricConfig_whenInvalidId_thenReturnNull() throws Exception {
		int storageId = 1, metricConfigId = -1;

		when(metricConfigRepositoryMock.fetchMetricConfig(anyInt(), anyInt())).thenReturn(null);

		Optional<MetricConfigModel> optionalMetricConfig = metricConfigServiceMock.getMetricConfig(
				storageId,
				metricConfigId);

		MetricConfigModel metricConfig = optionalMetricConfig.orElse(null);

		assertNull(metricConfig);
	}

	@Test
	void testGetMetricConfig_thenThrowException() throws Exception {
		int storageId = 1, metricConfigId = 1;

		when(metricConfigRepositoryMock.fetchMetricConfig(anyInt(), anyInt())).thenThrow(new SQLException());

		assertThrows(RuntimeException.class, () -> metricConfigServiceMock.getMetricConfig(storageId, metricConfigId));
	}

	@Test
	void testGetMetricConfigByMetricType_thenSuccess() throws Exception {
		int storageId = 1;
		String metricType = "metricType";

		when(metricConfigRepositoryMock.fetchMetricConfigByParams(anyInt(), anyMap())).thenReturn(metricConfigEntity);

		Optional<MetricConfigModel> optionalMetricConfig = metricConfigServiceMock.getMetricConfigByMetricType(
				storageId,
				metricType);

		MetricConfigModel metricConfig = optionalMetricConfig.orElse(null);

		assertNotNull(metricConfig);
	}

	@Test
	void testGetMetricConfigByMetricType_thenThrowException() throws Exception {
		int storageId = 1;
		String metricType = "metricType";

		when(metricConfigRepositoryMock.fetchMetricConfigByParams(anyInt(), anyMap())).thenThrow(new SQLException());

		assertThrows(
				RuntimeException.class,
				() -> metricConfigServiceMock.getMetricConfigByMetricType(storageId, metricType));

	}

	@Test
	void testGetMetricConfigByParams_thenSuccess() throws Exception {
		int storageId = 1;
		Map<String, Object> params = new HashMap<>();

		when(metricConfigRepositoryMock.fetchMetricConfigByParams(anyInt(), anyMap())).thenReturn(metricConfigEntity);

		Optional<MetricConfigModel> optionalMetricConfig = metricConfigServiceMock.getMetricConfigByParams(
				storageId,
				params);

		MetricConfigModel metricConfig = optionalMetricConfig.orElse(null);

		assertNotNull(metricConfig);
	}

	@Test
	void testGetMetricConfigByParams_whenInvalidMetricType_thenReturnNull() throws Exception {
		int storageId = 1;
		Map<String, Object> params = null;

		Optional<MetricConfigModel> optionalMetricConfig = metricConfigServiceMock.getMetricConfigByParams(
				storageId,
				params);

		MetricConfigModel metricConfig = optionalMetricConfig.orElse(null);

		assertNull(metricConfig);
	}

	@Test
	void testGetMetricConfigByParams_thenThrowException() throws Exception {
		int storageId = 1;
		Map<String, Object> params = new HashMap<>();

		when(metricConfigRepositoryMock.fetchMetricConfigByParams(anyInt(), anyMap())).thenThrow(new SQLException());

		assertThrows(
				RuntimeException.class,
				() -> metricConfigServiceMock.getMetricConfigByParams(storageId, params));
	}

	@Test
	void testInsertMetricConfig_thenSuccess() throws Exception {
		when(
				metricConfigRepositoryMock.insertMetricConfig(any(MetricConfigModel.class)))
				.thenReturn(metricConfigEntity);

		MetricConfigModel metricConfig = metricConfigServiceMock.insertMetricConfig(metricConfigDTO);

		assertEquals(metricConfigDTO.getMetricType(), metricConfig.getMetricType());
		assertEquals(metricConfigDTO.getInterval(), metricConfig.getInterval());
		assertEquals(metricConfigDTO.getGranularity(), metricConfig.getGranularity());
		assertEquals(metricConfigDTO.isEnabled(), metricConfig.isEnabled());
		assertEquals(metricConfigDTO.getStorageId(), metricConfig.getStorageId());
	}

	@Test
	void testInsertMetricConfig_whenValidateFailed_thenReturnNull() throws Exception {
		metricConfigDTO.setMetricType(null);

		MetricConfigModel metricConfig = metricConfigServiceMock.insertMetricConfig(metricConfigDTO);

		assertNull(metricConfig);
	}

	@Test
	void testInsertMetricConfig_thenThrowException() throws Exception {
		when(
				metricConfigRepositoryMock.insertMetricConfig(any(MetricConfigModel.class)))
				.thenThrow((new SQLException()));

		MetricConfigModel metricConfig = metricConfigServiceMock.insertMetricConfig(metricConfigDTO);

		assertNull(metricConfig);
	}

	@Test
	void testUpdateMetricConfig_thenSuccess() throws Exception {
		int metricConfigId = 1;

		when(
				metricConfigRepositoryMock.updateMetricConfig(anyInt(), any(MetricConfigModel.class)))
				.thenReturn(1);

		boolean isUpdated = metricConfigServiceMock.updateMetricConfig(metricConfigId, metricConfigDTO);

		assertTrue(isUpdated);
	}

	@Test
	void testUpdateMetricConfig_whenValidateFailed_thenReturnFalse() throws Exception {
		int metricConfigId = 1;

		metricConfigDTO.setMetricType(null);

		boolean isUpdated = metricConfigServiceMock.updateMetricConfig(metricConfigId, metricConfigDTO);

		assertFalse(isUpdated);
	}

	@Test
	void testUpdateMetricConfig_whenValidateSuccess_thenReturnFalse() throws Exception {
		int metricConfigId = 1;

		when(
				metricConfigRepositoryMock.updateMetricConfig(anyInt(), any(MetricConfigModel.class)))
				.thenReturn(0);

		boolean isUpdated = metricConfigServiceMock.updateMetricConfig(metricConfigId, metricConfigDTO);

		assertFalse(isUpdated);
	}

	@Test
	void testUpdateMetricConfig_thenThrowException() throws Exception {
		int metricConfigId = 1;

		when(
				metricConfigRepositoryMock.updateMetricConfig(anyInt(), any(MetricConfigModel.class)))
				.thenThrow((new SQLException()));

		boolean isUpdated = metricConfigServiceMock.updateMetricConfig(metricConfigId, metricConfigDTO);

		assertFalse(isUpdated);
	}

	@Test
	void testValidateMetricConfig_whenEmptyDto_thenReturnFalse() throws Exception {
		boolean result = invokeValidateMetricConfig(null);
		assertFalse(result);
	}

	@Test
	void testValidateMetricConfig_whenDtoHasEmptyInterval_thenReturnFalse() throws Exception {
		metricConfigDTO.setInterval(null);

		boolean result = invokeValidateMetricConfig(metricConfigDTO);
		assertFalse(result);
	}

	private boolean invokeValidateMetricConfig(MetricConfigDTO metricConfigDTO) throws Exception {
		Class<?> classWithMethod = metricConfigServiceMock.getClass();

		if (classWithMethod.getName().contains("$MockitoMock$")) {
			classWithMethod = classWithMethod.getSuperclass();
		}

		Method method = classWithMethod.getDeclaredMethod("validateMetricConfig", MetricConfigDTO.class);
		method.setAccessible(true);

		return (boolean) method.invoke(metricConfigServiceMock, metricConfigDTO);
	}

}
