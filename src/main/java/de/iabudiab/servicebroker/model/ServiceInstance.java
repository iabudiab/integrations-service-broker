package de.iabudiab.servicebroker.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.instance.OperationState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document
public class ServiceInstance {

	@Id
	private String serviceInstanceId;

	private String serviceDefinitionId;

	private String planId;

	@Builder.Default
	private Map<String, Object> parameters = new HashMap<>();

	private Operation operation;

	private OperationState state;

}
