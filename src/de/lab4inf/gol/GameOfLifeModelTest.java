package de.lab4inf.gol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameOfLifeModelTest {

    @org.junit.jupiter.api.Test
    void countLivingNeighbors() {
        GameOfLifeModel model = new GameOfLifeModel(5, 5);
        model.set(2, 2, true);
        model.set(0, 4, true);
        model.set(4, 4, true);
        assertEquals(0, model.countLivingNeighbors(2, 2));

        assertEquals(0, model.countLivingNeighbors(0, 0));

        assertEquals(1, model.countLivingNeighbors(0, 5));

        assertEquals(1, model.countLivingNeighbors(5, 5));

        assertEquals(0, model.countLivingNeighbors(5, 0));
    }

    @org.junit.jupiter.api.Test
    void countLivingNeighborsFullField() {
        GameOfLifeModel model = new GameOfLifeModel(3,3);
        model.set(0, 0, true);
        model.set(0,1,true);
        model.set(0,2,true);
        model.set(1,0,true);
        model.set(1,1,true);
        model.set(1,2,true);
        model.set(2,0,true);
        model.set(2,1,true);
        model.set(2,2,true);

        assertEquals(8, model.countLivingNeighbors(1, 1));
        assertEquals(3, model.countLivingNeighbors(0, 0));
        assertEquals(1, model.countLivingNeighbors(3, 3));
    }

    @org.junit.jupiter.api.Test
    void countLivingNeighborsEmptyField() {
        GameOfLifeModel model = new GameOfLifeModel(3,3);
        assertEquals(0, model.countLivingNeighbors(1, 1));
        assertEquals(0, model.countLivingNeighbors(0, 0));
        assertEquals(0, model.countLivingNeighbors(3, 3));
    }

    @Test
    void setDimensions() {
        GameOfLifeModel model = new GameOfLifeModel(5, 5);
        assertEquals(5, model.rows());
        assertEquals(5, model.columns());

        model.setDimensions(10, 10);
        assertEquals(10, model.rows());
        assertEquals(10, model.columns());

        model.setDimensions(1000, 1000);
        assertEquals(1000, model.rows());
        assertEquals(1000, model.columns());

        model.setDimensions(10000, 10000);
        assertEquals(10000, model.rows());
        assertEquals(10000, model.columns());

        model.setDimensions(1,1);
        assertEquals(1, model.rows());
        assertEquals(1, model.columns());

        assertThrows(IllegalArgumentException.class, () -> {
            model.setDimensions(-1, -1);
        });
    }

    @Test
    void setPattern() {
        GameOfLifeModel model = new GameOfLifeModel(5, 5);
        boolean[][] pattern = {
                {true, false, true},
                {false, true, false},
                {true, true, true}
        };
        model.setPattern(1, 1, pattern);

        assertTrue(model.get(1, 1));
        assertFalse(model.get(1, 2));
        assertFalse(model.get(2, 0));
        assertFalse(model.get(0, 0));
    }

    @Test
    void setPatternOutOfBounds() {
        GameOfLifeModel model = new GameOfLifeModel(5, 5);
        boolean[][] pattern = {
                {true, false, true},
                {false, true, false},
                {true, true, true}
        };

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            model.setPattern(4, 4, pattern);
        });
    }
}