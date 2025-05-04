package com.hitachids.metriccollector.storage.mapper.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hitachids.metriccollector.storage.entity.StorageEntity;
import com.hitachids.metriccollector.storage.mapper.StorageMapper;
import com.hitachids.metriccollector.storage.model.StorageModel;

public class StorageMapperImpl implements StorageMapper {

	@Override
	public StorageEntity toStorageEntity(ResultSet rs) throws SQLException {
		StorageEntity entity = null;

		while (rs.next()) {
			entity = new StorageEntity();

			entity.setId(rs.getInt("id"));
			entity.setStorageId(rs.getString("storage_id"));
			entity.setIpv4ServiceIp(rs.getString("ipv4_service_ip"));
			entity.setOrganizationId(rs.getString("organization_id"));
			entity.setUserId(rs.getString("credential_userid"));
			entity.setEncryptedPassword(rs.getString("credential_password"));
			entity.setCreatedAt(rs.getTimestamp("created_at"));
			entity.setCreatedBy(rs.getString("created_by"));
			entity.setUpdatedAt(rs.getTimestamp("updated_at"));
			entity.setUpdatedBy(rs.getString("updated_by"));
		}

		return entity;
	}

	@Override
	public StorageModel toStorageModel(StorageEntity entity) {
		StorageModel model = null;

		if (entity != null) {
			model = new StorageModel();

			model.setId(entity.getId());
			model.setStorageId(entity.getStorageId());
			model.setIpv4ServiceIp(entity.getIpv4ServiceIp());
			model.setOrganizationId(entity.getOrganizationId());
			model.setUserId(entity.getUserId());
			model.setEncryptedPassword(entity.getEncryptedPassword());
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
