package com.datastaxdev.todo.service;

import java.util.Optional;

import io.quarkus.logging.Log;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.datastaxdev.todo.api.Todo;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public class CqlSessionAstraService implements AstraService {
	private final QuarkusCqlSession cqlSession;

	public CqlSessionAstraService(QuarkusCqlSession cqlSession) {
		this.cqlSession = cqlSession;
	}

	@Override
	public Multi<Todo> getTodos(String listId) {
		return Multi.createFrom().publisher(
				this.cqlSession.executeReactive(
					"SELECT * FROM " + getTableKeyspaceName() + " WHERE list_id = ?",
					listId
				)
			)
			.map(this::mapRow)
			.invoke(todo -> Log.infof("Got todo: %s", todo));
	}

	@Override
	public Optional<Todo> findTodoById(String listId, String todoId) {
		return Optional.ofNullable(
				this.cqlSession.execute(
						"SELECT * FROM " + getTableKeyspaceName() + " WHERE list_id = ? AND id = ?",
						listId,
						todoId
					)
					.one()
			)
			.map(this::mapRow);
	}

	@Override
	public Uni<Void> setTodo(String listId, Todo todo) {
		return Uni.createFrom().publisher(
				this.cqlSession.executeReactive(
					"INSERT INTO " + getTableKeyspaceName() + " (list_id, id, title, completed) VALUES (?,?,?,?)",
					listId,
					todo.getId(),
					todo.getTitle(),
					todo.isCompleted()
				)
			)
			.replaceWithVoid();
	}

	@Override
	public void deleteTodos(String listId, String todoId) {
		this.cqlSession.execute(
			"DELETE FROM " + getTableKeyspaceName() + " WHERE list_id = ? AND id = ?",
			listId,
			todoId
		);
	}

	@Override
	public void completeTodo(String listId, String todoId) {
		this.cqlSession.execute(
			"INSERT INTO " + getTableKeyspaceName() + " (id, list_id, completed) VALUES (?, ?, ?)",
			todoId,
			listId,
			true
		);
	}

	private Todo mapRow(Row row) {
		return new Todo(row.getString("id"), row.getString("title"), row.getBoolean("completed"));
	}
}
