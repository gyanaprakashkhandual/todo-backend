package com.todo.app.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.todo.app.model.Priority;
import com.todo.app.model.TodoStatus;

import lombok.Data;

@Data
public class TodoFilterRequest {

    // Full-text search across title, description, notes, tags
    private String search;

    private TodoStatus status;
    private Priority priority;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDateTo;

    private String tag;

    // Sorting: field name
    private String sortBy = "createdAt";

    // asc / desc
    private String sortDir = "desc";

    // Pagination
    private int page = 0;
    private int size = 20;
}