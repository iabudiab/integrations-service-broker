package de.iabudiab.servicebroker.providers;

import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;

import de.iabudiab.servicebroker.model.ServiceInstance;
import de.iabudiab.servicebroker.model.ServiceInstanceBinding;

public interface ServiceInstanceProvider {

	ServiceDefinition getServiceDefinition();

	CreateServiceInstanceResponse createServiceInstance(ServiceInstance serviceInstance);

	DeleteServiceInstanceResponse deleteServiceInstance(ServiceInstance serviceInstance);

	UpdateServiceInstanceResponse updateServiceInstance(ServiceInstance serviceInstance);

	GetLastServiceOperationResponse getLastOperation(ServiceInstance serviceInstance);

	CreateServiceInstanceBindingResponse createServiceInstanceBinding(ServiceInstanceBinding serviceBinding);
}
