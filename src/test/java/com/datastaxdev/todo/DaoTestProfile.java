package com.datastaxdev.todo;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

import com.datastaxdev.todo.config.AstraConfig;

/**
 * Sets the {@code astra-service.type} property equal to {@code dao} so that the
 * {@link com.datastaxdev.todo.service.AstraService AstraService} implementation uses
 * {@link com.datastaxdev.todo.service.MapperAstraService MapperAstraService}.
 */
public class DaoTestProfile implements QuarkusTestProfile {
	@Override
	public Map<String, String> getConfigOverrides() {
		return Map.of(
			AstraConfig.ASTRA_SERVICE_IMPL_PROPERTY_NAME, "dao"
		);
	}
}
