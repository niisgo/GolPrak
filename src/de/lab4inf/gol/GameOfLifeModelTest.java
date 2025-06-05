package de.lab4inf.gol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class GameOfLifeModelTest {

    private GameOfLifeModel model;

    @BeforeEach
    void setUp() {
        model = new GameOfLifeModel(5, 5);
    }

    //========================================
    // Dimensions: rows() and columns()
    //========================================

    @Nested
    class DimensionTests {

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

        @Test
        void testSetDimensionsValid() {
            model.set(1, 1, true);
            model.setDimensions(2, 3);
            assertEquals(2, model.rows());
            assertEquals(3, model.columns());
            assertFalse(model.isAlive(), "All cells should be cleared after dimensions change");
        }

        @Test
        void testSetDimensionsResetsGeneration() {
            model.nextGeneration();
            assertEquals(1, model.getGeneration());
            model.setDimensions(4, 4);
            assertEquals(0, model.getGeneration(), "Generation should be reset after dimension change");
        }

        @Test
        void testSetDimensionsThrowsOnInvalid() {
            assertThrows(IllegalArgumentException.class, () -> model.setDimensions(0, 5));
            assertThrows(IllegalArgumentException.class, () -> model.setDimensions(5, 0));
        }
    }

    //========================================
    // Get & Set individual cells
    //========================================

    @Nested
    class GetSetTests {

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

        @Test
        void testGetThrowsExceptionOnInvalidIndices() {
            assertThrows(IndexOutOfBoundsException.class, () -> model.get(-1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> model.get(0, 5));
        }

        @Test
        void testSetThrowsExceptionOnInvalidIndices() {
            assertThrows(IndexOutOfBoundsException.class, () -> model.set(-1, 0, true));
            assertThrows(IndexOutOfBoundsException.class, () -> model.set(0, 5, false));
        }
    }

    //========================================
    // isAlive()
    //========================================

    @Nested
    class IsAliveTests {

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
    }

    //========================================
    // setPattern()
    //========================================

    @Nested
    class SetPatternTests {

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
            assertFalse(model.get(3, 4));
            assertFalse(model.get(4, 3));
        }

        @Test
        void testSetPatternWithNoTrueCells() {
            boolean[][] pattern = {{false, false}, {false, false}};
            model.setPattern(2, 2, pattern);
            assertFalse(model.isAlive(), "No cells should be set alive when pattern contains only false");
        }

        @Test
        void testSetPatternThrowsOnNull() {
            assertThrows(IllegalArgumentException.class, () -> model.setPattern(1, 1, null));
        }
    }

    //========================================
    // countLivingNeighbors()
    //========================================

    @Nested
    class CountLivingNeighborsTests {

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

        @Test
        void testCountLivingNeighborsThrowsOnInvalidIndices() {
            assertThrows(IndexOutOfBoundsException.class, () -> model.countLivingNeighbors(-1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> model.countLivingNeighbors(0, 5));
        }
    }

    //========================================
    // nextGeneration() and getGeneration()
    //========================================

    @Nested
    class GenerationTests {

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

        @Test
        void testNextGenerationEmptyStaysEmpty() {
            model.nextGeneration();
            assertFalse(model.isAlive(), "Empty model should remain empty");
        }

        @Test
        void testNextGenerationBlockStable() {
            boolean[][] block = {{true, true}, {true, true}};
            model.setPattern(1, 1, block);
            model.nextGeneration();
            assertTrue(model.get(1, 1));
            assertTrue(model.get(1, 2));
            assertTrue(model.get(2, 1));
            assertTrue(model.get(2, 2));
        }

        @Test
        void testNextGenerationBlinkerOscillation() {
            boolean[][] blinker = {{true, true, true}};
            model.setPattern(2, 1, blinker);
            model.nextGeneration();
            assertTrue(model.get(1, 2));
            assertTrue(model.get(2, 2));
            assertTrue(model.get(3, 2));
            assertFalse(model.get(2, 1));
            assertFalse(model.get(2, 3));
        }
    }

    //========================================
    // Observer & Notification Tests
    //========================================

    @Nested
    class ObserverTests {

        //---- generationChanged notifications ----

        @Nested
        class GenerationNotificationTests {

            @Test
            void testAddAndNotifyObserver() {
                AtomicBoolean called = new AtomicBoolean(false);

                GameOfLifeListener listener = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {
                        called.set(true);
                    }
                    @Override
                    public void dimensionChanged() {}
                };

                model.addObserver(listener);
                model.nextGeneration();
                assertTrue(called.get(), "Observer should be notified on generation change");
            }

            @Test
            void testAddMultipleObserversGenerationNotification() {
                AtomicBoolean called1 = new AtomicBoolean(false);
                AtomicBoolean called2 = new AtomicBoolean(false);

                GameOfLifeListener l1 = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {
                        called1.set(true);
                    }
                    @Override
                    public void dimensionChanged() {}
                };
                GameOfLifeListener l2 = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {
                        called2.set(true);
                    }
                    @Override
                    public void dimensionChanged() {}
                };

                model.addObserver(l1);
                model.addObserver(l2);
                model.nextGeneration();

                assertTrue(called1.get(), "First observer should be notified on generation change");
                assertTrue(called2.get(), "Second observer should be notified on generation change");
            }

            @Test
            void testRemoveOneObserverGeneration() {
                AtomicBoolean called1 = new AtomicBoolean(false);
                AtomicBoolean called2 = new AtomicBoolean(false);

                GameOfLifeListener l1 = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {
                        called1.set(true);
                    }
                    @Override
                    public void dimensionChanged() {}
                };
                GameOfLifeListener l2 = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {
                        called2.set(true);
                    }
                    @Override
                    public void dimensionChanged() {}
                };

                model.addObserver(l1);
                model.addObserver(l2);
                model.removeObserver(l1);
                model.nextGeneration();

                assertFalse(called1.get(), "Removed observer should not be notified on generation change");
                assertTrue(called2.get(), "Remaining observer should be notified on generation change");
            }

            @Test
            void testNotifyGenerationWithoutObservers() {
                assertDoesNotThrow(() -> model.notifyGenerationChanged(),
                        "notifyGenerationChanged should not throw when no observers are registered");
            }
        }

        //---- dimensionChanged notifications ----

        @Nested
        class DimensionNotificationTests {

            @Test
            void testDimensionChangedNotification() {
                AtomicBoolean called = new AtomicBoolean(false);

                GameOfLifeListener listener = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {}
                    @Override
                    public void dimensionChanged() {
                        called.set(true);
                    }
                };

                model.addObserver(listener);
                model.setDimensions(6, 6);
                model.notifyDimensionChanged();  // in case setDimensions doesn't call it automatically
                assertTrue(called.get(), "Observer should be notified on dimension change");
            }

            @Test
            void testAddMultipleObserversDimensionNotification() {
                AtomicBoolean called1 = new AtomicBoolean(false);
                AtomicBoolean called2 = new AtomicBoolean(false);

                GameOfLifeListener l1 = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {}
                    @Override
                    public void dimensionChanged() {
                        called1.set(true);
                    }
                };
                GameOfLifeListener l2 = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {}
                    @Override
                    public void dimensionChanged() {
                        called2.set(true);
                    }
                };

                model.addObserver(l1);
                model.addObserver(l2);
                model.setDimensions(2, 2);
                model.notifyDimensionChanged();

                assertTrue(called1.get(), "First observer should be notified on dimension change");
                assertTrue(called2.get(), "Second observer should be notified on dimension change");
            }

            @Test
            void testRemoveOneObserverDimension() {
                AtomicBoolean called1 = new AtomicBoolean(false);
                AtomicBoolean called2 = new AtomicBoolean(false);

                GameOfLifeListener l1 = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {}
                    @Override
                    public void dimensionChanged() {
                        called1.set(true);
                    }
                };
                GameOfLifeListener l2 = new GameOfLifeListener() {
                    @Override
                    public void generationChanged() {}
                    @Override
                    public void dimensionChanged() {
                        called2.set(true);
                    }
                };

                model.addObserver(l1);
                model.addObserver(l2);
                model.removeObserver(l2);
                model.setDimensions(3, 3);
                model.notifyDimensionChanged();

                assertTrue(called1.get(), "Remaining observer should be notified on dimension change");
                assertFalse(called2.get(), "Removed observer should not be notified on dimension change");
            }

            @Test
            void testNotifyDimensionWithoutObservers() {
                assertDoesNotThrow(() -> model.notifyDimensionChanged(),
                        "notifyDimensionChanged should not throw when no observers are registered");
            }
        }
    }
}
