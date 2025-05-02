package com.hitachids.metriccollector.metric.config.service.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hitachids.metriccollector.metric.config.dto.MetricConfigDTO;
import com.hitachids.metriccollector.metric.config.entity.MetricConfigEntity;
import com.hitachids.metriccollector.metric.config.mapper.MetricConfigMapper;
import com.hitachids.metriccollector.metric.config.mapper.impl.MetricConfigMapperImpl;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;
import com.hitachids.metriccollector.metric.config.repository.MetricConfigRepository;
import com.hitachids.metriccollector.metric.config.repository.impl.MetricConfigRepositoryImpl;
import com.hitachids.metriccollector.metric.config.service.MetricConfigService;

public class MetricConfigServiceImpl implements MetricConfigService {
	private static final Log LOG = LogFactory.getLog(MetricConfigServiceImpl.class);

	private MetricConfigRepository metricConfigRepository;
	private MetricConfigMapper metricConfigMapper;

	public MetricConfigServiceImpl() {
		this.metricConfigRepository = new MetricConfigRepositoryImpl();
		this.metricConfigMapper = new MetricConfigMapperImpl();
	}

	@Override
	public List<MetricConfigModel> getMetricConfigs(int storageId) {
		List<MetricConfigModel> metricConfigs = new ArrayList<>();

		try {
			List<MetricConfigEntity> metricConfigEntities = metricConfigRepository.fetchMetricConfigs(storageId);

			metricConfigEntities
					.stream()
					.forEach(entity -> {
						MetricConfigModel metricConfig = metricConfigMapper.toMetricConfigModel(entity);
						metricConfigs.add(metricConfig);
					});
		} catch (Exception ex) {
			LOG.error(String.format("Failed to get metric configs: %d", storageId), ex);
			throw new RuntimeException(ex);
		}

		return metricConfigs;
	}

	@Override
	public Optional<MetricConfigModel> getMetricConfig(int storageId, int id) {
		MetricConfigModel metricConfig = null;

		try {
			MetricConfigEntity entity = metricConfigRepository.fetchMetricConfig(storageId, id);
			metricConfig = metricConfigMapper.toMetricConfigModel(entity);
		} catch (Exception ex) {
			LOG.error(String.format("Failed to get metric config: %d - %d", storageId, id), ex);
			throw new RuntimeException(ex);
		}

		return Optional.ofNullable(metricConfig);
	}

	@Override
	public Optional<MetricConfigModel> getMetricConfigByMetricType(int storageId, String metricType) {
		MetricConfigModel metricConfig = null;

		try {
			Map<String, Object> params = Map.of("metric_type", metricType);

			MetricConfigEntity entity = metricConfigRepository.fetchMetricConfigByParams(storageId, params);
			metricConfig = metricConfigMapper.toMetricConfigModel(entity);
		} catch (Exception ex) {
			LOG.error(String.format("Failed to get metric config by metric type: %d - %s", storageId, metricType), ex);
			throw new RuntimeException(ex);
		}

		return Optional.ofNullable(metricConfig);
	}

	@Override
	public Optional<MetricConfigModel> getMetricConfigByParams(int storageId, Map<String, Object> params) {
		MetricConfigModel metricConfig = null;

		try {
			MetricConfigEntity entity = metricConfigRepository.fetchMetricConfigByParams(storageId, params);
			metricConfig = metricConfigMapper.toMetricConfigModel(entity);
		} catch (Exception ex) {
			LOG.error(String.format("Failed to get metric config by params: %d - %s", storageId, params), ex);
			throw new RuntimeException(ex);
		}

		return Optional.ofNullable(metricConfig);
	}

	@Override
	public MetricConfigModel insertMetricConfig(MetricConfigDTO metricConfigDTO) {
		MetricConfigModel metricConfig = null;

		try {
			if (!validateMetricConfig(metricConfigDTO)) {
				return metricConfig;
			}

			MetricConfigModel model = new MetricConfigModel();

			model.setMetricType(metricConfigDTO.getMetricType());
			model.setInterval(metricConfigDTO.getInterval());
			model.setGranularity(metricConfigDTO.getGranularity());
			model.setEnabled(metricConfigDTO.isEnabled());
			model.setStorageId(metricConfigDTO.getStorageId());
			model.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());
			model.setCreatedBy("Admin");

			MetricConfigEntity entity = metricConfigRepository.insertMetricConfig(model);
			metricConfig = metricConfigMapper.toMetricConfigModel(entity);
		} catch (Exception ex) {
			LOG.error("Failed to insert metric config: ", ex);
		}

		return metricConfig;
	}

	@Override
	public boolean updateMetricConfig(int id, MetricConfigDTO metricConfigDTO) {
		boolean isMetricConfigUpdated = false;

		try {
			if (!validateMetricConfig(metricConfigDTO)) {
				return isMetricConfigUpdated;
			}

			MetricConfigModel model = new MetricConfigModel();

			model.setId(id);
			model.setMetricType(metricConfigDTO.getMetricType());
			model.setInterval(metricConfigDTO.getInterval());
			model.setGranularity(metricConfigDTO.getGranularity());
			model.setEnabled(metricConfigDTO.isEnabled());
			model.setStorageId(metricConfigDTO.getStorageId());
			model.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime());
			model.setUpdatedBy("Admin");

			int affectedRows = metricConfigRepository.updateMetricConfig(id, model);
			isMetricConfigUpdated = affectedRows > 0;
		} catch (Exception ex) {
			LOG.error(String.format("Failed to update metric config %d: ", id), ex);
		}

		return isMetricConfigUpdated;
	}

	private boolean validateMetricConfig(MetricConfigDTO metricConfigDTO) {
		return metricConfigDTO != null
				&& metricConfigDTO.getMetricType() != null
				&& metricConfigDTO.getInterval() != null;
	}

}
