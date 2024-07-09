package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerCreateTodosTest {
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
    void createTodos_1() {

        String jsontest = "{\n" +
                "    \"title\": \"sample\",\n" +
                "    \"description\": \"sample\"\n" +
                "}";

        // HttpHeadersオブジェクトを作成し、Content-Typeを設定
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntityオブジェクトを作成
        HttpEntity<String> entity = new HttpEntity<>(jsontest, headers);

        // RestTemplateオブジェクトを作成
        RestTemplate restTemplate = new RestTemplate();

        // リクエストを送信する前の現在時刻を取得
        LocalDateTime beforeRequestTime = LocalDateTime.now();
        Date beforeRequestTime1 = Date.from(beforeRequestTime.atZone(ZoneId.systemDefault()).toInstant());

        ResponseEntity<TodoData> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<TodoData>() {}
        );

        // レスポンスを受け取った現在時刻を取得
        LocalDateTime afterResponseTime = LocalDateTime.now();
        Date afterResponseTime1 = Date.from(afterResponseTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(200, response.getStatusCode().value());

        // データベースのレコード数が1であることを確認
        List<TodoData> allTests = todoRepository.selectTodo();
        assertEquals(1, allTests.size());

        //返り値が正しいか　これはサービス部分の動作確認かも
        TodoData todo = response.getBody();
        assertNotNull(todo);
        assertEquals("sample", todo.getTitle());
        assertEquals("sample", todo.getDescription());
        assertNotNull(todo.getId());
        assertNotNull(todo.getCreatedAt());

        // データベースからデータを取得
        TodoData dbTodo = todoRepository.getTodoById(todo.getId());

        // データベースのデータが期待通りの値を持っていることを確認
        assertNotNull(dbTodo);
        assertEquals("sample", dbTodo.getTitle());
        assertEquals("sample", dbTodo.getDescription());
        assertEquals(todo.getId(), dbTodo.getId());
        assertEquals(todo.getCreatedAt(), dbTodo.getCreatedAt());

        // createdAtが正しいことを確認
        assertNotNull(todo.getCreatedAt());
        Date createdAt = todo.getCreatedAt();

        //print
        System.out.println("beforeRequestTime1: " + beforeRequestTime1);
        System.out.println("createdAt: " + createdAt);
        System.out.println("afterResponseTime1: " + afterResponseTime1);

        // createdAtがリクエストを送信した時刻と同じか、それ以降であり、かつレスポンスを受け取った時刻より前であることを確認
        assertTrue(createdAt.equals(beforeRequestTime) || (createdAt.after(beforeRequestTime1) && createdAt.before(afterResponseTime1)));
    }

    @Test
    void createTodos_2() {
        //DB前提：3つあり　リクエストボディ：正常

        // UUIDとcreatedAtを保存するための変数を作成
        String[] ids = new String[3];
        Date[] createdAts = new Date[3];

        for (int i = 0; i < 3; i++) {
            TodoData todoData = new TodoData();

            // UUIDとcreatedAtを生成
            String id = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();
            Date createdAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

            // 生成したUUIDとcreatedAtをテストデータに設定
            todoData.setId(id);
            todoData.setTitle("test" + i);
            todoData.setDescription("description" + i);
            todoData.setCreatedAt(createdAt);

            // 生成したUUIDとcreatedAtを変数に保存
            ids[i] = id;
            createdAts[i] = createdAt;

            todoRepository.insertTodo(todoData);
        }



        String jsontest = "{\n" +
                "    \"title\": \"sample\",\n" +
                "    \"description\": \"sample\"\n" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(jsontest, headers);

        RestTemplate restTemplate = new RestTemplate();

        LocalDateTime beforeRequestTime = LocalDateTime.now();
        Date beforeRequestTime1 = Date.from(beforeRequestTime.atZone(ZoneId.systemDefault()).toInstant());

        ResponseEntity<TodoData> response = restTemplate.exchange(
                "http://localhost:" + port + "/todo",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<TodoData>() {}
        );

        LocalDateTime afterResponseTime = LocalDateTime.now();
        Date afterResponseTime1 = Date.from(afterResponseTime.atZone(ZoneId.systemDefault()).toInstant());

        assertEquals(200, response.getStatusCode().value());

        // データベースのレコード数が4であることを確認
        List<TodoData> allTests = todoRepository.selectTodo();
        assertEquals(4, allTests.size());

        TodoData todo = response.getBody();
        assertNotNull(todo);
        assertEquals("sample", todo.getTitle());
        assertEquals("sample", todo.getDescription());
        assertNotNull(todo.getId());
        assertNotNull(todo.getCreatedAt());

        TodoData dbTodo = todoRepository.getTodoById(todo.getId());

        assertNotNull( dbTodo);
        assertEquals("sample",  dbTodo.getTitle());
        assertEquals("sample",  dbTodo.getDescription());
        assertEquals(todo.getId(),  dbTodo.getId());
        assertEquals(todo.getCreatedAt(),  dbTodo.getCreatedAt());

        assertNotNull(todo.getCreatedAt());
        Date createdAt = todo.getCreatedAt();

        assertTrue(createdAt.equals(beforeRequestTime) || (createdAt.after(beforeRequestTime1) && createdAt.before(afterResponseTime1)));

        // データベースから元の3つのテストデータを取得し、それらがテスト前と同じであることを確認
        for (int i = 0; i < 3; i++) {
            TodoData originalTodo = todoRepository.getTodoById(ids[i]);
            assertEquals(ids[i], originalTodo.getId());
            assertEquals("test" + i, originalTodo.getTitle());
            assertEquals("description" + i, originalTodo.getDescription());
            assertEquals(createdAts[i], originalTodo.getCreatedAt());
        }
    }

    @Test
    void createTodos_3() {
        // リクエストボディが不適切な場合のテスト　DB前提：空っぽ

        // titleが欠けているリクエストボディ
        String jsontodo = "{\n" +
                "    \"description\": \"sample\"\n" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(jsontodo, headers);

        RestTemplate restTemplate = new RestTemplate();

        // レスポンスのステータスコードが400であることを確認
        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/todo",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<TodoData>() {}
            );
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getRawStatusCode());
        }

        // descriptionが欠けているリクエストボディ
        String jsonMissingDescription = "{\n" +
                "    \"title\": \"sample\"\n" +
                "}";

        HttpEntity<String> entityMissingDescription = new HttpEntity<>(jsonMissingDescription, headers);

        // レスポンスのステータスコードが400であることを確認
        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/todo",
                    HttpMethod.POST,
                    entityMissingDescription,
                    new ParameterizedTypeReference<TodoData>() {}
            );
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(400, e.getRawStatusCode());
        }

    }






}