package com.example.demo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * Data class for Todo.
 */
@Data
@NoArgsConstructor
public class TodoData {
    /**
     * ID of the Todo.
     */
    private String id;

    /**
     * Title of the Todo. Must be between 1 and 255 characters. Cannot be null.
     */
    @NotNull
    @Size(min=1, max=255)
    private String title;

    /**
     * Description of the Todo. Cannot be null.
     */
    @NotNull
    private String description;

    /**
     * Date and time the Todo was created.
     */
    private Date createdAt;

    /**
     * Date and time the Todo was last updated.
     */
    private Date updatedAt;

    /**
     * Date and time the Todo was finished.
     */
    private Date finishedAt;

    /**
     * Constructor for TodoData.
     * @param id ID of the Todo.
     * @param title Title of the Todo.
     * @param description Description of the Todo.
     * @param createdAt Date and time the Todo was created.
     * @param updatedAt Date and time the Todo was last updated.
     * @param finishedAt Date and time the Todo was finished.
     */
    public TodoData(String id, String title, String description, Date createdAt, Date updatedAt, Date finishedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.finishedAt = finishedAt;
    }



}