package de.iabudiab.servicebroker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
public @interface ServiceInstanceProviderType {
}
