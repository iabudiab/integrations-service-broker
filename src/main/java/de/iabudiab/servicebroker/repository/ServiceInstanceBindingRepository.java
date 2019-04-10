package de.iabudiab.servicebroker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.iabudiab.servicebroker.model.ServiceInstanceBinding;

public interface ServiceInstanceBindingRepository extends MongoRepository<ServiceInstanceBinding, String> {

}
