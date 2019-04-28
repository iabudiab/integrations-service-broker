package de.iabudiab.servicebroker.providers.mongo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MongoAdministration {

	private final MongoProperties properties;
	private final MongoClient client;

	public boolean hasDatabase(String dbName) {
		MongoIterable<String> allDbNames = client.listDatabaseNames();
		for (String database : allDbNames) {
			if (database.equals(dbName)) {
				return true;
			}
		}

		return false;
	}

	public MongoDatabase createDatabase(String databaseName) {
		log.debug("Creating new database: {}", databaseName);
		MongoDatabase adminDatabase = client.getDatabase(properties.getAuthenticationDatabase());

		Document roles = new Document("role", "dbOwner").append("db", databaseName);
		Document grantOwnerCommand = new Document("grantRolesToUser", properties.getUsername()).append("roles",
				List.of(roles));
		adminDatabase.runCommand(grantOwnerCommand);
		log.debug("Granted dbOwner to {} on: {}", properties.getUsername(), databaseName);

		MongoDatabase newDatabase = client.getDatabase(databaseName);
		MongoCollection<Document> collection = newDatabase.getCollection("welcome");
		collection.insertOne(new Document("createdAt", Instant.now()));

		log.debug("Created new database: {}", databaseName);
		return newDatabase;
	}

	public void deleteDatabase(String databaseName) {
		MongoDatabase database = client.getDatabase(databaseName);
		database.drop();
	}

	public void createUserForDatabase(String databaseName, String username, String password) {
		log.debug("Creating new user {} for: {}", username, databaseName);
		try {
			MongoDatabase database = client.getDatabase(databaseName);

			Document role = new Document("role", "readWrite").append("db", databaseName);
			Document createUserCommand = new Document("createUser", username) //
					.append("pwd", password) //
					.append("roles", List.of(role));

			Document result = database.runCommand(createUserCommand);
			// TODO handle result
		} catch (MongoException e) {
			// TODO
		}
	}

	public void deleteUserForDatabase(String databaseName, String username) {
		log.debug("Deleting user {} for: {}", username, databaseName);
		try {
			MongoDatabase database = client.getDatabase(databaseName);

			Document deleteUserCommand = new Document("dropUser", username);
			Document result = database.runCommand(deleteUserCommand);
			// TODO handle result
		} catch (MongoException e) {
			// TODO
		}
	}

	public Map<String, Object> getCredentialsFor(String databaseName, String username, String password) {
		String uri = new StringBuilder() //
				.append(properties.getHost()) //
				.append(":") //
				.append(properties.getPort()) //
				.append("/") //
				.append(databaseName) //
				.toString();

		Map<String, Object> credentials = Map.of( //
				"MONGODB_URI", uri, //
				"MONGODB_USERNAME", username, //
				"MONGODB_PASSOWRD", password, //
				"MONGODB_DATABASE", databaseName, //
				"MONGODB_AUTHENTICATION_DATABASE", databaseName //
		);

		return credentials;
	}
}
