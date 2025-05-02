package com.hitachids.metriccollector.common.model.base;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseModel<ID extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected ID id;

	protected LocalDateTime createdAt;

	protected String createdBy;

	protected LocalDateTime updatedAt;

	protected String updatedBy;

}
