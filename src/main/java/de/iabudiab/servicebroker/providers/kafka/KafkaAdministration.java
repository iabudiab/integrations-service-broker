package de.iabudiab.servicebroker.providers.kafka;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaAdministration {

	private final KafkaAdminClient client;

	public boolean hasTopic(String topicName) {
		try {
			return client.listTopics().names().thenApply(names -> names.contains(topicName)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new ServiceBrokerException("Error checking for existing topic " + topicName, e);
		}
	}

	public void createTopic(String topicName) {
		log.debug("Creating kafka topic {}", topicName);
		// TODO provide config for partitions count and replication factor
		NewTopic topic = new NewTopic(topicName, 1, (short) 1);
		CreateTopicsResult result = client.createTopics(List.of(topic));
		try {
			result.values().get(topicName).get(30, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ServiceBrokerException("Error creating kafka topic " + topicName, e);
		}

		// TODO give admin user admin acls for topic
	}

	public void createAclForTopic(String topicName, String username) {
		// TODO create acl for group resource

		String principal = "User:" + username;
		String host = "*";
		AccessControlEntry aclEntry = new AccessControlEntry(principal, host, AclOperation.WRITE,
				AclPermissionType.ALLOW);
		ResourcePattern pattern = new ResourcePattern(ResourceType.TOPIC, topicName, PatternType.LITERAL);
		AclBinding aclBinding = new AclBinding(pattern, aclEntry);

		CreateAclsResult result = client.createAcls(List.of(aclBinding));
		try {
			result.values().get(aclBinding).get(30, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ServiceBrokerException("Error creating acl for topic " + topicName, e);
		}
	}

	public Map<String, Object> getCredentialsFor(String topicName, String username) {
		return null;
	}

	public void deleteAclForTopic(String topicName, String username) {
		// TODO Auto-generated method stub
	}
}
