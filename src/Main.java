/*
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
 * Bootstrap Main class with a main method for the GoL initialization.
 * @author nwulff
 *
 */
public class Main {
	/**
	 * main method to start with.
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		// local variables for the rows and columns etc of the game.
		int nRows= 9,mCols=9, xPos=3, yPos=3, maxGeneration=8;
		GameOfLifeModel model = new GameOfLifeModel(nRows,mCols);
		// initialize a pattern for the zero GoL generation.
		boolean[][] startPattern = patternFromCommandLine(args);
		model.setPattern(xPos,yPos, startPattern);
		// play game as long as cells are alive.
		do {
			showModel(model);
			model.nextGeneration();
		} while(model.isAlive() && model.getGeneration() < maxGeneration);
	}
	
    /**
     * Simple print-out method of the GoL model as long 
     * as we don't have a valid GUI via view class.
     * @param model to print
     */
	public static void showModel(GameOfLifeModel model) {
		int rows = model.rows(), cols = model.columns();
		System.out.printf("+");
		for(int j=0;j<cols;j++) {
			System.out.printf("-");
		}
		System.out.printf("+\n");
		for(int j=0;j<rows;j++) {
			System.out.printf("|");
			for(int k=0;k<cols;k++) {
				System.out.printf("%c",model.get(j, k) ? 'o': ' ');
			}
			System.out.printf("|\n");
		}
		System.out.printf("+");
		for(int j=0;j<cols;j++) {
			System.out.printf("-");
		}
		System.out.printf("+\nGeneration: %d \n",model.getGeneration());
	}
	
	/**
	 * Internal helper method to produce the GoL starting pattern.
	 * @param args of the command-line if any
	 * @return a GoF pattern for the initial state
	 */
	public static boolean[][] patternFromCommandLine(String[] args) {
		boolean[][] startPattern = crossPattern();
		if(args.length>0) {
			String cmd = args[0];
			if("-1".equals(cmd)) {
				startPattern = gliderPattern();
			} else if("-2".equals(cmd)) {
				startPattern = blinkerPattern();
			} else if("-3".equals(cmd)) {
				startPattern = worstPattern();
			}
		}
		return startPattern;
	}
	
    /**
     * Factory method for a glider pattern.
     * @return pattern
     */
	public static boolean[][] gliderPattern() {
		boolean[][] pattern = new boolean[3][3]; 
		//{{false,true,false},{false,false,true},{true,true,true}};
		pattern[0][1] = true;
		pattern[1][2] = true;
		pattern[2][0] = true;
		pattern[2][1] = true;
		pattern[2][2] = true;
		return pattern;
	}
	
    /**
     * Factory method for a periodic blinker pattern.
     * @return pattern
     */
	public static boolean[][] blinkerPattern() {
		boolean[][] pattern = {{true,true,true}};
		return pattern;
	}
	
    /**
     * Factory method for two crossed blinker.
     * @return pattern
     */
	public static boolean[][] crossPattern() {
		boolean[][] pattern = {{false,true,false},{true,true,true},{false,true,false}};
		return pattern;
	}

	/**
	 * Factory method the worst Pattern ever.
	 * @return pattern
	 */
	public static boolean[][] worstPattern() {
		boolean[][] pattern = {{false,true,false},{true,false,true},{true,false,true},{false,true,false}};


		return pattern;
	}


}
