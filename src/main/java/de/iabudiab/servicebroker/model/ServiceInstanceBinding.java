package de.iabudiab.servicebroker.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ServiceInstanceBinding {

	private String id;

	private String serviceInstanceId;

	private String appGuid;

	@Builder.Default
	private Map<String, Object> credentials = new HashMap<>();
}
