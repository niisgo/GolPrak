package de.lab4inf.gol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class GameOfLifeModelTest {
    private GameOfLifeModel model;

    @BeforeEach
    void setUp() {
        // Initialize a 5x5 model before each test
        model = new GameOfLifeModel(5, 5);
    }

    // Tests for rows() and columns()
    @Test
    void testInitialDimensions() {
        assertEquals(5, model.rows(), "rows() should return initial number of rows");
        assertEquals(5, model.columns(), "columns() should return initial number of columns");
    }

    @Test
    void testDimensionsAfterSetDimensions() {
        model.setDimensions(3, 7);
        assertEquals(3, model.rows(), "rows() should reflect new dimensions");
        assertEquals(7, model.columns(), "columns() should reflect new dimensions");
    }

    @Test
    void testDimensionsUnchangedByNextGeneration() {
        model.nextGeneration();
        assertEquals(5, model.rows(), "rows() should remain unchanged after nextGeneration");
        assertEquals(5, model.columns(), "columns() should remain unchanged after nextGeneration");
    }

    // Tests for get() and set()
    @Test
    void testSetAndGetSingleCell() {
        assertFalse(model.get(2, 3), "Cell should be dead by default");
        model.set(2, 3, true);
        assertTrue(model.get(2, 3), "Cell should be alive after set to true");
    }

    @Test
    void testSetMultipleCells() {
        model.set(0, 0, true);
        model.set(4, 4, true);
        assertTrue(model.get(0, 0));
        assertTrue(model.get(4, 4));
    }

    @Test
    void testOverrideCellState() {
        model.set(1, 1, true);
        assertTrue(model.get(1, 1));
        model.set(1, 1, false);
        assertFalse(model.get(1, 1));
    }

    // Tests for isAlive()
    @Test
    void testIsAliveInitiallyFalse() {
        assertFalse(model.isAlive(), "New model should have no living cells");
    }

    @Test
    void testIsAliveAfterSettingACell() {
        model.set(2, 2, true);
        assertTrue(model.isAlive(), "Model should be alive when at least one cell is alive");
    }

    @Test
    void testIsAliveAfterClearWithSetDimensions() {
        model.set(2, 2, true);
        model.setDimensions(5, 5);
        assertFalse(model.isAlive(), "Model should be dead after dimensions reset");
    }

    // Tests for setPattern()
    @Test
    void testSetPatternFullyInside() {
        boolean[][] pattern = {{true, false}, {false, true}};
        model.setPattern(1, 1, pattern);
        assertTrue(model.get(1, 1));
        assertFalse(model.get(1, 2));
        assertFalse(model.get(2, 1));
        assertTrue(model.get(2, 2));
    }

    @Test
    void testSetPatternPartiallyOutside() {
        boolean[][] pattern = {{true, true}, {true, true}};
        model.setPattern(4, 4, pattern); // bottom-right corner, only (4,4) inside
        assertTrue(model.get(4, 4));
        // All other target positions should remain false
        assertFalse(model.get(3, 4));
        assertFalse(model.get(4, 3));
    }

    @Test
    void testSetPatternWithNoTrueCells() {
        boolean[][] pattern = {{false, false}, {false, false}};
        model.setPattern(2, 2, pattern);
        assertFalse(model.isAlive(), "No cells should be set alive when pattern contains only false");
    }

    // Tests for getGeneration()
    @Test
    void testGenerationInitiallyZero() {
        assertEquals(0, model.getGeneration(), "Generation should start at 0");
    }

    @Test
    void testGenerationIncrements() {
        model.nextGeneration();
        assertEquals(1, model.getGeneration(), "Generation should increment by 1 after nextGeneration");
        model.nextGeneration();
        assertEquals(2, model.getGeneration(), "Generation should increment again after nextGeneration");
    }

    @Test
    void testGenerationResetAfterSetDimensions() {
        model.nextGeneration();
        assertEquals(1, model.getGeneration());
        model.setDimensions(5, 5);
        assertEquals(0, model.getGeneration(), "Generation should reset to 0 after dimensions change");
    }

    // Tests for nextGeneration() using known patterns
    @Test
    void testNextGenerationEmptyStaysEmpty() {
        model.nextGeneration();
        assertFalse(model.isAlive(), "Empty model should remain empty");
    }

    @Test
    void testNextGenerationBlockStable() {
        // Block pattern
        boolean[][] block = {{true, true}, {true, true}};
        model.setPattern(1, 1, block);
        model.nextGeneration();
        // Block remains in same place
        assertTrue(model.get(1, 1));
        assertTrue(model.get(1, 2));
        assertTrue(model.get(2, 1));
        assertTrue(model.get(2, 2));
    }

    @Test
    void testNextGenerationBlinkerOscillation() {
        // Horizontal blinker
        boolean[][] blinker = {{true, true, true}};
        model.setPattern(2, 1, blinker);
        model.nextGeneration();
        // Now should be vertical
        assertTrue(model.get(1, 2));
        assertTrue(model.get(2, 2));
        assertTrue(model.get(3, 2));
        assertFalse(model.get(2, 1));
        assertFalse(model.get(2, 3));
    }

    // Tests for countLivingNeighbors()
    @Test
    void testCountLivingNeighborsCenter() {
        model.set(2, 1, true);
        model.set(2, 3, true);
        model.set(1, 2, true);
        model.set(3, 2, true);
        assertEquals(4, model.countLivingNeighbors(2, 2));
    }

    @Test
    void testCountLivingNeighborsCorner() {
        model.set(0, 1, true);
        assertEquals(1, model.countLivingNeighbors(0, 0));
    }

    @Test
    void testCountLivingNeighborsNone() {
        assertEquals(0, model.countLivingNeighbors(4, 4));
    }

    // Tests for setDimensions()
    @Test
    void testSetDimensionsValid() {
        model.set(1, 1, true);
        model.setDimensions(2, 3);
        assertEquals(2, model.rows());
        assertEquals(3, model.columns());
        assertFalse(model.isAlive(), "All cells should be cleared after dimensions change");
    }

    @Test
    void testSetDimensionsThrowsOnInvalid() {
        assertThrows(IllegalArgumentException.class, () -> model.setDimensions(0, 5));
        assertThrows(IllegalArgumentException.class, () -> model.setDimensions(5, 0));
    }

    @Test
    void testSetDimensionsResetsGeneration() {
        model.nextGeneration();
        assertEquals(1, model.getGeneration());
        model.setDimensions(4, 4);
        assertEquals(0, model.getGeneration(), "Generation should be reset after dimension change");
    }


    @Test
    void testAddAndNotifyObserver() {
        AtomicBoolean called = new AtomicBoolean(false);

        GameOfLifeListener listener = new GameOfLifeListener() {
            @Override
            public void generationChanged() {
                called.set(true);
            }

            @Override
            public void dimensionChanged() {
            }
        };

        model.addObserver(listener);
        model.nextGeneration();
        assertTrue(called.get());
    }

    @Test
    void testRemoveObserver() {
        AtomicBoolean called = new AtomicBoolean(false);

        GameOfLifeListener listener = new GameOfLifeListener() {
            @Override
            public void generationChanged() {
                called.set(true);
            }

            @Override
            public void dimensionChanged() {
            }
        };

        model.addObserver(listener);
        model.removeObserver(listener);
        model.nextGeneration();
        assertFalse(called.get());
    }

    @Test
    void testDimensionChangedNotification() {
        AtomicBoolean called = new AtomicBoolean(false);

        GameOfLifeListener listener = new GameOfLifeListener() {
            @Override
            public void generationChanged() {
            }

            @Override
            public void dimensionChanged() {
                called.set(true);
            }
        };

        model.addObserver(listener);
        model.setDimensions(6, 6);
        model.notifyDimensionChanged();  // falls setDimensions das nicht automatisch macht
        assertTrue(called.get());
    }
}
