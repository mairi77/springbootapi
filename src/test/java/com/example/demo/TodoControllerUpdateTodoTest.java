package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerUpdateTodoTest {

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
    void updateTodo_1() {
        //DB前提：データ2件　リクエスト：正常
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        //変更しないDBレコードを作成
        TodoData todo2 = new TodoData();
        todo2.setId(UUID.randomUUID().toString());
        todo2.setTitle("test2");
        todo2.setDescription("test2");
        LocalDateTime now2 = LocalDateTime.now();
        Date date2 = Date.from(now2.atZone(ZoneId.systemDefault()).toInstant());
        todo2.setCreatedAt(date2);
        todoRepository.insertTodo(todo2);

        todo.setTitle("updated");
        todo.setDescription("updated");
        LocalDateTime now3 = LocalDateTime.now();
        Date date3 = Date.from(now3.atZone(ZoneId.systemDefault()).toInstant());
        todo.setUpdatedAt(date3);

        LocalDateTime beforeRequestTime = LocalDateTime.now();
        Date beforeRequestTime1 = Date.from(beforeRequestTime.atZone(ZoneId.systemDefault()).toInstant());

        restTemplate.put("http://localhost:" + port + "/todo/" + todo.getId(), todo);

        LocalDateTime afterResponseTime = LocalDateTime.now();
        Date afterResponseTime1 = Date.from(afterResponseTime.atZone(ZoneId.systemDefault()).toInstant());

        //assertEquals(HttpStatus.OK, response.getStatusCode());

        TodoData updatedTodo = todoRepository.getTodoById(todo.getId());
        assertEquals("updated", updatedTodo.getTitle());
        assertEquals("updated", updatedTodo.getDescription());

        Date updatedAt = todo.getUpdatedAt();

        // 各Dateオブジェクトを出力します。
        System.out.println("Before Request Time: " + beforeRequestTime1);
        System.out.println("Updated At: " + updatedAt);
        System.out.println("After Response Time: " + afterResponseTime1);


        //assertTrue(updatedAt.equals(beforeRequestTime) || (updatedAt.after(beforeRequestTime1) && updatedAt.before(afterResponseTime1)));
        assertTrue(updatedAt.equals(beforeRequestTime) || (updatedAt.equals(beforeRequestTime1) || updatedAt.after(beforeRequestTime1)) && (updatedAt.equals(afterResponseTime1) || updatedAt.before(afterResponseTime1)));

        //test2のデータが変更されていないことを確認
        TodoData originalTodo2 = todoRepository.getTodoById(todo2.getId());
        assertEquals("test2", originalTodo2.getTitle());
        assertEquals("test2", originalTodo2.getDescription());
        //DBのレコード数が2であることを確認
        assertEquals(2, todoRepository.selectTodo().size());

    }

    @Test
    void updateTodo_2() {
        //DB前提：データ1件　リクエスト：NOT NULL不正

        // Prepare test data
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        // Prepare request body with null values for NOT NULL fields
        TodoData updatedTodo = new TodoData();
        updatedTodo.setId(todo.getId());
        updatedTodo.setTitle(null); // Assuming title is NOT NULL
        updatedTodo.setDescription(null); // Assuming description is NOT NULL

        // レスポンスのステータスコードが400であることを確認
        try {
            HttpEntity<TodoData> requestEntity = new HttpEntity<>(updatedTodo);
            restTemplate.exchange(
                    "http://localhost:" + port + "/todo/" + todo.getId(),
                    HttpMethod.PUT,
                    requestEntity,
                    new ParameterizedTypeReference<TodoData>() {}
            );
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getRawStatusCode());
        }

        // 元のデータが変更されていないことを確認
        TodoData originalTest = todoRepository.getTodoById(todo.getId());
        assertEquals("test", originalTest.getTitle());
        assertEquals("test", originalTest.getDescription());
        // DBのレコード数が1であることを確認
        assertEquals(1, todoRepository.selectTodo().size());
    }

    @Test
    void updateTodo_3() {
        //DB前提：データ1件　リクエスト：ID不正
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        // Prepare request body with non-existing ID
        TodoData updatedTodo = new TodoData();
        updatedTodo.setTitle("updated");
        updatedTodo.setDescription("updated");

        String nonExistentId = UUID.randomUUID().toString();

        HttpEntity<TodoData> requestEntity = new HttpEntity<>(updatedTodo);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo/" + nonExistentId,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // 元のデータが変更されていないことを確認
        TodoData originalTodo = todoRepository.getTodoById(todo.getId());
        assertEquals("test", originalTodo.getTitle());
        assertEquals("test", originalTodo.getDescription());

       //DBのレコード数が1であることを確認
        assertEquals(1, todoRepository.selectTodo().size());
    }


}