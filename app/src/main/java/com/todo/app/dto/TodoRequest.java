package com.todo.app.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.todo.app.model.Priority;
import com.todo.app.model.TodoStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be under 255 characters")
    private String title;

    private String description;

    private String notes;

    @Size(max = 1024, message = "Ref link too long")
    private String refLink;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private Priority priority = Priority.MEDIUM;
    private TodoStatus status = TodoStatus.PENDING;

    @Size(max = 10, message = "Maximum 10 tags allowed")
    private List<String> tags = new ArrayList<>();
}