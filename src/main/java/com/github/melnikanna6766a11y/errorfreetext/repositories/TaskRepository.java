package com.github.melnikanna6766a11y.errorfreetext.repositories;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph()
    @Query("select t from Tasks t where ")
    public Task findEarliestCreatedTask();
}
