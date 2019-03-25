package de.iabudiab.servicebroker.providers;

import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;

import de.iabudiab.servicebroker.model.ServiceInstance;

public interface ServiceInstanceProvider {

	ServiceDefinition getServiceDefinition();

	CreateServiceInstanceResponse createServiceInstance(ServiceInstance serviceInstance);

	DeleteServiceInstanceResponse deleteServiceInstance(ServiceInstance serviceInstance);

	GetLastServiceOperationResponse getLastOperation(ServiceInstance serviceInstance);
}
