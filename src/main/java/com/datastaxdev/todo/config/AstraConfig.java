package com.datastaxdev.todo.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import io.quarkus.arc.properties.IfBuildProperty;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.datastaxdev.todo.dao.TodoItemDao;
import com.datastaxdev.todo.mapping.TodoMapper;
import com.datastaxdev.todo.service.AstraService;
import com.datastaxdev.todo.service.CqlSessionAstraService;
import com.datastaxdev.todo.service.MapperAstraService;

@Dependent
public class AstraConfig {
	public static final String ASTRA_SERVICE_IMPL_PROPERTY_NAME = "astra-service.type";

	@Produces
	@ApplicationScoped
	@IfBuildProperty(name = ASTRA_SERVICE_IMPL_PROPERTY_NAME, stringValue = "cql-session", enableIfMissing = true)
	public AstraService cqlSessionAstraService(QuarkusCqlSession cqlSession) {
		return new CqlSessionAstraService(cqlSession);
	}

	@Produces
	@ApplicationScoped
	@IfBuildProperty(name = ASTRA_SERVICE_IMPL_PROPERTY_NAME, stringValue = "dao")
	public AstraService mapperAstraService(TodoItemDao todoItemDao, TodoMapper todoMapper) {
		return new MapperAstraService(todoItemDao, todoMapper);
	}
}
