package com.todo.app.service;

import com.todo.app.dto.TodoFilterRequest;
import com.todo.app.dto.TodoRequest;
import com.todo.app.dto.TodoResponse;
import com.todo.app.exception.BadRequestException;
import com.todo.app.exception.ResourceNotFoundException;
import com.todo.app.exception.UnauthorizedException;
import com.todo.app.model.Todo;
import com.todo.app.model.User;
import com.todo.app.repository.TodoRepository;
import com.todo.app.repository.TodoSpecification;
import com.todo.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // ── Helper: load and verify ownership ────────────────────────────────────
    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    private Todo loadTodo(Long todoId, Long userId) {
        return todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Todo not found with id: " + todoId));
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Transactional
    public TodoResponse create(Long userId, TodoRequest req) {
        User user = loadUser(userId);

        Todo todo = Todo.builder()
                .user(user)
                .title(req.getTitle())
                .description(req.getDescription())
                .notes(req.getNotes())
                .refLink(req.getRefLink())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .priority(req.getPriority())
                .status(req.getStatus())
                .tags(req.getTags())
                .build();

        return TodoResponse.from(todoRepository.save(todo));
    }

    // ── READ ALL (with filter + search + pagination) ───────────────────────────
    public Page<TodoResponse> findAll(Long userId, TodoFilterRequest filter) {
        // Validate filter
        if (!filter.isValid()) {
            throw new BadRequestException("Invalid filter parameters: date ranges must be valid and page >= 0");
        }

        Sort sort = Sort.by(
                "asc".equalsIgnoreCase(filter.getSortDir())
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                isValidSortField(filter.getSortBy()) ? filter.getSortBy() : "createdAt");
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Specification<Todo> spec = TodoSpecification.build(userId, filter);

        return todoRepository.findAll(spec, pageable)
                .map(TodoResponse::from);
    }

    // ── READ ONE ──────────────────────────────────────────────────────────────
    public TodoResponse findById(Long userId, Long todoId) {
        return TodoResponse.from(loadTodo(todoId, userId));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Transactional
    public TodoResponse update(Long userId, Long todoId, TodoRequest req) {
        Todo todo = loadTodo(todoId, userId);

        todo.setTitle(req.getTitle());
        todo.setDescription(req.getDescription());
        todo.setNotes(req.getNotes());
        todo.setRefLink(req.getRefLink());
        todo.setStartDate(req.getStartDate());
        todo.setEndDate(req.getEndDate());
        todo.setStartTime(req.getStartTime());
        todo.setEndTime(req.getEndTime());
        todo.setPriority(req.getPriority());
        todo.setStatus(req.getStatus());
        todo.setTags(req.getTags());

        return TodoResponse.from(todoRepository.save(todo));
    }

    // ── PATCH STATUS only ─────────────────────────────────────────────────────
    @Transactional
    public TodoResponse patchStatus(Long userId, Long todoId, String status) {
        Todo todo = loadTodo(todoId, userId);
        try {
            todo.setStatus(com.todo.app.model.TodoStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new com.todo.app.exception.BadRequestException("Invalid status: " + status);
        }
        return TodoResponse.from(todoRepository.save(todo));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Transactional
    public void delete(Long userId, Long todoId) {
        if (!todoRepository.existsByIdAndUserId(todoId, userId)) {
            throw new ResourceNotFoundException("Todo not found with id: " + todoId);
        }
        todoRepository.deleteById(todoId);
    }

    // ── STATS ─────────────────────────────────────────────────────────────────
    public Map<String, Long> getStats(Long userId) {
        return Map.of(
                "total", todoRepository.countByUserIdAndStatus(userId, com.todo.app.model.TodoStatus.PENDING)
                        + todoRepository.countByUserIdAndStatus(userId, com.todo.app.model.TodoStatus.IN_PROGRESS)
                        + todoRepository.countByUserIdAndStatus(userId, com.todo.app.model.TodoStatus.COMPLETED)
                        + todoRepository.countByUserIdAndStatus(userId, com.todo.app.model.TodoStatus.CANCELLED),
                "pending", todoRepository.countByUserIdAndStatus(userId, com.todo.app.model.TodoStatus.PENDING),
                "inProgress", todoRepository.countByUserIdAndStatus(userId, com.todo.app.model.TodoStatus.IN_PROGRESS),
                "completed", todoRepository.countByUserIdAndStatus(userId, com.todo.app.model.TodoStatus.COMPLETED),
                "cancelled", todoRepository.countByUserIdAndStatus(userId, com.todo.app.model.TodoStatus.CANCELLED));
    }

    // -- Tag suggestions for the user (OPTIMIZED query) --
    public List<String> getAllTags(Long userId) {
        // Verify user exists
        loadUser(userId);
        // Use optimized repository query instead of loading all todos
        return todoRepository.getAllDistinctTags(userId);
    }

    private boolean isValidSortField(String field) {
        return field != null && List.of(
                "createdAt", "updatedAt", "title",
                "priority", "status", "startDate", "endDate").contains(field);
    }
}