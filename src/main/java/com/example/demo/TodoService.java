package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

/**
 * Service for handling Todo operations.
 */
@Service
public class TodoService {

    /**
     * Repository for handling Todo operations.
     */
    private final TodoRepository repository;

    /**
     * Constructor for TodoService.
     * @param repository TodoRepository
     */
    @Autowired
    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all todos
     * @return List of TodoData
     */
    public List<TodoData> getTodos(){
        return repository.selectTodo();
    }

    /**
     * Create a new todo
     * @param todo TodoData
     * @return TodoData
     */
    public TodoData create(TodoData todo){
        // UUIDの生成と設定
        String uuid = UUID.randomUUID().toString();
        todo.setId(uuid);

        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);

        int result = repository.insertTodo(todo);
        if (result > 0) {
            return todo;
        } else {
            return null;
        }
    }

    /**
     * Delete a todo
     * @param id String
     * @return ResponseEntity&lt;Void&gt;
     */
    public ResponseEntity<Void> delete(String id) {

        int deletedCount = repository.deleteTodo(id);
        if (deletedCount == 0) {
            // No records were deleted, return a custom response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Get a todo by id
     * @param id String
     * @return ResponseEntity&lt;TodoData&gt;
     */
    public ResponseEntity<TodoData> getTodoById(String id){
        TodoData result = repository.getTodoById(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Update a todo
     * @param id String
     * @param newTodo TodoData
     * @return ResponseEntity&lt;TodoData&gt;
     */
    public ResponseEntity<TodoData> updateTodo(String id, TodoData newTodo){
        TodoData todo = repository.getTodoById(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }

        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setUpdatedAt(date);

        todo.setTitle(newTodo.getTitle());
        todo.setDescription(newTodo.getDescription());
        repository.updateTodo(todo);
        TodoData updatedTodo = repository.getTodoById(id);

        return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
    }

    /**
     * Finish a todo
     * @param id String
     * @return ResponseEntity&lt;TodoData&gt;
     */
    public ResponseEntity<TodoData> finishTodo(String id){
        TodoData todo = repository.getTodoById(id);
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        if (todo != null) {
            todo.setFinishedAt(date);
            repository.finishTodo(todo);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    /**
     * Search todos by keyword
     * @param keyword String
     * @return List of TodoData
     */
    public ResponseEntity<List<TodoData>> searchTodos(String keyword){
        if (keyword.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<TodoData> todos = repository.getTodosByKeyword(keyword);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

}