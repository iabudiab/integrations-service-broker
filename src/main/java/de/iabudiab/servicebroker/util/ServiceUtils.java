package de.iabudiab.servicebroker.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Optional;

import de.iabudiab.servicebroker.model.ServiceInstanceBinding;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceUtils {

	public static String getUsernameParameter(ServiceInstanceBinding serviceBinding) {
		String username = Optional.ofNullable(serviceBinding.getParameters().get("username")) //
				.filter(it -> String.class.isAssignableFrom(it.getClass())) //
				.map(String.class::cast) //
				.orElse(serviceBinding.getId());
		return username;
	}

	public static String generatePassword(int length) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] token = new byte[length];
		secureRandom.nextBytes(token);
		return new BigInteger(1, token).toString(16);
	}
}
