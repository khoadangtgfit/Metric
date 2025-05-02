package com.hitachids.metriccollector.metric.config.entity;

import com.hitachids.metriccollector.common.entity.base.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MetricConfigEntity extends BaseEntity<Integer> {

	private String metricType;

	private Integer interval;

	private String granularity;

	private Integer isEnabled;

	private Integer storageId;

}
