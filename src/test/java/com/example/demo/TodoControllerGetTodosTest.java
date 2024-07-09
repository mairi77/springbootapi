package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerGetTodosTest {
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
    void getTodos_1() {
        //サーバに登録されているTODOが０件の場合
        ResponseEntity<List<TodoData>> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(200, response.getStatusCode().value());

        List<TodoData> todos = response.getBody();
        assertNotNull(todos);
        assertEquals(0, todos.size());
    }

    @Test
    void getTodos_2() {
        //サーバに登録されているTODOが１件の場合
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        ResponseEntity<List<TodoData>> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(200, response.getStatusCode().value());


        List<TodoData> todos = response.getBody();
        assertNotNull(todos);
        assertEquals(1, todos.size());

        //UUIDはSQL格納時に自動されるので、比較できないのでは？今はないから
        //assertEquals(test.getId(), tests.get(0).getId());
        assertEquals(todo.getId(), todos.get(0).getId());
        assertEquals(todo.getTitle(), todos.get(0).getTitle());
        assertEquals(todo.getDescription(), todos.get(0).getDescription());
        //serviceで作成日時は入れてるから、リポジトリ経由じゃできないので、一旦ここでのDATEを入れたのでそれと一致するかどうか
        assertEquals(todo.getCreatedAt(), todos.get(0).getCreatedAt());
    }

    @Test
    void getTodos_3() {
        //getTodos_サーバに登録されているTODOが３件の場合
        //UPあるver UPもFINもあるver　の二つも網羅すべき　今は３件とも同条件
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

        TodoData todo3 = new TodoData();
        todo3.setId(UUID.randomUUID().toString());
        todo3.setTitle("test3");
        todo3.setDescription("test3");
        LocalDateTime now3 = LocalDateTime.now();
        Date date3 = Date.from(now3.atZone(ZoneId.systemDefault()).toInstant());
        todo3.setCreatedAt(date3);
        todoRepository.insertTodo(todo3);

        ResponseEntity<List<TodoData>> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(200, response.getStatusCode().value());

        List<TodoData> todos = response.getBody();
        assertNotNull(todos);

        assertEquals(3, todos.size());

        LocalDateTime expectedCreatedAt1 = todo1.getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withNano(0); // 秒までを取得

        LocalDateTime actualCreatedAt1 = todos.get(0).getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withNano(0); // 秒までを取得

        LocalDateTime expectedCreatedAt2 = todo2.getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withNano(0); // 秒までを取得

        LocalDateTime actualCreatedAt2 = todos.get(1).getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withNano(0); // 秒までを取得

        LocalDateTime expectedCreatedAt3 = todo3.getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withNano(0); // 秒までを取得

        LocalDateTime actualCreatedAt3 = todos.get(2).getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .withNano(0); // 秒までを取得

        assertEquals(todo1.getId(), todos.get(0).getId());
        assertEquals(todo1.getTitle(), todos.get(0).getTitle());
        assertEquals(todo1.getDescription(), todos.get(0).getDescription());
        //assertEquals(test1.getCreatedAt(), tests.get(0).getCreatedAt());
        assertEquals(expectedCreatedAt1, actualCreatedAt1);

        assertEquals(todo2.getId(), todos.get(1).getId());
        assertEquals(todo2.getTitle(), todos.get(1).getTitle());
        assertEquals(todo2.getDescription(), todos.get(1).getDescription());
        //assertEquals(test2.getCreatedAt(), tests.get(0).getCreatedAt());
        assertEquals(expectedCreatedAt2, actualCreatedAt2);

        assertEquals(todo3.getId(), todos.get(2).getId());
        assertEquals(todo3.getTitle(), todos.get(2).getTitle());
        assertEquals(todo3.getDescription(), todos.get(2).getDescription());
        //assertEquals(test3.getCreatedAt(), tests.get(0).getCreatedAt());
        assertEquals(expectedCreatedAt3, actualCreatedAt3);

    }

}



