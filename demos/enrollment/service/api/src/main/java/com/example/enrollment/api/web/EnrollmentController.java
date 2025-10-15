package com.example.enrollment.api.web;

import com.example.enrollment.application.enrollment.EnrollmentCommandService;
import com.example.enrollment.application.enrollment.EnrollmentQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentCommandService commandService;
    private final EnrollmentQueryService queryService;

    public EnrollmentController(EnrollmentCommandService commandService, EnrollmentQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody CreateEnrollmentRequest request) {
        String id = commandService.create(request.name());
        return ResponseEntity.created(URI.create("/api/enrollments/" + id)).body(Map.of("id", id));
    }

    @GetMapping
    public List<EnrollmentQueryService.EnrollmentView> list() {
        return queryService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentQueryService.EnrollmentView> get(@PathVariable String id) {
        Optional<EnrollmentQueryService.EnrollmentView> view = queryService.getById(id);
        return view.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record CreateEnrollmentRequest(String name) {}
}
