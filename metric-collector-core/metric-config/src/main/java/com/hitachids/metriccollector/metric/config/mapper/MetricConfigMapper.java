package com.hitachids.metriccollector.metric.config.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hitachids.metriccollector.metric.config.entity.MetricConfigEntity;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;

public interface MetricConfigMapper {

	public List<MetricConfigEntity> toMetricConfigEntities(ResultSet rs) throws SQLException;

	public MetricConfigEntity toMetricConfigEntity(ResultSet rs) throws SQLException;

	public MetricConfigModel toMetricConfigModel(MetricConfigEntity entity);

}
