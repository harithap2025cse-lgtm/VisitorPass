package com.visitorpass.repository;

import com.visitorpass.entity.Flat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlatRepository extends JpaRepository<Flat, Long> {
    boolean existsByFlatNumber(String flatNumber);
}
