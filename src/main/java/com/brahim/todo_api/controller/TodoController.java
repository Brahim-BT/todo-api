package com.brahim.todo_api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brahim.todo_api.model.PaginatedResponse;
import com.brahim.todo_api.model.Todo;
import com.brahim.todo_api.repository.TodoRepository;
import com.brahim.todo_api.specification.TodoSpecifications;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    // Custom endpoint to create multiple todos
    @PostMapping("/batch")
    public List<Todo> createTodos(@RequestBody List<Todo> todos) {
        return todoRepository.saveAll(todos);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<Todo>> getTodos(@RequestParam Map<String, String> filters,
            Pageable pageable) {

        Specification<Todo> spec = TodoSpecifications.buildSpecification(filters);
        Page<Todo> page = todoRepository.findAll(spec, pageable);

        return ResponseEntity
                .ok(new PaginatedResponse<>(page.getContent(), new PaginatedResponse.PageMetadata(page.getSize(),
                        page.getTotalElements(), page.getTotalPages(), page.getNumber())));
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return todoRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST (Create)
    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        return todoRepository.save(todo);
    }

    // PUT (Full Update)
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        return todoRepository.findById(id).map(existingTodo -> {
            existingTodo.setTask(todoDetails.getTask());
            existingTodo.setDescription(todoDetails.getDescription());
            existingTodo.setCompleted(todoDetails.isCompleted());
            existingTodo.setDueDate(todoDetails.getDueDate());
            return ResponseEntity.ok(todoRepository.save(existingTodo));
        }).orElse(ResponseEntity.notFound().build());
    }

    // PATCH (Partial Update)
    @PatchMapping("/{id}")
    public ResponseEntity<Todo> partialUpdateTodo(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        Optional<Todo> todo = todoRepository.findById(id);
        if (todo.isPresent()) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Todo.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, todo.get(), value);
                }
            });
            return ResponseEntity.ok(todoRepository.save(todo.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Todo> deleteTodo(@PathVariable Long id) {
        return todoRepository.findById(id).map(todo -> {
            todoRepository.delete(todo);
            return ResponseEntity.ok(todo);
        }).orElse(ResponseEntity.notFound().build());
    }

}