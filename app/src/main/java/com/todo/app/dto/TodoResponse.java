package com.todo.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.todo.app.model.Priority;
import com.todo.app.model.Todo;
import com.todo.app.model.TodoStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private String notes;
    private String refLink;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Priority priority;
    private TodoStatus status;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Static factory ────────────────────────────────────────────────────────
    public static TodoResponse from(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .notes(todo.getNotes())
                .refLink(todo.getRefLink())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .startTime(todo.getStartTime())
                .endTime(todo.getEndTime())
                .priority(todo.getPriority())
                .status(todo.getStatus())
                .tags(todo.getTags())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }
}