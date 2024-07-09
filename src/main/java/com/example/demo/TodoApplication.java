package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the Todo application.
 */
@SpringBootApplication
public class TodoApplication {
    /**
     * Main method for the Todo application.
     * @param args Command line arguments
     */
    public static void main(String[] args){
        SpringApplication.run(TodoApplication.class, args);
    }
}