package com.datastaxdev.todo.mapping;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.datastaxdev.todo.api.Todo;
import com.datastaxdev.todo.dao.TodoItem;

@Mapper(componentModel = "cdi", nullValueMappingStrategy = RETURN_DEFAULT)
public interface TodoMapper {
	Todo mapToTodo(TodoItem todoItem);

	@Mapping(source = "listId", target = "listId")
	TodoItem mapToTodoItem(Todo todo, String listId);
}
