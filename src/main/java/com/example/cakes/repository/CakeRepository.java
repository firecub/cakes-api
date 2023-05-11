package com.example.cakes.repository;

import com.example.cakes.CakeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CakeRepository extends CrudRepository<CakeEntity, Long> {
    List<CakeEntity> findByTitle(String title);
    Optional<CakeEntity> findById(long id);
}
