package de.iabudiab.servicebroker.providers.kafka;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
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
import de.iabudiab.servicebroker.util.ServiceUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ServiceInstanceProviderType
public class KafkaTopicInstanceProvider implements ServiceInstanceProvider {

	private final KafkaAdministration administration;

	@Override
	public ServiceDefinition getServiceDefinition() {
		Plan defaultPlan = Plan.builder() //
				.id("c83a8d7d-81c5-4fdf-8a13-6b9b3c7fb9a5") //
				.name("default")//
				.description("Topic") //
				.bindable(true) //
				.build();

		return ServiceDefinition.builder() //
				.id("c5e31e6a-d710-4b3b-b9b8-60c71d9737fb") //	
				.name("kafka-topic") //
				.description("Kafka Topic") //
				.tags("stream", "messaging") //
				.bindable(true) //
				.instancesRetrievable(true) //
				.bindingsRetrievable(false) //
				.plans(List.of(defaultPlan)) //
				.build();
	}

	@Override
	public CreateServiceInstanceResponse createServiceInstance(ServiceInstance serviceInstance) {
		String topicName = serviceInstance.getServiceInstanceId();
		if (administration.hasTopic(topicName)) {
			throw new ServiceInstanceExistsException(topicName, serviceInstance.getServiceDefinitionId());
		}

		administration.createTopic(topicName);

		return CreateServiceInstanceResponse.builder()//
				.async(false)//
				.build();
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(ServiceInstance serviceInstance,
			List<ServiceInstanceBinding> serviceBindings) {

		return DeleteServiceInstanceResponse.builder()//
				.async(false)//
				.build();
	}

	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(ServiceInstance serviceInstance) {
		return null;
	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(ServiceInstance serviceInstance) {
		throw new UnsupportedOperationException("This service provider is synchronous");
	}

	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(
			ServiceInstanceBinding serviceBinding) {
		String topicName = serviceBinding.getServiceInstanceId();

		String username = ServiceUtils.getUsernameParameter(serviceBinding);

		administration.createAclForTopic(topicName, username);
		Map<String, Object> credentials = administration.getCredentialsFor(topicName, username);

		return CreateServiceInstanceAppBindingResponse.builder() //
				.async(false) //
				.credentials(credentials) //
				.build();
	}

	@Override
	public DeleteServiceInstanceBindingResponse deleteServiceInstanceBinding(
			ServiceInstanceBinding serviceBinding) {
		String topicName = serviceBinding.getServiceInstanceId();

		String username = ServiceUtils.getUsernameParameter(serviceBinding);

		administration.deleteAclForTopic(topicName, username);

		return DeleteServiceInstanceBindingResponse.builder() //
				.async(false) //
				.build();
	}
}
