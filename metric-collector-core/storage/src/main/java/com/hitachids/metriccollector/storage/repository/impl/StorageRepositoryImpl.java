package com.hitachids.metriccollector.storage.repository.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hitachids.metriccollector.db.repository.impl.CrudRepositoryImpl;
import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.mapper.StorageMapper;
import com.hitachids.metriccollector.storage.mapper.impl.StorageMapperImpl;
import com.hitachids.metriccollector.storage.model.StorageModel;
import com.hitachids.metriccollector.storage.repository.StorageRepository;

public class StorageRepositoryImpl
		extends CrudRepositoryImpl<StorageEntity, Integer>
		implements StorageRepository {

	private StorageMapper storageMapper;

	public StorageRepositoryImpl() {
		this.storageMapper = new StorageMapperImpl();
	}

	@Override
	public StorageEntity fetchStorage(int id) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				SELECT
					s.id,
					s.storage_id,
					s.ipv4_service_ip,
					s.organization_id,
					s.credential_username,
					s.credential_password,
					s.created_at,
					s.created_by,
					s.updated_at,
					s.updated_by
				FROM
					storage s
				WHERE
					s.id = %d
					""");

		return fetchEntity(
				String.format(sqlBuilder.toString(), id),
				rs -> storageMapper.toStorageEntity(rs));
	}

	@Override
	public StorageEntity fetchStorageByParams(Map<String, Object> params) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				SELECT
				    s.id,
					s.storage_id,
					s.ipv4_service_ip,
					s.organization_id,
					s.credential_username,
					s.credential_password,
					s.created_at,
					s.created_by,
					s.updated_at,
					s.updated_by
				FROM
					storage s
				WHERE
				 	1 = 1
				""");

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String column = entry.getKey();
			Object value = entry.getValue();

			if (value != null) {
				sqlBuilder.append(" AND s.").append(column);

				if (value instanceof String) {
					sqlBuilder.append(" = '").append(value).append("'");
				} else if (value instanceof Number) {
					sqlBuilder.append(" = ").append(value);
				}
			}
		}

		return fetchEntity(
				sqlBuilder.toString(),
				rs -> storageMapper.toStorageEntity(rs));
	}

	@Override
	public StorageEntity insertStorage(StorageModel storageModel) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				INSERT INTO
					storage (
						storage_id,
						ipv4_service_ip,
						organization_id,
						credential_username,
						credential_password,
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

					stmt.setString(index++, storageModel.getStorageId());
					stmt.setString(index++, storageModel.getIpv4ServiceIp());
					stmt.setString(index++, storageModel.getOrganizationId());
					stmt.setString(index++, storageModel.getUsername());
					stmt.setString(index++, storageModel.getEncryptedPassword());
					stmt.setTimestamp(index++, Timestamp.valueOf(storageModel.getCreatedAt()));
					stmt.setString(index++, storageModel.getCreatedBy());
				},
				rs -> fetchStorage(rs.getInt(1)));
	}

	@Override
	public int updateStorage(int id, StorageModel storageModel) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("""
				UPDATE
					storage
				SET
					storage_id = ?,
					ipv4_service_ip = ?,
					organization_id = ?,
					credential_username = ?,
					credential_password = ?,
					updated_at = ?,
					updated_by = ?
				WHERE
					id = %d
				""");

		return updateEntity(
				String.format(sqlBuilder.toString(), id),
				stmt -> {
					int index = 1;

					stmt.setString(index++, storageModel.getStorageId());
					stmt.setString(index++, storageModel.getIpv4ServiceIp());
					stmt.setString(index++, storageModel.getOrganizationId());
					stmt.setString(index++, storageModel.getUsername());
					stmt.setString(index++, storageModel.getEncryptedPassword());
					stmt.setTimestamp(index++, Timestamp.valueOf(storageModel.getUpdatedAt()));
					stmt.setString(index++, storageModel.getUpdatedBy());
				});
	}

	@Override
	public int updateStorageByParams(int id, Map<String, Object> params) throws SQLException {
		Map<String, String> fieldToColumnMap = Map.of(
				"storageId", "storage_id",
				"ipv4ServiceIp", "ipv4_service_ip",
				"organizationId", "organization_id",
				"username", "credential_username",
				"encryptedPassword", "credential_password",
				"updatedAt", "updated_at",
				"updatedBy", "updated_by");

		StringBuilder setClauseBuilder = new StringBuilder();

		List<Object> paramValues = new ArrayList<>();

		params.forEach((key, value) -> {
			if (value != null && fieldToColumnMap.containsKey(key)) {

				if (setClauseBuilder.length() > 0) {
					setClauseBuilder.append(", ");
				}

				setClauseBuilder.append(fieldToColumnMap.get(key)).append(" = ?");

				paramValues.add(value);
			}
		});

		if (!params.containsKey("updatedAt")) {
			if (setClauseBuilder.length() > 0) {
				setClauseBuilder.append(", ");
			}

			setClauseBuilder.append("updated_at = ?");

			paramValues.add(Timestamp.valueOf(LocalDateTime.now()));
		}

		StringBuilder sqlBuilder = new StringBuilder("UPDATE storage SET " + setClauseBuilder + " WHERE id = %d");

		return updateEntity(
				String.format(sqlBuilder.toString(), id),
				stmt -> {
					int index = 1;

					for (Object value : paramValues) {
						if (value instanceof String) {
							stmt.setString(index++, (String) value);
						} else if (value instanceof LocalDateTime) {
							stmt.setTimestamp(index++, Timestamp.valueOf((LocalDateTime) value));
						}
					}
				});
	}

}
