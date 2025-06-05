package de.lab4inf.gol;

import java.util.ArrayList;

public class GameOfLifeModel {
	// state of the actualState
	private boolean[][] actualState;
	// actual generation counter
	private int generation;

	ArrayList<GameOfLifeListener> observer;

	/**
	 * Constructor for a GoL model instance.
	 * @param n the number of rows
	 * @param m the number of columns
	 */
	public GameOfLifeModel(int n, int m) {
		if (n < 1 || m < 1) {
			throw new IllegalArgumentException("n: " + n + ", m: " + m + " illegal values");
		}
		generation = 0;
		actualState = new boolean[n][m];

		observer = new ArrayList<>();
	}

	/**
	 * Number of columns of this GoL.
	 * @return number of columns
	 */
	public int columns() {
		// actualState always has at least one row (constructor enforces n >= 1)
		return actualState[0].length;
	}

	/**
	 * Number of rows of this GoL.
	 * @return number of rows
	 */
	public int rows() {
		return actualState.length;
	}

	/**
	 * Get the internal state of cell (j,k).
	 * @param j row index
	 * @param k column index
	 * @return state of the cell
	 */
	public boolean get(int j, int k) {
		if (j < 0 || j >= rows() || k < 0 || k >= columns()) {
			throw new IndexOutOfBoundsException("Cell indices out of range: (" + j + ", " + k + ")");
		}
		return actualState[j][k];
	}

	/**
	 * Set the internal state of cell (j,k).
	 * @param j row index
	 * @param k column index
	 * @param value to set
	 */
	public void set(int j, int k, boolean value) {
		if (j < 0 || j >= rows() || k < 0 || k >= columns()) {
			throw new IndexOutOfBoundsException("Cell indices out of range: (" + j + ", " + k + ")");
		}
		actualState[j][k] = value;
	}

	/**
	 * Indicate if this GoL is alive, i.e. there is any living cell.
	 * @return live indicator
	 */
	public boolean isAlive() {
		for (boolean[] row : actualState) {
			for (boolean cell : row) {
				if (cell) return true;
			}
		}
		return false;
	}

	/**
	 * Set the given pattern at place (r,c).
	 * @param r starting row
	 * @param c starting column
	 * @param pattern to set
	 */
	public void setPattern(int r, int c, boolean[][] pattern) {
		if (pattern == null || pattern.length == 0) {
			throw new IllegalArgumentException("Pattern must not be null or empty");
		}
		int maxRow = rows();
		int maxCol = columns();

		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i] == null) {
				continue;
			}
			for (int j = 0; j < pattern[i].length; j++) {
				if (!pattern[i][j]) {
					continue;
				}

				int targetRow = r + i;
				int targetCol = c + j;

				// Prüfen, ob (targetRow,targetCol) im Modell liegt
				if (targetRow >= 0 && targetRow < maxRow
						&& targetCol >= 0 && targetCol < maxCol) {
					actualState[targetRow][targetCol] = true;
				}
			}
		}
	}


	/**
	 * Get the actual generation counter.
	 * @return generation counter
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Calculate the next GoL generation.
	 */
	public void nextGeneration() {
		int numRows = rows();
		int numCols = columns();
		boolean[][] next = new boolean[numRows][numCols];
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				int neighbors = countLivingNeighbors(i, j);
				if (actualState[i][j]) {
					next[i][j] = (neighbors == 2 || neighbors == 3);
				} else {
					next[i][j] = (neighbors == 3);
				}
			}
		}
		generation++;
		actualState = next;

		notifyGenerationChanged();
	}

	// Count living neighbors
	public int countLivingNeighbors(int row, int col) {
		if (row < 0 || row >= rows() || col < 0 || col >= columns()) {
			throw new IndexOutOfBoundsException("Cell indices out of range: (" + row + ", " + col + ")");
		}
		int count = 0;
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) continue;
				if (i >= 0 && i < rows() && j >= 0 && j < columns()) {
					if (actualState[i][j]) count++;
				}
			}
		}
		return count;
	}

	/**
	 * Set new dimensions, clearing the field and resetting generation.
	 * @param n new number of rows
	 * @param m new number of columns
	 */
	public void setDimensions(int n, int m) {
		if (n < 1 || m < 1) {
			throw new IllegalArgumentException("Dimensions must be >= 1: received " + n + "x" + m);
		}
		int oldRows = rows();
		int oldCols = columns();
		boolean[][] newState = new boolean[n][m];
		int minRows = Math.min(oldRows, n);
		int minCols = Math.min(oldCols, m);
		for (int i = 0; i < minRows; i++) {
			System.arraycopy(actualState[i], 0, newState[i], 0, minCols);
		}
		actualState = newState;
		generation = 0;
		notifyDimensionChanged();
	}

	public void addObserver(GameOfLifeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener must not be null");
		}
		observer.add(listener);
	}

	public void removeObserver(GameOfLifeListener listener) {
		if (listener == null) {
			return;
		}
		observer.remove(listener);
	}

	public void notifyGenerationChanged() {
		for (GameOfLifeListener listener : observer) {
			listener.generationChanged();
		}
	}
	public void notifyDimensionChanged() {
		for (GameOfLifeListener listener : observer) {
			listener.dimensionChanged();
		}
	}
}