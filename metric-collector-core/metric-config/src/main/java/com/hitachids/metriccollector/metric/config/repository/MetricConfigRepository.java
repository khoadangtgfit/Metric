package com.hitachids.metriccollector.metric.config.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.hitachids.metriccollector.db.repository.CrudRepository;
import com.hitachids.metriccollector.metric.config.entity.MetricConfigEntity;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;

public interface MetricConfigRepository extends CrudRepository<MetricConfigEntity, Integer> {

	public List<MetricConfigEntity> fetchMetricConfigs(int storageId) throws SQLException;

	public MetricConfigEntity fetchMetricConfig(int storageId, int id) throws SQLException;

	public MetricConfigEntity fetchMetricConfigByParams(int storageId, Map<String, Object> params) throws SQLException;

	public MetricConfigEntity insertMetricConfig(MetricConfigModel metricConfigModel) throws SQLException;

	public int updateMetricConfig(int id, MetricConfigModel metricConfigModel) throws SQLException;

}