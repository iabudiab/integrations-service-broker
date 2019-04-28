package de.iabudiab.servicebroker.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.iabudiab.servicebroker.model.ServiceInstanceBinding;

public interface ServiceInstanceBindingRepository extends MongoRepository<ServiceInstanceBinding, String> {

	List<ServiceInstanceBinding> findByServiceInstanceId(String serviceInstanceId);

	void deleteByServiceInstanceId(String serviceInstanceId);
}
