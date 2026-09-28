package com.visitorpass.repository;

import com.visitorpass.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResidentRepository extends JpaRepository<Resident, Long> {
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    List<Resident> findByFlatId(Long flatId);
}
