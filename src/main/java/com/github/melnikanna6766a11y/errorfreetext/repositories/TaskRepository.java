package com.github.melnikanna6766a11y.errorfreetext.repositories;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @EntityGraph(value = "task-entity-graph")
    @Query("select t from Task t join t.status s where s.id = 1")
    public List<Task> findAllCreatedTasks();

    @Query("select sum(t.numberOfCharacters) from Task t where t.completionDate = :date")
    public int findSumSentChars(LocalDate date);

    @Query("select sum(t.numberOfExecutions) from Task t where t.completionDate = :date")
    public int findSumSentExecutions(LocalDate date);
}
