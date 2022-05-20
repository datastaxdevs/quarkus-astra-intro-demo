package com.datastaxdev.todo.rest;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import com.datastax.oss.quarkus.test.CassandraTestResource;

@QuarkusIntegrationTest
@QuarkusTestResource(CassandraTestResource.class)
public class TodoResourceIT {

}