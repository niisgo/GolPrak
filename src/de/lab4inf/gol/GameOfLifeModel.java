package de.lab4inf.gol;

public class GameOfLifeModel {
	// state of the actualState
	private boolean[][] actualState;
	// actual generation counter
	private int generation;

	/**
	 * Constructor for a GoL model instance.
	 * @param n the number of rows
	 * @param m the number of columns
	 */
	public GameOfLifeModel(int n, int m) {
		if (n < 1 || m < 1) {
			System.err.printf("n: %d, m: %d illegal values\n", n, m);
			System.exit(-1);
		}
		generation = 0;
		actualState = new boolean[n][m];
	}

	/**
	 * Number of columns of this GoL.
	 * @return number of columns
	 */
	public int columns() {
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
		return actualState[j][k];
	}

	/**
	 * Set the internal state of cell (j,k).
	 * @param j row index
	 * @param k column index
	 * @param value to set
	 */
	public void set(int j, int k, boolean value) {
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
		for (int i = 0; i < pattern.length; i++) {
			for (int j = 0; j < pattern[0].length; j++) {
				set(r + i, c + j, pattern[i][j]);
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
		int rows = actualState.length;
		int cols = actualState[0].length;
		boolean[][] next = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
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
	}

	// Count living neighbors
	public int countLivingNeighbors(int row, int col) {
		int count = 0;
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) continue;
				if (i >= 0 && i < actualState.length && j >= 0 && j < actualState[0].length) {
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
		actualState = new boolean[n][m];
		generation = 0;
	}
}