package de.iabudiab.servicebroker.catalog;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.stereotype.Service;

import de.iabudiab.servicebroker.annotation.ServiceInstanceProviderType;
import de.iabudiab.servicebroker.providers.ServiceInstanceProvider;

@Service
public class IntegrationsCatalogService implements CatalogService {

	private final Map<String, ServiceInstanceProvider> providers;

	public IntegrationsCatalogService(@ServiceInstanceProviderType List<ServiceInstanceProvider> providers) {
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
}
