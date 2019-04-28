package de.iabudiab.servicebroker.catalog;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

import de.iabudiab.servicebroker.annotation.ServiceInstanceProviderType;
import de.iabudiab.servicebroker.model.ServiceInstance;
import de.iabudiab.servicebroker.model.ServiceInstanceBinding;
import de.iabudiab.servicebroker.providers.ServiceInstanceProvider;
import de.iabudiab.servicebroker.repository.ServiceInstanceBindingRepository;
import de.iabudiab.servicebroker.repository.ServiceInstanceRepository;

@Service
public class IntegrationsCatalog implements ServiceInstanceService, ServiceInstanceBindingService, CatalogService {

	private final ServiceInstanceRepository instanceRepository;
	private final ServiceInstanceBindingRepository bindingRepository;
	private final Map<String, ServiceInstanceProvider> providers;

	public IntegrationsCatalog(ServiceInstanceRepository instanceRepository,
			ServiceInstanceBindingRepository bindingRepository,
			@ServiceInstanceProviderType List<ServiceInstanceProvider> providers) {
		this.instanceRepository = instanceRepository;
		this.bindingRepository = bindingRepository;
		this.providers = providers.stream() //
				.collect(toMap(it -> it.getServiceDefinition().getId(), identity()));
	}

	@Override
	public Catalog getCatalog() {
		List<ServiceDefinition> definitions = providers.values() //
				.stream() //
				.map(ServiceInstanceProvider::getServiceDefinition) //
				.collect(toList());

		return Catalog.builder() //
				.serviceDefinitions(definitions) //
				.build();
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return Optional.ofNullable(providers.get(serviceId)) //
				.map(ServiceInstanceProvider::getServiceDefinition) //
				.orElse(null);
	}

	@Override
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String serviceDefinitionId = request.getServiceDefinitionId();

		if (instanceRepository.existsById(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, serviceDefinitionId);
		}

		ServiceInstance instance = ServiceInstance.builder()//
				.serviceInstanceId(serviceInstanceId)//
				.serviceDefinitionId(serviceDefinitionId)//
				.planId(request.getPlanId())//
				.parameters(request.getParameters())//
				.build();

		CreateServiceInstanceResponse response = providers.get(instance.getServiceDefinitionId())
				.createServiceInstance(instance);
		instanceRepository.save(instance);

		return response;
	}

	@Override
	public GetServiceInstanceResponse getServiceInstance(GetServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		ServiceInstance instance = instanceRepository.findById(serviceInstanceId) //
				.orElseThrow(() -> new ServiceInstanceDoesNotExistException(serviceInstanceId));

		return GetServiceInstanceResponse.builder() //
				.serviceDefinitionId(instance.getServiceDefinitionId()) //
				.planId(instance.getPlanId()) //
				.parameters(Optional.ofNullable(instance.getParameters()).orElse(Map.of())) //
				.build();
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String serviceDefinitionId = request.getServiceDefinitionId();

		ServiceInstance serviceInstance = instanceRepository.findById(serviceInstanceId)//
				.orElseThrow(() -> new ServiceInstanceDoesNotExistException(serviceInstanceId));

		DeleteServiceInstanceResponse response = providers.get(serviceDefinitionId)
				.deleteServiceInstance(serviceInstance);
		instanceRepository.delete(serviceInstance);

		return response;
	}

	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request) {

		String bindingId = request.getBindingId();
		String serviceInstanceId = request.getServiceInstanceId();
		String serviceDefinitionId = request.getServiceDefinitionId();

		if (bindingRepository.existsById(bindingId)) {
			throw new ServiceInstanceBindingExistsException(serviceInstanceId, bindingId);
		}

		ServiceInstanceBinding instanceBinding = ServiceInstanceBinding.builder() //
				.id(bindingId) //
				.serviceInstanceId(serviceInstanceId) //
				.parameters(request.getParameters()) //
				.build();

		CreateServiceInstanceBindingResponse response = providers.get(serviceDefinitionId)
				.createServiceInstanceBinding(instanceBinding);

		bindingRepository.save(instanceBinding);

		return response;
	}

	@Override
	public DeleteServiceInstanceBindingResponse deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest request) {
		return null;
	}
}
