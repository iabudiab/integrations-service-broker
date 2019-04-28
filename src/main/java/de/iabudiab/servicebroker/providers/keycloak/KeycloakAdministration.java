package de.iabudiab.servicebroker.providers.keycloak;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KeycloakAdministration {

	private final Keycloak keycloak;
	private final ObjectMapper mapper;

	public KeycloakAdministration(KeycloakProperties properties, ObjectMapper mapper) {
		this.mapper = mapper;
		this.keycloak = KeycloakBuilder.builder() //
				.serverUrl(properties.getServerUrl()) //
				.realm(properties.getRealm()) //
				.grantType(OAuth2Constants.PASSWORD) //
				.clientId(properties.getClientId()) //
				.clientSecret(properties.getClientSecret()) //
				.username(properties.getUsername()) //
				.password(properties.getPassword()) //
				.build();
	}

	public boolean hasRealm(String realmName) {
		List<RealmRepresentation> allRealms = keycloak.realms().findAll();
		for (RealmRepresentation realmRepresentation : allRealms) {
			if (realmRepresentation.getRealm().equalsIgnoreCase(realmName)) {
				return true;
			}
		}
		return false;
	}

	public void createRealm(String realmName, Map<String, Object> parameters) {
		Object realmObject = parameters.get("realm");
		String realmJson;
		try {
			realmJson = mapper.writeValueAsString(realmObject);
			RealmRepresentation representation = mapper.readValue(realmJson, RealmRepresentation.class);
			keycloak.realms().create(representation);
		} catch (IOException e) {
			// TODO
		}
	}

	public void deleteRealm(String realmName) {
		keycloak.realm(realmName).remove();
	}
}
