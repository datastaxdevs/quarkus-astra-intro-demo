package com.datastaxdev.todo.dao;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface TodoItemMapper {
	@DaoFactory
	TodoItemDao todoItemDao();
}
