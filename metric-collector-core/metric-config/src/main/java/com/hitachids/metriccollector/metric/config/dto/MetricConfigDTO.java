package com.hitachids.metriccollector.metric.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricConfigDTO {

	@JsonProperty("metric_type")
	private String metricType;

	private Integer interval;

	private String granularity;

	@JsonProperty("is_enabled")
	private boolean isEnabled;

	@JsonProperty("storage_id")
	private Integer storageId;

}
