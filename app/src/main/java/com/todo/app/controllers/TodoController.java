package com.todo.app.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.app.dto.TodoFilterRequest;
import com.todo.app.dto.TodoRequest;
import com.todo.app.dto.TodoResponse;
import com.todo.app.security.UserPrincipal;
import com.todo.app.service.TodoService;
import com.todo.app.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // ── POST /api/todos ───────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TodoRequest req) {
        TodoResponse res = todoService.create(principal.getId(), req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Todo created", res));
    }

    // ── GET /api/todos ────────────────────────────────────────────────────────
    // Supports: ?search=&status=&priority=&tag=
    // &startDateFrom=&startDateTo=&endDateFrom=&endDateTo=
    // &sortBy=createdAt&sortDir=desc&page=0&size=20
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TodoResponse>>> findAll(
            @AuthenticationPrincipal UserPrincipal principal,
            @ModelAttribute TodoFilterRequest filter) {
        Page<TodoResponse> page = todoService.findAll(principal.getId(), filter);
        return ResponseEntity.ok(ApiResponse.success("Todos fetched", page));
    }

    // ── GET /api/todos/stats ──────────────────────────────────────────────────
    // MUST be before /{id} to avoid route shadowing
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, Long> stats = todoService.getStats(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Stats fetched", stats));
    }

    // ── GET /api/todos/tags ───────────────────────────────────────────────────
    // MUST be before /{id} to avoid route shadowing
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<String>>> getTags(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<String> tags = todoService.getAllTags(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Tags fetched", tags));
    }

    // ── GET /api/todos/{id} ───────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> findById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        TodoResponse res = todoService.findById(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Todo fetched", res));
    }

    // ── PUT /api/todos/{id} ───────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest req) {
        TodoResponse res = todoService.update(principal.getId(), id, req);
        return ResponseEntity.ok(ApiResponse.success("Todo updated", res));
    }

    // ── PATCH /api/todos/{id}/status ──────────────────────────────────────────
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TodoResponse>> patchStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestParam String value) {
        TodoResponse res = todoService.patchStatus(principal.getId(), id, value);
        return ResponseEntity.ok(ApiResponse.success("Status updated", res));
    }

    // ── DELETE /api/todos/{id} ────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        todoService.delete(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Todo deleted"));
    }
}