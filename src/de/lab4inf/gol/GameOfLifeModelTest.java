package de.lab4inf.gol;

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
}