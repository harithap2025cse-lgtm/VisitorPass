package com.visitorpass.service;

import com.visitorpass.dto.FlatRequest;
import com.visitorpass.entity.Flat;
import com.visitorpass.exception.BusinessRuleException;
import com.visitorpass.exception.ResourceNotFoundException;
import com.visitorpass.repository.FlatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlatService {

    private final FlatRepository flatRepository;

    public FlatService(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    public Flat create(FlatRequest request) {
        if (flatRepository.existsByFlatNumber(request.flatNumber())) {
            throw new BusinessRuleException("Flat number already exists.");
        }

        Flat flat = new Flat();
        flat.setFlatNumber(request.flatNumber());
        flat.setBlock(request.block());
        flat.setFloor(request.floor());

        return flatRepository.save(flat);
    }

    public List<Flat> getAll() {
        return flatRepository.findAll();
    }

    public Flat getById(Long id) {
        return flatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + id));
    }
}
