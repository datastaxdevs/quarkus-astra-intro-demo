package com.datastaxdev.todo.dao;

import java.util.StringJoiner;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastaxdev.todo.service.AstraService;

@Entity
@CqlName(AstraService.TABLE_NAME)
public class TodoItem {
	@PartitionKey
	@CqlName("list_id")
	private String listId;

	@ClusteringColumn
	private String id;

	private String title;
	private Boolean completed;

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public TodoItem completed(Boolean completed) {
		setCompleted(completed);
		return this;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TodoItem.class.getSimpleName() + "[", "]")
			.add("listId='" + listId + "'")
			.add("id='" + id + "'")
			.add("title='" + title + "'")
			.add("completed=" + completed)
			.toString();
	}
}
