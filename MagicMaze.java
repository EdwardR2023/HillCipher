/* Edward Rosales
 *  COP3503 Computer Science 2
 *  Programming Assignment 2
 *  Fall 2023
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class MagicMaze {
	
	
    private char[][] maze;
    private String mazeName;
    private int numRows;
    private int numCols;
    
    private HashMap<Character, HashSet<GridCoordinate>> teleporterMap = new HashMap<>();
    private Map<Character, Integer> teleporterUsageCount = new HashMap<>(); // Track teleporter usage count
    private Map<Character, GridCoordinate> portalCoordinates = new HashMap<>(); // Added portal coordinates map
    
    
    

    public MagicMaze(String fileName, int numRows, int numCols) {
        this.mazeName = fileName;
        this.numRows = numRows;
        this.numCols = numCols;
        this.maze = new char[numRows][numCols];
    }

    public void readMazeFromFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(mazeName))) {
			for (int i = 0; i < numRows; i++) {
			    String line = reader.readLine();
			        for (int j = 0; j < numCols; j++) {
			            maze[i][j] = line.charAt(j);
			            
			         // Check if the current position is a teleporter and add its coordinates to the map
			            if (Character.isDigit(maze[i][j])) {
			            	
			            	//the number at the current position is assigned to digit
	                        char digit = maze[i][j];
	                        GridCoordinate coordinate = new GridCoordinate(i, j);
	                        //inserts the current position of the teleport and it's number 
	                        portalCoordinates.put(digit, coordinate);
	                        teleporterMap.computeIfAbsent(digit, k -> new HashSet<>()).add(coordinate);
	                    }
			        }
			    
			    }
			
			reader.close();
		}
    }

    
    public boolean solveMagicMaze() throws IOException {
    	
    	boolean [][]visited = new boolean [numRows][numCols];
    	
    	//calls function to read in the file
    	readMazeFromFile();
    	
    	//starting pos
    	int  posRow = numRows-1, posCol = 0;
    	maze[posRow][posCol] = 'S';

    	//holds the result of the backtracking function
    	boolean res = backtrack(posRow, posCol, numRows, numCols, maze, visited);
    	
    	for(int i = 0; i < numRows; i++) {
    		for(int j = 0; j < numCols;j++) {
    			System.out.print(maze[i][j]);
    			}
    		System.out.println();
    		}
		 
    	
    	//returns the result of the backtracking function
		return res;
    }
    
    
    
    boolean backtrack(int posRow, int posCol, int numRows, int numCols, char maze[][],boolean visited[][]) {

    	// Mark the current cell as visited
        visited[posRow][posCol] = true;
        
      //end of the maze reached
    	if(maze[posRow][posCol] == 'X') { 
  		  //System.out.println("PASSED");
    		visited[posRow][posCol] = true;
    		return true;
    		
    	}
    	//need to check for teleporters
    	if (Character.isDigit(maze[posRow][posCol])) {
    	    char digit = maze[posRow][posCol];
    	    GridCoordinate otherEnd = getOtherEnd(digit, posRow, posCol);

    	    // Check the teleporter usage count
    	    int usageCount = teleporterUsageCount.getOrDefault(digit, 0);
    	    
    	    //usage is capped at 2 because ideally there is no need to go beyond
    	    if (usageCount < 2) {
    	        posRow = otherEnd.getX();
    	        posCol = otherEnd.getY();
    	        visited[posRow][posCol] = false;
    	        teleporterUsageCount.put(digit, usageCount + 1);
    	    } else {
    	        // Mark as visited if the teleporter has been used twice
    	        visited[posRow][posCol] = true;
    	    }
    	}

    	
    	
    	//each move gets a bounds check, obstacle check and checked to see if they have been visited
    	//move up
    	if (posRow - 1 >= 0 && maze[posRow - 1][posCol] != '@' && !visited[posRow - 1][posCol]) {
    	    if (backtrack(posRow - 1, posCol, numRows, numCols, maze, visited)) {
    	    	maze[posRow - 1][posCol] = 'U';
    	        return true;
    	    }
    	}
    	//move right
    	if (posCol + 1 < numCols && maze[posRow][posCol + 1] != '@' && !visited[posRow][posCol + 1]) {
    	    if (backtrack(posRow, posCol + 1, numRows, numCols, maze, visited)) {
    	    	maze[posRow][posCol+1] = 'R';
    	        return true;
    	    }
    	}
    	//move down
    	if (posRow + 1 < numRows && maze[posRow + 1][posCol] != '@' && !visited[posRow + 1][posCol]) {
    	    if (backtrack(posRow + 1, posCol, numRows, numCols, maze, visited)) {
    	    	maze[posRow+1][posCol] = 'D';
    	        return true;
    	    }
    	}
    	//move left
    	if (posCol - 1 >= 0 && maze[posRow][posCol - 1] != '@' && !visited[posRow][posCol - 1]) {
    	    if (backtrack(posRow, posCol - 1, numRows, numCols, maze, visited)) {
    	    	maze[posRow][posCol-1] = 'L';
    	        return true;
    	    }
    	}

    	// If none of the directions leads to the end, backtrack to the previous spot
    	visited[posRow][posCol] = false;
    	return false;
    }  
    
    
    //Define GridCoordinate as an inner class
    private class GridCoordinate {
        private int x;
        private int y;

        public GridCoordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    // Get the other end of the teleporter given its digit and current coordinates
    private GridCoordinate getOtherEnd(char digit, int currentX, int currentY) {
        HashSet<GridCoordinate> coordinatesSet = teleporterMap.get(digit);
        
        for (GridCoordinate coordinate : coordinatesSet) {
        	//ensures the coordinates that are retrieved are different from our current position
            if (!(coordinate.getX() == currentX && coordinate.getY() == currentY)) {
                return coordinate;
            }
        }
        return null;
    }
    
}
