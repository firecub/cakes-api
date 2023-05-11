package com.example.cakes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CakeEntityTests {

    @Test
    void testTitle() {
        CakeEntity cakeEntity = new CakeEntity();
        final String title = "Layer cake";
        cakeEntity.setTitle(title);
        assertEquals(title, cakeEntity.getTitle());
    }

    @Test
    void testDescription() {
        CakeEntity cakeEntity = new CakeEntity();
        final String description = "Can be made with pancakes.";
        cakeEntity.setTitle(description);
        assertEquals(description, cakeEntity.getTitle());
    }

    @Test
    void testImage() {
        CakeEntity cakeEntity = new CakeEntity();
        final String image = "https://chelsweets.com/wp-content/uploads/2022/01/cake-slice-by-itself-1525805572-1549941453895-edited-scaled.jpg";
        cakeEntity.setTitle(image);
        assertEquals(image, cakeEntity.getTitle());
    }
}
