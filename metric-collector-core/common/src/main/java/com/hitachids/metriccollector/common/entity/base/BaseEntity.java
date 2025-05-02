package com.hitachids.metriccollector.common.entity.base;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseEntity<ID extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected ID id;

	protected Timestamp createdAt;

	protected String createdBy;

	protected Timestamp updatedAt;

	protected String updatedBy;

}
