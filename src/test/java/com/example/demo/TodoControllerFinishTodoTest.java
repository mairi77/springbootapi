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
class TodoControllerFinishTodoTest {

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
    void finishTodo_1() {
        //DB前提：データ2件　リクエスト：正常

        //テスト対象のデータを作成
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

        //テスト対象のデータを取得
        TodoData targetTodo = todoRepository.getTodoById(todo.getId());
        //テスト対象のデータのfinished_atがnullであることを確認
        assertNull(targetTodo.getFinishedAt());


        LocalDateTime beforeRequestTime = LocalDateTime.now();
        Date beforeRequestTime1 = Date.from(beforeRequestTime.atZone(ZoneId.systemDefault()).toInstant());
        //リクエストを実行
        restTemplate.put("http://localhost:" + port + "/todo/" + todo.getId() + "/finish", null);
        LocalDateTime afterResponseTime = LocalDateTime.now();
        Date afterResponseTime1 = Date.from(afterResponseTime.atZone(ZoneId.systemDefault()).toInstant());

        //テスト対象のデータを再取得
        TodoData updatedTodo = todoRepository.getTodoById(todo.getId());
        //テスト対象のデータのfinished_atがnullでないことを確認
        assertNotNull(updatedTodo.getFinishedAt());
        //テスト対象のデータのfinished_atがリクエストを実行した時刻とレスポンスが返ってきた時刻の間であることを確認
        assertTrue(updatedTodo.getFinishedAt().equals(beforeRequestTime) || (updatedTodo.getFinishedAt().equals(beforeRequestTime1) || updatedTodo.getFinishedAt().after(beforeRequestTime1)) && (updatedTodo.getFinishedAt().equals(afterResponseTime1) || updatedTodo.getFinishedAt().before(afterResponseTime1)));
        //テスト対象のデータのtitleとdescriptionとcreatedAtに変化がないことを確認
        assertEquals(targetTodo.getTitle(), updatedTodo.getTitle());
        assertEquals(targetTodo.getDescription(), updatedTodo.getDescription());
        assertEquals(targetTodo.getCreatedAt(), updatedTodo.getCreatedAt());

        //テスト対象以外のデータに変更がないことを確認
        TodoData notUpdatedTodo = todoRepository.getTodoById(todo2.getId());
        assertEquals(todo2, notUpdatedTodo);

    }

    @Test
    void finishTodo_2() {
        // DB前提：０　存在しないIDを指定した場合のテスト
        String nonExistentId = UUID.randomUUID().toString();

        // Send DELETE request and get the response
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo/" + nonExistentId + "/finish/",
                HttpMethod.PUT,
                null,
                String.class
        );

        // Check the HTTP status code
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void finishTodo_3() {
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
                "http://localhost:" + port + "/todo/" + nonExistentId + "/finish/",
                HttpMethod.PUT,
                null,
                String.class
        );

        // Check the HTTP status code
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Check that the number of records in the database has not changed
        List<TodoData> allTodos = todoRepository.selectTodo();
        assertEquals(2, allTodos.size());

        //元々のデータが変更されていないことを確認
        TodoData notUpdatedTodo1 = todoRepository.getTodoById(todo1.getId());
        assertEquals(todo1, notUpdatedTodo1);
        TodoData notUpdatedTodo2 = todoRepository.getTodoById(todo2.getId());
        assertEquals(todo2, notUpdatedTodo2);
    }
}