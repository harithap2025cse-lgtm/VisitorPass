package com.visitorpass.controller;

import com.visitorpass.dto.ResidentRequest;
import com.visitorpass.entity.Resident;
import com.visitorpass.service.ResidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/residents")
public class ResidentController {

    private final ResidentService residentService;

    public ResidentController(ResidentService residentService) {
        this.residentService = residentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Resident create(@Valid @RequestBody ResidentRequest request) {
        return residentService.create(request);
    }

    @GetMapping
    public List<Resident> getAll() {
        return residentService.getAll();
    }

    @GetMapping("/{id}")
    public Resident getById(@PathVariable Long id) {
        return residentService.getById(id);
    }

    @GetMapping("/flat/{flatId}")
    public List<Resident> getByFlat(@PathVariable Long flatId) {
        return residentService.getByFlat(flatId);
    }
}
