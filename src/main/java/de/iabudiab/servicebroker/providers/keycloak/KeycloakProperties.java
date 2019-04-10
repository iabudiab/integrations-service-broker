package de.iabudiab.servicebroker.providers.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

	private String serverUrl;
	private String realm;
	private String username;
	private String password;
	private String clientId;
	private String clientSecret;
}
