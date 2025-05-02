package com.hitachids.metriccollector.auth.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenRequest implements Serializable {

	private long aliveTime;

	private long authenticationTimeout;

}