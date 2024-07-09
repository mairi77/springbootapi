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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerSearchTodosTest {

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
    void searchTodos_1() {
        TodoData todo = new TodoData();
        todo.setId(UUID.randomUUID().toString());
        todo.setTitle("test");
        todo.setDescription("test");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todoRepository.insertTodo(todo);

        TodoData todo2 = new TodoData();
        todo2.setId(UUID.randomUUID().toString());
        todo2.setTitle("test2 apple");
        todo2.setDescription("test2");
        LocalDateTime now2 = LocalDateTime.now();
        Date date2 = Date.from(now2.atZone(ZoneId.systemDefault()).toInstant());
        todo2.setCreatedAt(date2);
        todoRepository.insertTodo(todo2);

        TodoData todo3 = new TodoData();
        todo3.setId(UUID.randomUUID().toString());
        todo3.setTitle("test3　milk");
        todo3.setDescription("test3 apple");
        LocalDateTime now3 = LocalDateTime.now();
        Date date3 = Date.from(now3.atZone(ZoneId.systemDefault()).toInstant());
        todo3.setCreatedAt(date3);
        todoRepository.insertTodo(todo3);

        // testふくむやつ：３
        //List<TestData> searchResults = restTemplate.getForObject("http://localhost:" + port + "/test/search?keyword=test", List.class);
        ResponseEntity<List<TodoData>> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo/search?keyword=test",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TodoData>>() {}
        );
        List<TodoData> searchResults = response.getBody();

        // 検索結果が正しいことを確認
        assertNotNull(searchResults);
        assertEquals(3, searchResults.size());
        //検索結果にtest1,2,3が含まれていることを確認
        assertTrue(searchResults.stream().anyMatch(testData -> testData.getId().equals(todo.getId())));
        assertTrue(searchResults.stream().anyMatch(testData -> testData.getId().equals(todo2.getId())));
        assertTrue(searchResults.stream().anyMatch(testData -> testData.getId().equals(todo3.getId())));

        // appleふくむやつ：２（titleもdesも確認）
        //List<TestData> searchResults2 = restTemplate.getForObject("http://localhost:" + port + "/test/search?keyword=apple", List.class);
        ResponseEntity<List<TodoData>> response2 = restTemplate.exchange(
                "http://localhost:" + port + "/todo/search?keyword=apple",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TodoData>>() {}
        );
        List<TodoData> searchResults2 = response2.getBody();

        // 検索結果が正しいことを確認
        assertNotNull(searchResults2);
        assertEquals(2, searchResults2.size());
        //検索結果にtest2,3が含まれていることを確認
        assertTrue(searchResults2.stream().anyMatch(testData -> testData.getId().equals(todo2.getId())));
        assertTrue(searchResults2.stream().anyMatch(testData -> testData.getId().equals(todo3.getId())));

        // milkふくむやつ：１　
        //List<TestData> searchResults3 = restTemplate.getForObject("http://localhost:" + port + "/test/search?keyword=milk", List.class);

        ResponseEntity<List<TodoData>> response3 = restTemplate.exchange(
                "http://localhost:" + port + "/todo/search?keyword=milk",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TodoData>>() {}
        );
        List<TodoData> searchResults3 = response3.getBody();
        assert searchResults3 != null;

        // 検索結果が正しいことを確認
        assertNotNull(searchResults3);
        assertEquals(1, searchResults3.size());
        assertEquals(todo3.getId(), searchResults3.get(0).getId());

    }

    @Test
    void searchTodos_2() {
        // テスト前のデータベースの状態を保存
        List<TodoData> todosBeforeTest = todoRepository.selectTodo();

        // keywordがnullの場合にリクエストを送信
        //String keyword = null;
        ResponseEntity<List<TodoData>> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo/search?keyword=",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TodoData>>() {}
        );

        // HTTPステータスコードが400であることを確認
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // テスト後のデータベースの状態を確認
        List<TodoData> todosAfterTest = todoRepository.selectTodo();

        // テスト前後でデータベースの状態が変わっていないことを確認
        assertEquals(todosBeforeTest, todosAfterTest);
    }

    @Test
    void searchTodos_3() {
        // テスト前のデータベースの状態を保存
        List<TodoData> todosBeforeTest = todoRepository.selectTodo();

        // keywordが空文字の場合にリクエストを送信
        String keyword = "";
        ResponseEntity<List<TodoData>> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo/search?keyword=" + keyword,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TodoData>>() {}
        );

        // HTTPステータスコードが400であることを確認
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // テスト後のデータベースの状態を確認
        List<TodoData> todosAfterTest = todoRepository.selectTodo();

        // テスト前後でデータベースの状態が変わっていないことを確認
        assertEquals(todosBeforeTest, todosAfterTest);
    }

}