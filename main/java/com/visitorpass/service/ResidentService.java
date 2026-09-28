package com.visitorpass.service;

import com.visitorpass.dto.ResidentRequest;
import com.visitorpass.entity.Flat;
import com.visitorpass.entity.Resident;
import com.visitorpass.exception.BusinessRuleException;
import com.visitorpass.exception.ResourceNotFoundException;
import com.visitorpass.repository.FlatRepository;
import com.visitorpass.repository.ResidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final FlatRepository flatRepository;

    public ResidentService(ResidentRepository residentRepository, FlatRepository flatRepository) {
        this.residentRepository = residentRepository;
        this.flatRepository = flatRepository;
    }

    public Resident create(ResidentRequest request) {
        if (residentRepository.existsByPhone(request.phone())) {
            throw new BusinessRuleException("Resident phone number already exists.");
        }

        if (residentRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Resident email already exists.");
        }

        Flat flat = flatRepository.findById(request.flatId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Flat not found with id: " + request.flatId()));

        Resident resident = new Resident();
        resident.setName(request.name());
        resident.setPhone(request.phone());
        resident.setEmail(request.email());
        resident.setFlat(flat);

        return residentRepository.save(resident);
    }

    public Resident getById(Long id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resident not found with id: " + id));
    }

    public List<Resident> getAll() {
        return residentRepository.findAll();
    }

    public List<Resident> getByFlat(Long flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new ResourceNotFoundException("Flat not found with id: " + flatId);
        }

        return residentRepository.findByFlatId(flatId);
    }
}
