package com.datastaxdev.todo.service;

import java.util.Optional;

import com.datastaxdev.todo.api.Todo;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface AstraService {
	String KEYSPACE_NAME = "todolist";
	String TABLE_NAME = "todolist";

	Multi<Todo> getTodos(String listId);
	Optional<Todo> findTodoById(String listId, String todoId);
	Uni<Void> setTodo(String listId, Todo todo);
	void deleteTodos(String listId, String todoId);
	void completeTodo(String listId, String todoId);

	default String getTableKeyspaceName() {
		return KEYSPACE_NAME + "." + TABLE_NAME;
	}
}
