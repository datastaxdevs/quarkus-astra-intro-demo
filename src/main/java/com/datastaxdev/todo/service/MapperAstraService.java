package com.datastaxdev.todo.service;

import java.util.Optional;

import io.quarkus.logging.Log;

import com.datastaxdev.todo.api.Todo;
import com.datastaxdev.todo.dao.TodoItemDao;
import com.datastaxdev.todo.mapping.TodoMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class MapperAstraService implements AstraService {
	private final TodoItemDao todoItemDao;
	private final TodoMapper todoMapper;

	public MapperAstraService(TodoItemDao todoItemDao, TodoMapper todoMapper) {
		this.todoItemDao = todoItemDao;
		this.todoMapper = todoMapper;
	}

	@Override
	public Multi<Todo> getTodos(String listId) {
		return this.todoItemDao.findByListId(listId)
			.invoke(todo -> Log.infof("Got todo: %s", todo))
			.map(this.todoMapper::mapToTodo);
	}

	@Override
	public Optional<Todo> findTodoById(String listId, String todoId) {
		return this.todoItemDao.findById(listId, todoId)
			.map(this.todoMapper::mapToTodo);
	}

	@Override
	public Uni<Void> setTodo(String listId, Todo todo) {
		return this.todoItemDao.save(this.todoMapper.mapToTodoItem(todo, listId));
	}

	@Override
	public void deleteTodos(String listId, String todoId) {
		this.todoItemDao.deleteById(listId, todoId);
	}

	@Override
	public void completeTodo(String listId, String todoId) {
		this.todoItemDao.findById(listId, todoId)
			.map(todoItem -> todoItem.completed(true))
			.ifPresent(this.todoItemDao::saveBlocking);
	}
}
