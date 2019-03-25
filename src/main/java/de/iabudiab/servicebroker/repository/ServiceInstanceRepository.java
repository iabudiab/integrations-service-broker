package de.iabudiab.servicebroker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.iabudiab.servicebroker.model.ServiceInstance;

public interface ServiceInstanceRepository extends MongoRepository<ServiceInstance, String> {

}
