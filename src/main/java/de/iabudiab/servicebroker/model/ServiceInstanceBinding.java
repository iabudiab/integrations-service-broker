package de.iabudiab.servicebroker.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Document
public class ServiceInstanceBinding {

	@Id
	private String id;

	private String serviceInstanceId;

	@Builder.Default
	private Map<String, Object> parameters = new HashMap<>();
}
