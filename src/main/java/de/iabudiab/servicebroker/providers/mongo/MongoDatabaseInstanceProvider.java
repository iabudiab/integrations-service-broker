package de.iabudiab.servicebroker.providers.mongo;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
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
public class MongoDatabaseInstanceProvider implements ServiceInstanceProvider {

	private final MongoAdministration administration;

	@Override
	public ServiceDefinition getServiceDefinition() {
		Plan defaultPlan = Plan.builder() //
				.id("4dc66694-2d84-4e64-b12f-0f77b9ea3950") //
				.name("default")//
				.description("Database") //
				.bindable(true) //
				.build();

		return ServiceDefinition.builder() //
				.id("0b67ec26-848f-4f38-97d5-918fbe42b3ec") //
				.name("mongo-database") //
				.description("MongoDB Database") //
				.tags("db", "nosql") //
				.bindable(true) //
				.plans(List.of(defaultPlan)) //
				.build();
	}

	@Override
	public CreateServiceInstanceResponse createServiceInstance(ServiceInstance serviceInstance) {
		String databaseName = serviceInstance.getServiceInstanceId();
		if (administration.hasDatabase(databaseName)) {
			throw new ServiceInstanceExistsException(databaseName, serviceInstance.getServiceDefinitionId());
		}

		administration.createDatabase(databaseName);

		return CreateServiceInstanceResponse.builder()//
				.async(false)//
				.build();
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(ServiceInstance serviceInstance) {
		String databaseName = serviceInstance.getServiceInstanceId();
		administration.deleteDatabase(databaseName);

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
		String databaseName = serviceBinding.getServiceInstanceId();

		String username = Optional.ofNullable(serviceBinding.getParameters().get("username")) //
				.filter(it -> String.class.isAssignableFrom(it.getClass())) //
				.map(String.class::cast) //
				.orElse(serviceBinding.getId());

		String password = Optional.ofNullable(serviceBinding.getParameters().get("password")) //
				.filter(it -> String.class.isAssignableFrom(it.getClass())) //
				.map(String.class::cast) //
				.orElseGet(() -> generatePassword(64));

		administration.createUserForDatabase(databaseName, username, password);
		Map<String, Object> credentials = administration.getCredentialsFor(databaseName, username, password);

		return CreateServiceInstanceAppBindingResponse.builder() //
				.async(false) //
				.credentials(credentials) //
				.build();
	}

	private String generatePassword(int length) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] token = new byte[length];
		secureRandom.nextBytes(token);
		return new BigInteger(1, token).toString(16);
	}
}
