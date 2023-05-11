package com.example.cakes.controller;

import com.example.cakes.CakeEntity;
import com.example.cakes.repository.CakeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cakes")
public class CakesController {
    @Autowired
    private CakeRepository cakeRepository;

    @GetMapping
    ResponseEntity<Iterable<CakeEntity>> getCakes() {
        return new ResponseEntity<>(cakeRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CakeEntity> addCake(@RequestBody CakeEntity newCake) {
        return new ResponseEntity<>(cakeRepository.save(newCake), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CakeEntity> updateCake(@PathVariable("id") long id, @RequestBody CakeEntity cake) {
        Optional<CakeEntity> currentCakeEntityOptional = cakeRepository.findById(id);
        if (currentCakeEntityOptional.isPresent()) {
            CakeEntity currentCakeEntity = currentCakeEntityOptional.get();
            currentCakeEntity.setTitle(cake.getTitle());
            currentCakeEntity.setDescription(cake.getDescription());
            currentCakeEntity.setImage(cake.getImage());
            return new ResponseEntity<>(cakeRepository.save(currentCakeEntity), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteCake(@PathVariable("id") long id) {
        cakeRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
