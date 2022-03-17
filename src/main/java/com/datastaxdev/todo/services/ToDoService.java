package com.datastaxdev.todo.services;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import com.datastaxdev.todo.model.ToDo;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ToDoService {

    @Inject
    QuarkusCqlSession cqlSession;

    @ConfigProperty(name = "quarkus.cassandra.todo.keyspace")
    private String keyspaceName;

    @ConfigProperty(name = "quarkus.cassandra.todo.table")
    private String tableName;

    private String tableFullName;

    public boolean onStart(@Observes StartupEvent ev) {
        ResultSet rs = this.cqlSession.execute("CREATE TABLE IF NOT EXISTS " +
                tableFullName + "(list_id text, id text, title text, completed boolean, PRIMARY KEY(list_id, id));");
        System.out.println("**** Table created " + rs.wasApplied() + "****");
        return rs.wasApplied();
    }

    @PostConstruct
    public void init() {
        tableFullName = keyspaceName + "." + tableName;
    }

    public List<ToDo> getTodos(String list_id) {
        ResultSet rs = this.cqlSession.execute("SELECT * FROM " + tableFullName + " where list_id =?", list_id);
        List<Row> rows = rs.all();
        return rows.stream()
                .map(x -> new ToDo(x.getString("id"), x.getString("title"), x.getBoolean("completed")))
                .collect(Collectors.toList());
    }

    public boolean setTodo(String list_id, ToDo todo) {
        ResultSet rs = this.cqlSession
                .execute("INSERT INTO " + tableFullName + "(list_id, id, title, completed) VALUES (?,?,?,?)",
                        list_id, todo.getId(), todo.getTitle(), todo.isCompleted());
        return rs.wasApplied();
    }

    public boolean deleteToDoById(String list_id, String id) {
        ResultSet rs = this.cqlSession.execute("DELETE FROM " + tableFullName + " WHERE list_id = ? AND id = ?", list_id, id);
        return rs.wasApplied();
    }

    public boolean completeToDo(String list_id, String id) {
        ResultSet rs = this.cqlSession.execute("INSERT INTO " + tableFullName + " (id, list_id, completed) VALUES (?, ?, ?)", id, list_id, true);
        return rs.wasApplied();
    }

}
