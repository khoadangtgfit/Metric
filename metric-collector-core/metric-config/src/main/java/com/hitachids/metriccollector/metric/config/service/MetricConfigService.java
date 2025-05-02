package com.hitachids.metriccollector.metric.config.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hitachids.metriccollector.metric.config.dto.MetricConfigDTO;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;

public interface MetricConfigService {

	public List<MetricConfigModel> getMetricConfigs(int storageId);

	public Optional<MetricConfigModel> getMetricConfig(int storageId, int id);

	public Optional<MetricConfigModel> getMetricConfigByMetricType(int storageId, String metricType);

	public Optional<MetricConfigModel> getMetricConfigByParams(int storageId, Map<String, Object> params);

	public MetricConfigModel insertMetricConfig(MetricConfigDTO metricConfigDTO);

	public boolean updateMetricConfig(int id, MetricConfigDTO metricConfigDTO);

}
