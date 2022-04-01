package com.datastaxdev.todo.rest;

import java.net.URI;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastaxdev.todo.api.Todo;
import com.datastaxdev.todo.service.AstraService;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/api")
public class TodoResource {
    @Inject
    AstraService astraService;

    @Inject
    CqlSession cqlSession;

    public void onStart(@Observes StartupEvent startupEvent) {
        ResultSet rs = this.cqlSession.execute("CREATE TABLE IF NOT EXISTS " + this.astraService.getTableKeyspaceName() + " (list_id text, id text, title text, completed boolean, PRIMARY KEY(list_id, id));");
        Log.infof("**** Table created %s****", rs.wasApplied());
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    @NonBlocking
    public String hello() {
        return "Hello Quarkus and Cassandra!";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}")
    public Multi<Todo> getTodos(@PathParam("list_id") String listId) {
        return this.astraService.getTodos(listId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}/{id}")
    public Response getTodo(@PathParam("list_id") String listId, @PathParam("id") String id) {
        return this.astraService.findTodoById(listId, id)
          .map(todo -> Response.ok(todo).build())
          .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}")
    public Uni<Response> setTodo(@PathParam("list_id") String listId, Todo todo, @Context UriInfo uriInfo) {
        return this.astraService.setTodo(listId, todo)
          .map(uri -> Response.created(createTodoURI(todo, uriInfo)).build());
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}/{id}")
    public void deleteTodos(@PathParam("list_id") String listId, @PathParam("id") String id){
        this.astraService.deleteTodos(listId, id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}/{id}")
    public void completeTodo(@PathParam("list_id") String listId, @PathParam("id") String id) {
        this.astraService.completeTodo(listId, id);
    }

    private static URI createTodoURI(Todo todo, UriInfo uriInfo) {
        return uriInfo.getAbsolutePathBuilder().path(todo.getId()).build();
    }
}
