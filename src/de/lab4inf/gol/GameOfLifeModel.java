package de.lab4inf.gol;/*
 * Project: GoL2025
 *
 * Copyright (c) 2004-2025,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * Game of Life model class holding the internal state of a NxM GoL field.
 * @author nwulff
 *
 */
public class GameOfLifeModel {
	// state of the  actualState
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
			System.err.printf("n: %d, m: %d illegal values\n",n,m);
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
		for (int i = 0; i < actualState.length; i++) {
			for (int j = 0; j < actualState[i].length; j++) {
				if (actualState[i][j]) {
					return true; // Mindestens eine Zelle lebt
				}
			}
		}
		return false; // Alle Zellen sind tot
	}
    /**
     * Set the given pattern at place (r,c).
     * @param r starting row
     * @param c starting column
     * @param pattern to set
     */
	public void setPattern(int r, int c, boolean[][] pattern) {
		int u, v;
		u = pattern.length;
		v = pattern[0].length;
		for (int pRow = 0; pRow < u; pRow++) {
			for (int pCol = 0; pCol < v; pCol++) {
				set(r + pRow, c + pCol, pattern[pRow][pCol]);
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
		boolean[][] next = new boolean[rows][cols]; // Neues Spielfeld

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int neighbors = countLivingNeighbors(i, j);

				if (actualState[i][j]) {
					// Regel: Überleben bei 2 oder 3 Nachbarn
					next[i][j] = (neighbors == 2 || neighbors == 3);
				} else {
					// Regel: Geburt bei genau 3 Nachbarn
					next[i][j] = (neighbors == 3);
				}
			}
		}

		generation++;
		actualState = next; // Neue Generation wird übernommen
	}

	// Zählt, wie viele lebende Nachbarn eine Zelle hat
	private int countLivingNeighbors(int row, int col) {
		int count = 0;
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (i == row && j == col) continue; // Eigene Zelle überspringen
				if (i >= 0 && i < actualState.length && j >= 0 && j < actualState[0].length) {
					if (actualState[i][j]) count++; // Nachbar lebt
				}
			}
		}
		return count;
	}
}
