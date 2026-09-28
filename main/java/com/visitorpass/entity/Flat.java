package com.visitorpass.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flats")
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 30)
    private String flatNumber;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String block;

    @Column(nullable = false)
    private Integer floor;

    @JsonIgnore
    @OneToMany(mappedBy = "flat")
    private List<Resident> residents = new ArrayList<>();

    public Flat() {
    }

    public Long getId() {
        return id;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public List<Resident> getResidents() {
        return residents;
    }
}
