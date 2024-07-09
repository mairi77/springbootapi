package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerDeleteTodoTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;
    //これなんだっけ　あの図のどこをどう担ってるんだっけ

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAllTodos();
    }

    @Test
    void deleteTodo_1() {
        //DB前提：データ1件　リクエスト：正常
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        restTemplate.delete("http://localhost:" + port + "/todo/" + todo.getId());

        TodoData deletedTodo = todoRepository.getTodoById(todo.getId());
        assertNull(deletedTodo);
    }

    @Test
    void deleteTodo_2() {
        //DB前提：データ2件　リクエスト：正常
        // 複数のテストデータをデータベースに挿入
        TodoData todo1 = new TodoData();
        todo1.setId(UUID.randomUUID().toString());
        todo1.setTitle("test1");
        todo1.setDescription("test1");
        LocalDateTime now1 = LocalDateTime.now();
        Date date1 = Date.from(now1.atZone(ZoneId.systemDefault()).toInstant());
        todo1.setCreatedAt(date1);
        todoRepository.insertTodo(todo1);

        TodoData todo2 = new TodoData();
        todo2.setId(UUID.randomUUID().toString());
        todo2.setTitle("test2");
        todo2.setDescription("test2");
        LocalDateTime now2 = LocalDateTime.now();
        Date date2 = Date.from(now2.atZone(ZoneId.systemDefault()).toInstant());
        todo2.setCreatedAt(date2);
        todoRepository.insertTodo(todo2);

        // 一つのテストデータを削除
        restTemplate.delete("http://localhost:" + port + "/todo/" + todo1.getId());

        // 削除したテストデータが存在しないことを確認
        TodoData deletedTodo = todoRepository.getTodoById(todo1.getId());
        assertNull(deletedTodo);

        // 削除していないテストデータが依然として存在することを確認
        TodoData remainingTodo = todoRepository.getTodoById(todo2.getId());
        assertNotNull(remainingTodo);
    }

    @Test
    void deleteTodo_3() {
        // 存在しないIDを指定した場合のテスト
        String nonExistentId = UUID.randomUUID().toString();

        // Send DELETE request and get the response
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo/" + nonExistentId,
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Check the HTTP status code
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteTodo_4() {
        // DB前提：データ2件 存在しないIDを指定した場合のテスト
        TodoData todo1 = new TodoData();
        todo1.setId(UUID.randomUUID().toString());
        todo1.setTitle("test1");
        todo1.setDescription("test1");
        LocalDateTime now1 = LocalDateTime.now();
        Date date1 = Date.from(now1.atZone(ZoneId.systemDefault()).toInstant());
        todo1.setCreatedAt(date1);
        todoRepository.insertTodo(todo1);

        TodoData todo2 = new TodoData();
        todo2.setId(UUID.randomUUID().toString());
        todo2.setTitle("test2");
        todo2.setDescription("test2");
        LocalDateTime now2 = LocalDateTime.now();
        Date date2 = Date.from(now2.atZone(ZoneId.systemDefault()).toInstant());
        todo2.setCreatedAt(date2);
        todoRepository.insertTodo(todo2);

        // 存在しないIDを指定した場合のテスト
        String nonExistentId = UUID.randomUUID().toString();

        // Send DELETE request and get the response
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo/" + nonExistentId,
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Check the HTTP status code
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Check that the number of records in the database has not changed
        List<TodoData> allTodos = todoRepository.selectTodo();
        assertEquals(2, allTodos.size());
    }
}