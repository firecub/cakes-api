package com.example.cakes.controller;

import com.example.cakes.CakeEntity;
import com.example.cakes.repository.CakeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class CakesControllerTest {

    @InjectMocks
    private CakesController cakesController;

    @Mock
    private CakeRepository cakeRepository;

    @Test
    public void testGetCakes() {
        CakeEntity[] cakes = {
                new CakeEntity("Layer cake", "A cake made of layers.", "http://roguecakes.co.uk/layercake.jpg"),
                new CakeEntity("Mud cake", "A cake made of mud.", "http://roguecakes.co.uk/mudcake.jpg")
        };
        Mockito.when(cakeRepository.findAll()).thenReturn(Arrays.asList(cakes));
        ResponseEntity<Iterable<CakeEntity>> responseEntity = cakesController.getCakes();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Set<CakeEntity> expectedCakeEntitySet = Set.of(cakes);
        Set<CakeEntity> actualCakeEntitySet = new HashSet<>();
        responseEntity.getBody().forEach(cakeEntity -> actualCakeEntitySet.add(cakeEntity));
        assertEquals(expectedCakeEntitySet, actualCakeEntitySet);
    }

    @Test
    public void testAddCake() {
        CakeEntity newCakeEntity = new CakeEntity("Layer cake", "A cake made of layers.", "http://roguecakes.co.uk/layercake.jpg");
        Mockito.when(cakeRepository.save(eq(newCakeEntity))).thenReturn(newCakeEntity);
        ResponseEntity<CakeEntity> responseEntity = cakesController.addCake(newCakeEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(newCakeEntity, responseEntity.getBody());
    }

    @Test
    public void testHappyUpdateCake() {
        final long id = 88;
        CakeEntity newCakeEntity = new CakeEntity("Layer cake", "A cake made of layers.", "http://roguecakes.co.uk/layercake.jpg");
        Mockito.when(cakeRepository.findById(eq(id))).thenReturn(Optional.of(newCakeEntity));
        Mockito.when(cakeRepository.save(eq(newCakeEntity))).thenReturn(newCakeEntity);
        ResponseEntity<CakeEntity> responseEntity = cakesController.updateCake(id, newCakeEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(newCakeEntity, responseEntity.getBody());
    }

    @Test
    public void testNotFoundUpdateCake() {
        final long id = 88;
        CakeEntity newCakeEntity = new CakeEntity("Layer cake", "A cake made of layers.", "http://roguecakes.co.uk/layercake.jpg");
        Mockito.when(cakeRepository.findById(eq(id))).thenReturn(Optional.empty());
        ResponseEntity<CakeEntity> responseEntity = cakesController.updateCake(id, newCakeEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteCake() {
        ResponseEntity<Void> responseEntity = cakesController.deleteCake(88L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
