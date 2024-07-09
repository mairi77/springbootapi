
package com.example.demo;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Repository for Todo. This is an interface for MyBatis.
 */
@Mapper
public interface TodoRepository {
    /**
     * Get all todos
     * @return List of TodoData
     */
    List<TodoData> selectTodo();

    /**
     * Create a new todo
     * @param todo TodoData
     * @return int
     */
    int insertTodo(TodoData todo);

    /**
     * Delete a todo
     * @param id String
     * @return int
     */
    int deleteTodo(String id);

    /**
     * Get a todo by id
     * @param id String
     * @return TodoData
     */
    TodoData getTodoById(String id);

    /**
     * Update a todo
     *
     * @param todo TodoData
     */
    void updateTodo(TodoData todo);

    /**
     * Finish a todo
     * @param todo TodoData
     */
    void finishTodo(TodoData todo);

    /**
     * Get todos by keyword
     * @param keyword String
     * @return List of TodoData
     */
    List<TodoData> getTodosByKeyword(String keyword);

    /**
     * Delete all todos (for testing)
     */
    void deleteAllTodos();
}