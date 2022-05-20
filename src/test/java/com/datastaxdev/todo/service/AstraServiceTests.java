package com.datastaxdev.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastaxdev.todo.api.Todo;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

/**
 * Contains all the tests that are run against all implementations of the {@link com.datastaxdev.todo.service.AstraService AstraService}.
 */
abstract class AstraServiceTests {
	static final String DEFAULT_LIST_ID = UUID.randomUUID().toString();
	static final String DEFAULT_ID = UUID.randomUUID().toString();
	static final Todo DEFAULT_TODO = new Todo(DEFAULT_ID, "title", false);

	@Inject
	CqlSession cqlSession;

	@Inject
	AstraService astraService;

	@BeforeEach
	public void beforeEach() {
		this.cqlSession.execute(QueryBuilder.truncate(AstraService.TABLE_NAME).build());
		assertNoRows();
	}

	@Test
	public void noTodosFound() {
		this.astraService.getTodos(DEFAULT_LIST_ID)
			.subscribe().withSubscriber(AssertSubscriber.create())
			.assertSubscribed()
			.assertHasNotReceivedAnyItem();
	}

	@Test
	public void todosFound() {
		insertTodo();

		var todos = this.astraService.getTodos(DEFAULT_LIST_ID)
			.subscribe().withSubscriber(AssertSubscriber.create(1))
			.assertSubscribed()
			.awaitItems(1, Duration.ofSeconds(10))
			.assertCompleted()
			.getItems();

		assertThat(todos)
			.isNotNull()
			.hasSize(1)
			.first()
			.usingRecursiveComparison()
			.isEqualTo(DEFAULT_TODO);
	}

	@Test
	public void noTodoFound() {
		assertThat(this.astraService.findTodoById(DEFAULT_LIST_ID, DEFAULT_TODO.getId()))
			.isNotNull()
			.isEmpty();
	}

	@Test
	public void todoFound() {
		insertTodo();

		assertThat(this.astraService.findTodoById(DEFAULT_LIST_ID, DEFAULT_TODO.getId()))
			.isNotNull()
			.isNotEmpty()
			.get()
			.usingRecursiveComparison()
			.isEqualTo(DEFAULT_TODO);
	}

	@Test
	public void addTodo() {
		this.astraService.setTodo(DEFAULT_LIST_ID, DEFAULT_TODO)
			.subscribe().withSubscriber(UniAssertSubscriber.create())
			.assertSubscribed()
			.awaitItem(Duration.ofSeconds(10));

		assertNumberOfRows(1);

		assertThat(this.astraService.findTodoById(DEFAULT_LIST_ID, DEFAULT_TODO.getId()))
			.isNotNull()
			.isNotEmpty()
			.get()
			.usingRecursiveComparison()
			.isEqualTo(DEFAULT_TODO);
	}

	@Test
	public void deleteTodo() {
		insertTodo();
		this.astraService.deleteTodos(DEFAULT_LIST_ID, DEFAULT_TODO.getId());
		assertNoRows();
	}

	@Test
	public void completeTodo() {
		insertTodo();
		this.astraService.completeTodo(DEFAULT_LIST_ID, DEFAULT_TODO.getId());

		assertThat(this.astraService.findTodoById(DEFAULT_LIST_ID, DEFAULT_TODO.getId()))
			.isNotNull()
			.isNotEmpty()
			.get()
			.extracting(Todo::getId, Todo::getTitle, Todo::isCompleted)
			.containsExactly(DEFAULT_TODO.getId(), DEFAULT_TODO.getTitle(), true);
	}

	private void insertTodo() {
		insertTodo(1);
	}

	private void insertTodo(int expectedRowsAfterInsert) {
		this.cqlSession.execute(
			"INSERT INTO " + this.astraService.getTableKeyspaceName() + " (list_id, id, title, completed) VALUES (?, ?, ?, ?)",
			DEFAULT_LIST_ID,
			DEFAULT_TODO.getId(),
			DEFAULT_TODO.getTitle(),
			DEFAULT_TODO.isCompleted()
		);

		assertNumberOfRows(expectedRowsAfterInsert);
	}

	private void assertNoRows() {
		assertNumberOfRows(0);
	}

	private void assertNumberOfRows(int expectedNumberOfRows) {
		var rowCount = this.cqlSession.execute("SELECT COUNT(*) FROM " + this.astraService.getTableKeyspaceName())
			.one()
			.getLong(0);

		assertThat(rowCount).isEqualTo(expectedNumberOfRows);
	}
}