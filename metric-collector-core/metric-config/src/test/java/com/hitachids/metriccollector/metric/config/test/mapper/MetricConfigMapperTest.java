package com.hitachids.metriccollector.metric.config.test.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hitachids.metriccollector.metric.config.entity.MetricConfigEntity;
import com.hitachids.metriccollector.metric.config.mapper.impl.MetricConfigMapperImpl;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;

@ExtendWith(MockitoExtension.class)
class MetricConfigMapperTest {

	@Mock
	private ResultSet resultSetMock;

	@InjectMocks
	private MetricConfigMapperImpl metricConfigMapperMock;

	private MetricConfigEntity metricConfigEntity;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		metricConfigEntity = new MetricConfigEntity();

		metricConfigEntity.setId(1);
		metricConfigEntity.setMetricType("metricType");
		metricConfigEntity.setInterval(0);
		metricConfigEntity.setGranularity("granularity");
		metricConfigEntity.setIsEnabled(0);
		metricConfigEntity.setStorageId(1);
		metricConfigEntity.setCreatedBy("Admin");
		metricConfigEntity.setUpdatedBy("Admin");
	}

	@Test
	void testToMetricConfigEntities_thenSuccess() throws Exception {
		when(resultSetMock.next()).thenReturn(true, false);

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		when(resultSetMock.getInt("id")).thenReturn(1);
		when(resultSetMock.getString("metric_type")).thenReturn("metricType");
		when(resultSetMock.getInt("interval")).thenReturn(0);
		when(resultSetMock.getString("granularity")).thenReturn("granularity");
		when(resultSetMock.getInt("is_enabled")).thenReturn(1);
		when(resultSetMock.getInt("storage_id")).thenReturn(1);
		when(resultSetMock.getTimestamp("created_at")).thenReturn(timestamp);
		when(resultSetMock.getString("created_by")).thenReturn("Admin");
		when(resultSetMock.getTimestamp("updated_at")).thenReturn(timestamp);
		when(resultSetMock.getString("updated_by")).thenReturn("Admin");

		List<MetricConfigEntity> entities = metricConfigMapperMock.toMetricConfigEntities(resultSetMock);

		assertNotNull(entities);
		assertEquals(1, entities.size());
	}

	@Test
	void testToMetricConfigEntity_thenSuccess() throws Exception {
		when(resultSetMock.next()).thenReturn(true, false);

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		when(resultSetMock.getInt("id")).thenReturn(1);
		when(resultSetMock.getString("metric_type")).thenReturn("metricType");
		when(resultSetMock.getInt("interval")).thenReturn(0);
		when(resultSetMock.getString("granularity")).thenReturn("granularity");
		when(resultSetMock.getInt("is_enabled")).thenReturn(1);
		when(resultSetMock.getInt("storage_id")).thenReturn(1);
		when(resultSetMock.getTimestamp("created_at")).thenReturn(timestamp);
		when(resultSetMock.getString("created_by")).thenReturn("Admin");
		when(resultSetMock.getTimestamp("updated_at")).thenReturn(timestamp);
		when(resultSetMock.getString("updated_by")).thenReturn("Admin");

		MetricConfigEntity entity = metricConfigMapperMock.toMetricConfigEntity(resultSetMock);

		assertNotNull(entity);
		assertEquals(1, entity.getId());
		assertEquals("metricType", entity.getMetricType());
		assertEquals(0, entity.getInterval());
		assertEquals("granularity", entity.getGranularity());
		assertEquals(1, entity.getIsEnabled());
		assertEquals(1, entity.getStorageId());
		assertEquals(timestamp, entity.getCreatedAt());
		assertEquals("Admin", entity.getCreatedBy());
		assertEquals(timestamp, entity.getUpdatedAt());
		assertEquals("Admin", entity.getUpdatedBy());
	}

	@Test
	void testToMetricConfigModel_whenIsEnabledAndValidDates_thenSuccess() throws Exception {
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		metricConfigEntity.setIsEnabled(1);
		metricConfigEntity.setCreatedAt(timestamp);
		metricConfigEntity.setUpdatedAt(timestamp);

		MetricConfigModel model = metricConfigMapperMock.toMetricConfigModel(metricConfigEntity);

		assertNotNull(model);
		assertEquals(1, model.getId());
		assertEquals("metricType", model.getMetricType());
		assertEquals(0, model.getInterval());
		assertEquals("granularity", model.getGranularity());
		assertEquals(true, model.isEnabled());
		assertEquals(1, model.getStorageId());
		assertEquals(timestamp, Timestamp.valueOf(model.getCreatedAt()));
		assertEquals("Admin", model.getCreatedBy());
		assertEquals(timestamp, Timestamp.valueOf(model.getUpdatedAt()));
		assertEquals("Admin", model.getUpdatedBy());
	}

	@Test
	void testToMetricConfigModel_whenIsNotEnabledAndInvalidDates_thenSuccess() throws Exception {
		Timestamp timestamp = null;

		metricConfigEntity.setIsEnabled(0);
		metricConfigEntity.setCreatedAt(timestamp);
		metricConfigEntity.setUpdatedAt(timestamp);

		MetricConfigModel model = metricConfigMapperMock.toMetricConfigModel(metricConfigEntity);

		assertNotNull(model);
		assertEquals(1, model.getId());
		assertEquals("metricType", model.getMetricType());
		assertEquals(0, model.getInterval());
		assertEquals("granularity", model.getGranularity());
		assertEquals(false, model.isEnabled());
		assertEquals(1, model.getStorageId());
		assertEquals(timestamp, model.getCreatedAt());
		assertEquals("Admin", model.getCreatedBy());
		assertEquals(timestamp, model.getUpdatedAt());
		assertEquals("Admin", model.getUpdatedBy());
	}

	@Test
	void testToMetricConfigModel_whenEmptyDto_thenReturnNull() throws Exception {
		MetricConfigModel model = metricConfigMapperMock.toMetricConfigModel(null);
		assertNull(model);
	}

}
