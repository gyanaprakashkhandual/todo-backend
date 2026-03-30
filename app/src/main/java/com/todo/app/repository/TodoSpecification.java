package com.todo.app.repository;

import com.todo.app.dto.TodoFilterRequest;
import com.todo.app.model.Todo;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TodoSpecification {

    private TodoSpecification() {
    }

    @SuppressWarnings("CollectionsToArray")
    public static Specification<Todo> build(Long userId, TodoFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ── Always scope to current user ──────────────────────────────────
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            // ── Full-text search ──────────────────────────────────────────────
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate notesMatch = cb.like(cb.lower(root.get("notes")), pattern);

                // Tag search via join
                Join<Object, Object> tagJoin = root.join("tags", JoinType.LEFT);
                Predicate tagMatch = cb.like(cb.lower(tagJoin.as(String.class)), pattern);

                predicates.add(cb.or(titleMatch, descMatch, notesMatch, tagMatch));
                query.distinct(true);
            }

            // ── Status filter ─────────────────────────────────────────────────
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            // ── Priority filter ───────────────────────────────────────────────
            if (filter.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), filter.getPriority()));
            }

            // ── Start date range ──────────────────────────────────────────────
            if (filter.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));
            }
            if (filter.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));
            }

            // ── End date range ────────────────────────────────────────────────
            if (filter.getEndDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), filter.getEndDateFrom()));
            }
            if (filter.getEndDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDateTo()));
            }

            // ── Single tag filter ─────────────────────────────────────────────
            if (filter.getTag() != null && !filter.getTag().isBlank()) {
                Join<Object, Object> tagJoin = root.join("tags", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(tagJoin.as(String.class)),
                        filter.getTag().toLowerCase()));
                query.distinct(true);
            }

            // ── Sorting ───────────────────────────────────────────────────────
            String sortField = isValidSortField(filter.getSortBy())
                    ? filter.getSortBy()
                    : "createdAt";

            Order order = "asc".equalsIgnoreCase(filter.getSortDir())
                    ? cb.asc(root.get(sortField))
                    : cb.desc(root.get(sortField));
            query.orderBy(order);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean isValidSortField(String field) {
        return field != null && List.of(
                "createdAt", "updatedAt", "title",
                "priority", "status", "startDate", "endDate").contains(field);
    }
}