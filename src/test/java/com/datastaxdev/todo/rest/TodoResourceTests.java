package com.datastaxdev.todo.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import com.datastaxdev.todo.api.Todo;
import com.datastaxdev.todo.service.AstraService;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@QuarkusTest
public class TodoResourceTests {
    private static final String DEFAULT_LIST_ID = UUID.randomUUID().toString();
    private static final String DEFAULT_ID = UUID.randomUUID().toString();
    private static final String DEFAULT_TITLE = "title";
    private static final boolean DEFAULT_COMPLETED = false;

    private static final ArgumentMatcher<Todo> TODO_MATCHER = todo ->
      DEFAULT_ID.equals(todo.getId()) &&
        DEFAULT_TITLE.equals(todo.getTitle()) &&
        (DEFAULT_COMPLETED == todo.isCompleted());

    @InjectMock
    AstraService astraService;

    @Test
    public void testHelloEndpoint() {
        get("/api/hello")
          .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(ContentType.TEXT)
            .body(is("Hello Quarkus and Cassandra!"));

        verifyNoInteractions(this.astraService);
    }

    @Test
    public void getTodosNoneFound() {
        when(this.astraService.getTodos(eq(DEFAULT_LIST_ID)))
          .thenReturn(Multi.createFrom().empty());

        get("/api/todo/{list_id}", DEFAULT_LIST_ID)
          .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .body("$.size()", is(0));

        verify(this.astraService).getTodos(eq(DEFAULT_LIST_ID));
        verifyNoMoreInteractions(this.astraService);
    }

    @Test
    public void getTodosFound() {
        when(this.astraService.getTodos(eq(DEFAULT_LIST_ID)))
          .thenReturn(Multi.createFrom().item(new Todo(DEFAULT_ID, DEFAULT_TITLE, DEFAULT_COMPLETED)));

        get("/api/todo/{list_id}", DEFAULT_LIST_ID)
          .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .body("$.size()", is(1))
            .body("[0].id", is(DEFAULT_ID))
            .body("[0].title", is(DEFAULT_TITLE))
            .body("[0].completed", is(DEFAULT_COMPLETED));

        verify(this.astraService).getTodos(eq(DEFAULT_LIST_ID));
        verifyNoMoreInteractions(this.astraService);
    }

    @Test
    public void getTodoNotFound() {
        when(this.astraService.findTodoById(eq(DEFAULT_LIST_ID), eq(DEFAULT_ID)))
          .thenReturn(Optional.empty());

        get("/api/todo/{list_id}/{id}", DEFAULT_LIST_ID, DEFAULT_ID)
          .then()
            .statusCode(Status.NOT_FOUND.getStatusCode());

        verify(this.astraService).findTodoById(eq(DEFAULT_LIST_ID), eq(DEFAULT_ID));
        verifyNoMoreInteractions(this.astraService);
    }

    @Test
    public void getTodoFound() {
        when(this.astraService.findTodoById(eq(DEFAULT_LIST_ID), eq(DEFAULT_ID)))
          .thenReturn(Optional.of(new Todo(DEFAULT_ID, DEFAULT_TITLE, DEFAULT_COMPLETED)));

        get("/api/todo/{list_id}/{id}", DEFAULT_LIST_ID, DEFAULT_ID)
          .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("id", is(DEFAULT_ID))
              .body("title", is(DEFAULT_TITLE))
              .body("completed", is(DEFAULT_COMPLETED));

        verify(this.astraService).findTodoById(eq(DEFAULT_LIST_ID), eq(DEFAULT_ID));
        verifyNoMoreInteractions(this.astraService);
    }

    @Test
    public void createTodo() {
        when(this.astraService.setTodo(eq(DEFAULT_LIST_ID), argThat(TODO_MATCHER)))
          .thenReturn(Uni.createFrom().voidItem());

        given()
          .when()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(new Todo(DEFAULT_ID, DEFAULT_TITLE, DEFAULT_COMPLETED))
            .post("/api/todo/{list_id}", DEFAULT_LIST_ID)
          .then()
            .statusCode(Status.CREATED.getStatusCode())
            .header(HttpHeaders.LOCATION, CoreMatchers.endsWith(String.format("/api/todo/%s/%s", DEFAULT_LIST_ID, DEFAULT_ID)));

        verify(this.astraService).setTodo(eq(DEFAULT_LIST_ID), argThat(TODO_MATCHER));
        verifyNoMoreInteractions(this.astraService);
    }

    @Test
    public void deleteTodos() {
        doNothing()
          .when(this.astraService)
          .deleteTodos(eq(DEFAULT_LIST_ID), eq(DEFAULT_ID));

        delete("/api/todo/{list_id}/{id}", DEFAULT_LIST_ID, DEFAULT_ID)
          .then()
          .statusCode(Status.NO_CONTENT.getStatusCode());

        verify(this.astraService).deleteTodos(eq(DEFAULT_LIST_ID), eq(DEFAULT_ID));
        verifyNoMoreInteractions(this.astraService);
    }

    @Test
    public void completeTodo() {
        doNothing()
          .when(this.astraService)
          .completeTodo(eq(DEFAULT_LIST_ID), eq(DEFAULT_ID));

        post("/api/todo/{list_id}/{id}", DEFAULT_LIST_ID, DEFAULT_ID)
          .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());

        verify(this.astraService).completeTodo(eq(DEFAULT_LIST_ID), eq(DEFAULT_ID));
        verifyNoMoreInteractions(this.astraService);
    }
}
