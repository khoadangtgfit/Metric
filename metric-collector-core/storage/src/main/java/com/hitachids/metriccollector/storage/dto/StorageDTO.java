package com.hitachids.metriccollector.storage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageDTO {

	@JsonProperty("storage_id")
	private String storageId;

	@JsonProperty("ipv4_service_ip")
	private String ipv4ServiceIp;

	@JsonProperty("organization_id")
	private String organizationId;

	private String userId;

	private String password;

}
