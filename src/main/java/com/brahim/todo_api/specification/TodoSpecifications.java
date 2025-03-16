package com.brahim.todo_api.specification;

import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.brahim.todo_api.model.Todo;

public class TodoSpecifications {
    public static Specification<Todo> buildSpecification(Map<String, String> filters) {
        Specification<Todo> spec = Specification.where(null);

        // Combine 'task' and 'description' with OR (for global search)
        Specification<Todo> taskOrDesc = Specification.where(null);
        if (filters.containsKey("task")) {
            String value = filters.get("task");
            taskOrDesc = taskOrDesc
                    .or((root, query, cb) -> cb.like(cb.lower(root.get("task")), "%" + value.toLowerCase() + "%"));
        }
        if (filters.containsKey("description")) {
            String value = filters.get("description");
            taskOrDesc = taskOrDesc.or(
                    (root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + value.toLowerCase() + "%"));
        }
        if (!taskOrDesc.equals(Specification.where(null))) {
            spec = spec.and(taskOrDesc);
        }

        // Handle 'completed' with AND
        if (filters.containsKey("completed")) {
            Boolean value = Boolean.parseBoolean(filters.get("completed"));
            spec = spec.and((root, query, cb) -> cb.equal(root.get("completed"), value));
        }

        return spec;
    }
}
