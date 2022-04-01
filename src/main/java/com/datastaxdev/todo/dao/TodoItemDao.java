package com.datastaxdev.todo.dao;

import java.util.Optional;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Dao
public interface TodoItemDao {
	@Delete(entityClass = TodoItem.class)
	void deleteById(String listId, String id);

	@Select
	Multi<TodoItem> findByListId(String listId);

	@Select
	Optional<TodoItem> findById(String listId, String todoId);

	@Insert
	Uni<Void> save(TodoItem todo);

	@Insert
	void saveBlocking(TodoItem todo);
}
