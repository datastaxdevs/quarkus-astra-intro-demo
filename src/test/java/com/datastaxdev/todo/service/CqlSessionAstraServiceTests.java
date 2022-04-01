package com.datastaxdev.todo.service;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import com.datastax.oss.quarkus.test.CassandraTestResource;

/**
 * {@link com.datastaxdev.todo.service.AstraService AstraService} tests using the
 * {@link com.datastaxdev.todo.service.CqlSessionAstraService CqlSessionAstraService} implementation.
 */
@QuarkusTest
@QuarkusTestResource(CassandraTestResource.class)
public class CqlSessionAstraServiceTests extends AstraServiceTests {

}
