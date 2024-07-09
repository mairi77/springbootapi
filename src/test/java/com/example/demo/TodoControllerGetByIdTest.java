package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerGetByIdTest {
    //複数パターン
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
    void getTodoById_1() {
        //DB前提：データ1件　リクエスト：正常
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        TodoData response = restTemplate.getForObject("http://localhost:" + port + "/todo/" + todo.getId(), TodoData.class);

        assertEquals(todo.getId(), response.getId());
        assertEquals(todo.getTitle(), response.getTitle());
        assertEquals(todo.getDescription(), response.getDescription());
    }

    @Test
    void getTodoById_2() {
        //DB前提：データ2件　リクエスト：正常

        //1つ目のデータを作成
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        //2つ目のデータを作成
        TodoData todo2 = new TodoData();
        todo2.setId(UUID.randomUUID().toString());
        todo2.setTitle("test2");
        todo2.setDescription("test2");
        LocalDateTime now2 = LocalDateTime.now();
        Date date2 = Date.from(now2.atZone(ZoneId.systemDefault()).toInstant());
        todo2.setCreatedAt(date2);
        todoRepository.insertTodo(todo2);

        //リクエストを投げる（testを検索）
        TodoData response = restTemplate.getForObject("http://localhost:" + port + "/todo/" + todo.getId(), TodoData.class);

        //レスポンスの内容を確認
        assertEquals(todo.getId(), response.getId());
        assertEquals(todo.getTitle(), response.getTitle());
        assertEquals(todo.getDescription(), response.getDescription());
        assertEquals(todo.getCreatedAt(), response.getCreatedAt());
    }

    @Test
    void getTodoById_3() {
        //DB前提：データ2件　リクエスト：異常（存在しないID）

        //1つ目のデータを作成
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        //2つ目のデータを作成
        TodoData todo2 = new TodoData();
        todo2.setId(UUID.randomUUID().toString());
        todo2.setTitle("test2");
        todo2.setDescription("test2");
        LocalDateTime now2 = LocalDateTime.now();
        Date date2 = Date.from(now2.atZone(ZoneId.systemDefault()).toInstant());
        todo2.setCreatedAt(date2);
        todoRepository.insertTodo(todo2);

        // 存在しないID
        String nonExistentId = UUID.randomUUID().toString();

        //リクエストを投げる（存在しないIDを検索）
        ResponseEntity<TodoData> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo" + nonExistentId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<TodoData>() {}
        );

        //レスポンスの内容を確認
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


}