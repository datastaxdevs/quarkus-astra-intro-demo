package com.datastaxdev.todo.service;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import com.datastax.oss.quarkus.test.CassandraTestResource;
import com.datastaxdev.todo.DaoTestProfile;

/**
 * {@link com.datastaxdev.todo.service.AstraService AstraService} tests using the
 * {@link com.datastaxdev.todo.service.MapperAstraService MapperAstraService} implementation.
 */
@QuarkusTest
@TestProfile(DaoTestProfile.class)
@QuarkusTestResource(CassandraTestResource.class)
public class MapperAstraServiceTests extends AstraServiceTests {
}
