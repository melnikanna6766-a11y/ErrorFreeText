package com.github.melnikanna6766a11y.errorfreetext.repositories;

import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskCriteriaRepository {
    private EntityManager entityManager;

    public Task findEarliestCreatedTask() {
        EntityGraph<Task> taskEntityGraph = entityManager.createEntityGraph(Task.class);
        taskEntityGraph.addAttributeNode("status");
        taskEntityGraph.addAttributeNode("language");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> taskCriteriaQuery = criteriaBuilder.createQuery(Task.class);
        Root<Task> taskRoot = taskCriteriaQuery.from(Task.class);
        Join<Task, Status> taskStatusJoin = taskRoot.join("status");
        taskCriteriaQuery
                .select(taskRoot)
                .where(taskStatusJoin.get("status").equalTo("created"))
                .orderBy(criteriaBuilder.asc(taskRoot.get("id")));
        TypedQuery<Task> taskTypedQuery = entityManager.createQuery(taskCriteriaQuery);
        return taskTypedQuery.setMaxResults(1).getResultList().getFirst();
    }
}
