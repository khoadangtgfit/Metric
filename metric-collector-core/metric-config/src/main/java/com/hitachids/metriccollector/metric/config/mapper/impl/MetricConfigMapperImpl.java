package com.hitachids.metriccollector.metric.config.mapper.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hitachids.metriccollector.metric.config.entity.MetricConfigEntity;
import com.hitachids.metriccollector.metric.config.mapper.MetricConfigMapper;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;

public class MetricConfigMapperImpl implements MetricConfigMapper {

	@Override
	public List<MetricConfigEntity> toMetricConfigEntities(ResultSet rs) throws SQLException {
		List<MetricConfigEntity> entities = new ArrayList<>();

		while (rs.next()) {
			MetricConfigEntity entity = new MetricConfigEntity();

			entity.setId(rs.getInt("id"));
			entity.setMetricType(rs.getString("metric_type"));
			entity.setInterval(rs.getInt("interval"));
			entity.setGranularity(rs.getString("granularity"));
			entity.setIsEnabled(rs.getInt("is_enabled"));
			entity.setStorageId(rs.getInt("storage_id"));
			entity.setCreatedAt(rs.getTimestamp("created_at"));
			entity.setCreatedBy(rs.getString("created_by"));
			entity.setUpdatedAt(rs.getTimestamp("updated_at"));
			entity.setUpdatedBy(rs.getString("updated_by"));

			entities.add(entity);
		}

		return entities;
	}

	@Override
	public MetricConfigEntity toMetricConfigEntity(ResultSet rs) throws SQLException {
		MetricConfigEntity entity = null;

		while (rs.next()) {
			entity = new MetricConfigEntity();

			entity.setId(rs.getInt("id"));
			entity.setMetricType(rs.getString("metric_type"));
			entity.setInterval(rs.getInt("interval"));
			entity.setGranularity(rs.getString("granularity"));
			entity.setIsEnabled(rs.getInt("is_enabled"));
			entity.setStorageId(rs.getInt("storage_id"));
			entity.setCreatedAt(rs.getTimestamp("created_at"));
			entity.setCreatedBy(rs.getString("created_by"));
			entity.setUpdatedAt(rs.getTimestamp("updated_at"));
			entity.setUpdatedBy(rs.getString("updated_by"));
		}

		return entity;
	}

	@Override
	public MetricConfigModel toMetricConfigModel(MetricConfigEntity entity) {
		MetricConfigModel model = null;

		if (entity != null) {
			model = new MetricConfigModel();

			model.setId(entity.getId());
			model.setMetricType(entity.getMetricType());
			model.setInterval(entity.getInterval());
			model.setGranularity(entity.getGranularity());
			model.setEnabled(entity.getIsEnabled() == 1);
			model.setStorageId(entity.getStorageId());
			model.setCreatedBy(entity.getCreatedBy());
			model.setUpdatedBy(entity.getUpdatedBy());

			if (entity.getCreatedAt() != null) {
				model.setCreatedAt(entity.getCreatedAt().toLocalDateTime());
			}

			if (entity.getUpdatedAt() != null) {
				model.setUpdatedAt(entity.getUpdatedAt().toLocalDateTime());
			}
		}

		return model;
	}

}
