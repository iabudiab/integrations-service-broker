package de.iabudiab.servicebroker.providers.keycloak;

import java.util.List;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.stereotype.Service;

import de.iabudiab.servicebroker.annotation.ServiceInstanceProviderType;
import de.iabudiab.servicebroker.model.ServiceInstance;
import de.iabudiab.servicebroker.model.ServiceInstanceBinding;
import de.iabudiab.servicebroker.providers.ServiceInstanceProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ServiceInstanceProviderType
public class KeycloakRealmInstanceProvider implements ServiceInstanceProvider {

	private final KeycloakAdministration administration;

	@Override
	public ServiceDefinition getServiceDefinition() {
		Plan defaultPlan = Plan.builder() //
				.id("fd9e8dad-e715-4ab5-950b-78b5230f93be") //
				.name("default")//
				.description("Realm") //
				.bindable(true) //
				.build();

		return ServiceDefinition.builder() //
				.id("03069b4a-e5da-427b-b524-e87529508111") //
				.name("keycloak-realm") //
				.description("Keycloak realm") //
				.tags("sso", "oidc") //
				.bindable(true) //
				.plans(List.of(defaultPlan)) //
				.build();
	}

	@Override
	public CreateServiceInstanceResponse createServiceInstance(ServiceInstance serviceInstance) {
		String realmName = serviceInstance.getServiceInstanceId();
		if (administration.hasRealm(realmName)) {
			throw new ServiceInstanceExistsException(realmName, serviceInstance.getServiceDefinitionId());
		}

		administration.createRealm(realmName, serviceInstance.getParameters());

		return CreateServiceInstanceResponse.builder()//
				.async(false)//
				.build();
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(ServiceInstance serviceInstance) {
		String realmName = serviceInstance.getServiceInstanceId();
		administration.deleteRealm(realmName);

		return DeleteServiceInstanceResponse.builder()//
				.async(false)//
				.build();
	}

	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(ServiceInstance serviceInstance) {
		return UpdateServiceInstanceResponse.builder() //
				.async(false) //
				.build();
	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(ServiceInstance serviceInstance) {
		throw new UnsupportedOperationException("This service provider is synchronous");
	}

	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(ServiceInstanceBinding serviceBinding) {
		// TODO Auto-generated method stub
		return null;
	}

}
