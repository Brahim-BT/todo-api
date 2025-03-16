package com.brahim.todo_api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brahim.todo_api.model.Todo;
import com.brahim.todo_api.repository.TodoRepository;
import com.brahim.todo_api.specification.TodoSpecifications;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "http://localhost:4200")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    // Custom endpoint to create multiple todos
    @PostMapping("/batch")
    public List<Todo> createTodos(@RequestBody List<Todo> todos) {
        return todoRepository.saveAll(todos);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getTodos(
            @RequestParam Map<String, String> filters,
            Pageable pageable) {

        Specification<Todo> spec = TodoSpecifications.buildSpecification(filters);
        Page<Todo> page = todoRepository.findAll(spec, pageable);

        System.out.println("Entered getTodos with filters: " + filters + " and pageable: " + pageable);
        // Wrap response to match Angular's PaginatedResponse structure
        return ResponseEntity.ok(Map.of("_embedded", Map.of("todos", page.getContent()), "page",
                Map.of("size", page.getSize(), "totalElements", page.getTotalElements(), "totalPages",
                        page.getTotalPages(), "number", page.getNumber())));
    }

}