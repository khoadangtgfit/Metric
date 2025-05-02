package com.hitachids.metriccollector.metric.config.model;

import com.hitachids.metriccollector.common.model.base.BaseModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MetricConfigModel extends BaseModel<Integer> {

	private String metricType;

	private Integer interval;

	private String granularity;

	private boolean isEnabled;

	private Integer storageId;

}
