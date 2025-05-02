package com.hitachids.metriccollector.storage.entity;

import com.hitachids.metriccollector.common.entity.base.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StorageEntity extends BaseEntity<Integer> {

	private String storageId;

	private String ipv4ServiceIp;

	private String organizationId;

	private String userId;

	private String encryptedPassword;

}
