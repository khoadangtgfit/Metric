package com.hitachids.metriccollector.storage.model;

import com.hitachids.metriccollector.common.model.base.BaseModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StorageModel extends BaseModel<Integer> {

	private String storageId;

	private String ipv4ServiceIp;

	private String organizationId;

	private String userId;

	private String encryptedPassword;

}
