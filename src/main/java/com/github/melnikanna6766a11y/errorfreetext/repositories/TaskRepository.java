package com.github.melnikanna6766a11y.errorfreetext.repositories;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(value = "task-entity-graph")
    @Query("select t from tasks t join t.status where t.status.id = 1")
    public List<Task> findAllTasksWhereCreated ();
}
