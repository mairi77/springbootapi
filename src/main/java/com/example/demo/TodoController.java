package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling Todo operations.
 */
@RestController
@RequestMapping("/todo")

public class TodoController {

    /**
     * Service for handling Todo operations.
     */
    private final TodoService service;

    /**
     * Constructor
     * @param service TodoService
     */
    @Autowired
    public TodoController(TodoService service) {
        this.service = service;
    }

    /**
     * Get all todos
     * @return List of TodoData
     */
    @GetMapping
    public List<TodoData> getTodos(
    ){
        return service.getTodos();
    }

    /**
     * Create a new todo
     * @param todo TodoData
     * @return TodoData
     */
    @PostMapping
    public TodoData createTodos(
        @Valid @RequestBody TodoData todo
    ){
        return service.create(todo);
    }

    /**
     * Delete a todo
     * @param id String
     * @return ResponseEntity&lt;Void&gt;
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String id) {
        return service.delete(id);
    }

    /**
     * Get a todo by id
     * @param id String
     * @return ResponseEntity&lt;TodoData&gt;
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoData> getTodoById(@PathVariable String id) {
        return service.getTodoById(id);
    }

    /**
     * Update a todo
     * @param id String
     * @param newTodo TodoData
     * @return ResponseEntity&lt;TodoData&gt;
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoData> updateTodo(@PathVariable String id, @Valid @RequestBody TodoData newTodo) {
        return service.updateTodo(id, newTodo);
    }

    /**
     * Finish a todo
     * @param id String
     * @return ResponseEntity&lt;TodoData&gt;
     */
    @PutMapping("/{id}/finish")
    public ResponseEntity<TodoData> finishTodo(@PathVariable String id) {
        return service.finishTodo(id);
    }

    /**
     * Search todos by keyword
     * @param keyword String
     * @return ResponseEntity&lt;List&lt;TodoData&gt;&gt;
     */
    @GetMapping("/search")
    public ResponseEntity<List<TodoData>> searchTodos(@RequestParam String keyword) {
        return service.searchTodos(keyword);
    }


}

