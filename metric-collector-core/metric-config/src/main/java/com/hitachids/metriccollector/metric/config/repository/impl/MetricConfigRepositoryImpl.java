package com.hitachids.metriccollector.metric.config.repository.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.hitachids.metriccollector.db.repository.impl.CrudRepositoryImpl;
import com.hitachids.metriccollector.metric.config.entity.MetricConfigEntity;
import com.hitachids.metriccollector.metric.config.mapper.MetricConfigMapper;
import com.hitachids.metriccollector.metric.config.mapper.impl.MetricConfigMapperImpl;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;
import com.hitachids.metriccollector.metric.config.repository.MetricConfigRepository;

public class MetricConfigRepositoryImpl
		extends CrudRepositoryImpl<MetricConfigEntity, Integer>
		implements MetricConfigRepository {

	private MetricConfigMapper metricConfigMapper;

	public MetricConfigRepositoryImpl() {
		this.metricConfigMapper = new MetricConfigMapperImpl();
	}

	@Override
	public List<MetricConfigEntity> fetchMetricConfigs(int storageId) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				SELECT
					mc.id,
					mc.metric_type,
					mc.interval,
					mc.granularity,
					mc.is_enabled,
					mc.storage_id,
					mc.created_at,
					mc.created_by,
					mc.updated_at,
					mc.updated_by
				FROM
					metric_config mc
				ORDER BY
					mc.metric_type DESC
				""");

		return fetchEntities(
				sqlBuilder.toString(),
				rs -> metricConfigMapper.toMetricConfigEntities(rs));
	}

	@Override
	public MetricConfigEntity fetchMetricConfig(int storageId, int id) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				SELECT
					mc.id,
					mc.metric_type,
					mc.interval,
					mc.granularity,
					mc.is_enabled,
					mc.storage_id,
					mc.created_at,
					mc.created_by,
					mc.updated_at,
					mc.updated_by
				FROM
					metric_config mc
				WHERE
					mc.storage_id = %d
					AND mc.id = %d
				""");

		return fetchEntity(
				String.format(sqlBuilder.toString(), storageId, id),
				rs -> metricConfigMapper.toMetricConfigEntity(rs));
	}

	@Override
	public MetricConfigEntity fetchMetricConfigByParams(int storageId, Map<String, Object> params) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				SELECT
				    mc.id,
				    mc.metric_type,
				    mc.interval,
				    mc.granularity,
				    mc.is_enabled,
					mc.storage_id,
				    mc.created_at,
				    mc.created_by,
				    mc.updated_at,
				    mc.updated_by
				FROM
				    metric_config mc
				WHERE
					mc.storage_id = %d
				""");

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String column = entry.getKey();
			Object value = entry.getValue();

			if (value != null) {
				sqlBuilder.append(" AND mc.").append(column);

				if (value instanceof String) {
					sqlBuilder.append(" = '").append(value).append("'");
				} else if (value instanceof Number) {
					sqlBuilder.append(" = ").append(value);
				}
			}
		}

		return fetchEntity(
				String.format(sqlBuilder.toString(), storageId),
				rs -> metricConfigMapper.toMetricConfigEntity(rs));
	}

	@Override
	public MetricConfigEntity insertMetricConfig(MetricConfigModel metricConfigModel) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				INSERT INTO
					metric_config (
						metric_type,
						interval,
						granularity,
						is_enabled,
						storage_id,
						created_at,
						created_by
					)
				VALUES (
					?, ?, ?, ?, ?, ?, ?
				)
				""");

		return insertEntity(
				sqlBuilder.toString(),
				stmt -> {
					int index = 1;

					stmt.setString(index++, metricConfigModel.getMetricType());
					stmt.setInt(index++, metricConfigModel.getInterval());
					stmt.setString(index++, metricConfigModel.getGranularity());
					stmt.setInt(index++, metricConfigModel.isEnabled() ? 1 : 0);
					stmt.setInt(index++, metricConfigModel.getStorageId());
					stmt.setTimestamp(index++, Timestamp.valueOf(metricConfigModel.getCreatedAt()));
					stmt.setString(index++, metricConfigModel.getCreatedBy());
				},
				rs -> fetchMetricConfig(metricConfigModel.getStorageId(), rs.getInt(1)));
	}

	@Override
	public int updateMetricConfig(int id, MetricConfigModel metricConfigModel) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				UPDATE
					metric_config
				SET
					metric_type = ?,
					interval = ?,
					granularity = ?,
					is_enabled = ?,
					storage_id = ?,
					updated_at = ?,
					updated_by = ?
				WHERE
					id = %d
				""");

		return updateEntity(
				String.format(sqlBuilder.toString(), id),
				stmt -> {
					int index = 1;

					stmt.setString(index++, metricConfigModel.getMetricType());
					stmt.setInt(index++, metricConfigModel.getInterval());
					stmt.setString(index++, metricConfigModel.getGranularity());
					stmt.setInt(index++, metricConfigModel.isEnabled() ? 1 : 0);
					stmt.setInt(index++, metricConfigModel.getStorageId());
					stmt.setTimestamp(index++, Timestamp.valueOf(metricConfigModel.getUpdatedAt()));
					stmt.setString(index++, metricConfigModel.getUpdatedBy());
				});
	}

}