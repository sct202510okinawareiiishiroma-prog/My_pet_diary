package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomItemValueRepository extends JpaRepository<CustomItemValue, Long> {
    List<CustomItemValue> findByPetRecordId(Long petRecordId);
}