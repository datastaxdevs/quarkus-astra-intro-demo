package com.datastaxdev.todo.api;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.datastaxdev.todo.model.ToDo;
import com.datastaxdev.todo.services.ToDoService;
import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
@Path("/api")
public class AstraToDoApi {

    @Inject
    ToDoService toDoService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}")
    @Blocking
    public List<ToDo> getTodos(@PathParam("list_id") String list_id) {
        return toDoService.getTodos(list_id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}")
    @Blocking
    public Response setTodo(@PathParam("list_id") String list_id, ToDo todo) {
        boolean toDoAdded = toDoService.setTodo(list_id, todo);
        return toDoAdded ? Response.ok().build() : Response.serverError().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}/{id}")
    @Blocking
    public boolean deleteTodo(@PathParam("list_id") String list_id, @PathParam("id") String id) {
        return toDoService.deleteToDoById(list_id, id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo/{list_id}/{id}")
    @Blocking
    public boolean completeTodo(@PathParam("list_id") String list_id, @PathParam("id") String id) {
        return toDoService.completeToDo(list_id, id);
    }
}
