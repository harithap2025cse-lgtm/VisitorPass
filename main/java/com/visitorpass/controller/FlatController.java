package com.visitorpass.controller;

import com.visitorpass.dto.FlatRequest;
import com.visitorpass.entity.Flat;
import com.visitorpass.service.FlatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flats")
public class FlatController {

    private final FlatService flatService;

    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Flat create(@Valid @RequestBody FlatRequest request) {
        return flatService.create(request);
    }

    @GetMapping
    public List<Flat> getAll() {
        return flatService.getAll();
    }

    @GetMapping("/{id}")
    public Flat getById(@PathVariable Long id) {
        return flatService.getById(id);
    }
}
