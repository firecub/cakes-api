package com.example.cakes;

import com.example.cakes.repository.CakeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SmokeTest {
    @Autowired
    private CakeRepository cakeRepository;

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(cakeRepository);
    }
}
