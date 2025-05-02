package com.hitachids.metriccollector.metric.config.test.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import com.hitachids.metriccollector.metric.config.entity.MetricConfigEntity;
import com.hitachids.metriccollector.metric.config.mapper.MetricConfigMapper;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;
import com.hitachids.metriccollector.metric.config.repository.impl.MetricConfigRepositoryImpl;
import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.mapper.impl.StorageMapperImpl;
import com.hitachids.metriccollector.storage.model.StorageModel;
import com.hitachids.metriccollector.storage.repository.impl.StorageRepositoryImpl;

@ExtendWith(MockitoExtension.class)
class MetricConfigRepositoryTest extends DatabaseManagerTest {

	@Mock
	private StorageMapperImpl storageMapperMock;

	@Mock
	private MetricConfigMapper metricConfigMapperMock;

	@InjectMocks
	private StorageRepositoryImpl storageRepositoryMock;

	@InjectMocks
	private MetricConfigRepositoryImpl metricConfigRepositoryMock;

	private MetricConfigModel metricConfigModel;

	private MetricConfigEntity metricConfigEntity;

	private Map<String, Object> metricConfigParams;

	private StorageModel storageModel;

	private StorageEntity storageEntity;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		LocalDateTime localDateTime = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime();

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		metricConfigModel = new MetricConfigModel();

		metricConfigModel.setId(1);
		metricConfigModel.setMetricType("metricType");
		metricConfigModel.setInterval(0);
		metricConfigModel.setGranularity("granularity");
		metricConfigModel.setEnabled(true);
		metricConfigModel.setStorageId(1);
		metricConfigModel.setCreatedAt(localDateTime);
		metricConfigModel.setCreatedBy("Admin");
		metricConfigModel.setUpdatedAt(localDateTime);
		metricConfigModel.setUpdatedBy("Admin");

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

		metricConfigParams = new HashMap<>();

		metricConfigParams.put("id", 1);
		metricConfigParams.put("metric_type", "metricType");
		metricConfigParams.put("interval", 0);
		metricConfigParams.put("granularity", "granularity");
		metricConfigParams.put("is_enabled", 1);
		metricConfigParams.put("storage_id", 1);
		metricConfigParams.put("created_by", "Admin");
		metricConfigParams.put("updated_by", null);

		storageModel = new StorageModel();

		storageModel.setId(1);
		storageModel.setStorageId("storageId");
		storageModel.setIpv4ServiceIp("ipv4ServiceIp");
		storageModel.setOrganizationId("organizationId");
		storageModel.setUsername("username");
		storageModel.setEncryptedPassword("encryptedPassword");
		storageModel.setCreatedAt(localDateTime);
		storageModel.setCreatedBy("Admin");

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
	}

	@Test
	void testFetchMetricConfigs_thenSuccess() throws Exception {
		int storageId = 1;

		List<MetricConfigEntity> metricConfigEntities = Arrays.asList(metricConfigEntity);

		when(
				metricConfigMapperMock.toMetricConfigEntities(any(ResultSet.class)))
				.thenReturn(metricConfigEntities);

		List<MetricConfigEntity> entities = metricConfigRepositoryMock.fetchMetricConfigs(storageId);

		assertEquals(1, entities.size());
	}

	@Test
	void testFetchMetricConfig_thenSuccess() throws Exception {
		int storageId = 1, metricConfigId = 1;

		when(
				metricConfigMapperMock.toMetricConfigEntity(any(ResultSet.class)))
				.thenReturn(metricConfigEntity);

		MetricConfigEntity entity = metricConfigRepositoryMock.fetchMetricConfig(storageId, metricConfigId);

		assertNotNull(entity);
	}

	@Test
	void testFetchMetricConfigByParams_thenSuccess() throws Exception {
		int storageId = 1;

		when(
				metricConfigMapperMock.toMetricConfigEntity(any(ResultSet.class)))
				.thenReturn(metricConfigEntity);

		MetricConfigEntity entity = metricConfigRepositoryMock.fetchMetricConfigByParams(storageId, metricConfigParams);

		assertNotNull(entity);
	}

	@Test
	void testInsertMetricConfig_whenIsEnabled_thenSuccess() throws Exception {
		storageModel.setId(null);
		storageModel.setStorageId(UUID.randomUUID().toString());

		when(
				storageMapperMock.toStorageEntity(any(ResultSet.class)))
				.thenReturn(storageEntity);

		StorageEntity sEntity = storageRepositoryMock.insertStorage(storageModel);

		metricConfigModel.setId(null);
		metricConfigModel.setMetricType(UUID.randomUUID().toString());
		metricConfigModel.setEnabled(true);
		metricConfigModel.setStorageId(sEntity.getId());

		when(
				metricConfigMapperMock.toMetricConfigEntity(any(ResultSet.class)))
				.thenReturn(metricConfigEntity);

		MetricConfigEntity entity = metricConfigRepositoryMock.insertMetricConfig(metricConfigModel);

		assertNotNull(entity);
	}

	@Test
	void testInsertMetricConfig_whenIsNotEnabled_thenSuccess() throws Exception {
		storageModel.setId(null);
		storageModel.setStorageId(UUID.randomUUID().toString());

		when(
				storageMapperMock.toStorageEntity(any(ResultSet.class)))
				.thenReturn(storageEntity);

		StorageEntity sEntity = storageRepositoryMock.insertStorage(storageModel);

		metricConfigModel.setId(null);
		metricConfigModel.setMetricType(UUID.randomUUID().toString());
		metricConfigModel.setEnabled(false);
		metricConfigModel.setStorageId(sEntity.getId());

		when(
				metricConfigMapperMock.toMetricConfigEntity(any(ResultSet.class)))
				.thenReturn(metricConfigEntity);

		MetricConfigEntity entity = metricConfigRepositoryMock.insertMetricConfig(metricConfigModel);

		assertNotNull(entity);
	}

	@Test
	void testUpdateMetricConfig_whenIsEnabled_thenSuccess() throws Exception {
		int metricConfigId = 1;

		metricConfigModel.setEnabled(true);

		metricConfigRepositoryMock.updateMetricConfig(metricConfigId, metricConfigModel);
	}

	@Test
	void testUpdateMetricConfig_whenIsNotEnabled_thenSuccess() throws Exception {
		int metricConfigId = 1;

		metricConfigModel.setEnabled(false);

		metricConfigRepositoryMock.updateMetricConfig(metricConfigId, metricConfigModel);
	}

}
