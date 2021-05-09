package Model;
import java.util.*;

public class GoldMiner extends Position
{
	/**
	 * Cave to be searched by miner
	 */
	private final Cave cave; 
	/**
	 * Set of directions that the miner will use and analyze per move
	 */
	private ArrayList<Character> directions; 
	/**
	 * Current direction of the miner being analyzed
	 */
	private char direction; 
	/**
	 * Target direction of the miner for tracking
	 */
	private char curDirection; 
	/**
	 * Target tile to be reached by miner during search
	 */
	private Tile target; 
	/**
	 * Object currently found during the scanning process
	 */
	private Tile found; 
	/**
	 * Tiles already visited by miner
	 */
	private ArrayList<Tile> visited; 
	/**
	 * Stack for backtracking
	 */
	private Stack<Tile> backtrack; 
	/**
	 * Signal for backtracking
	 */
	private boolean bTrack; 
	/**
	 * To end search immediately if necessary (smart level only)
	 */
	private boolean end; 

	/* COUNTER VARIABLES */
	/**
	 * Movement counter
	 */
	private int moveCtr; 
	/**
	 * Rotation counter
	 */
	private int rotateCtr; 
	/**
	 * Scan counter
	 */
	private int scanCtr; 

	public GoldMiner (Cave cave) {
		// miner will start at top left of the board
		super (0, 0); 
		// assign cave to be searched by miner 
		this.cave = cave; 
		// default direction of miner movement
		direction = curDirection = 'R'; 
		// no object found yet
		target = found = null; 
		// initialize list of visited tiles
		visited = new ArrayList<> (); 
		// initialize backtrack stack list
		backtrack = new Stack<> (); 
		// initialize backtrack control variables
		bTrack = end = false; 
		// initialize counters to zero (0)
		moveCtr = rotateCtr = scanCtr = 0; 
	}

	/**
	 * Changes the miner agent's direction to a valid one. A direction is valid for
	 * an agent if it will not face a wall upon rotating (wall = edge of cave).
	 */
	public void randomRotate () {
		Random rand = new Random ();

		/* ROTATION SET OF MINER AGENT */
		directions = new ArrayList<>();

		directions.add ('U'); // UP
		directions.add ('L'); // LEFT
		directions.add ('D'); // DOWN
		directions.add ('R'); // RIGHT

		/* REMOVE MINER'S CURRENT DIRECTION (MINER CAN'T ROTATE TO CURRENT DIRECTION) */
		directions.remove (directions.indexOf (getDirection ()));

		/* ROTATE MINER RANDOMLY */
		rotate (directions.get (rand.nextInt (directions.size ())));
	}

	/**
	 * To get the location (Tile class instance) of the Gold tile with the given distance (> 0)
	 * of the beacon landed on by the miner.
	 * 
	 * @param 	distance	the distance value given by the beacon to get the location of 
	 * 						the gold tile from all possible directions
	 */
	public void setGoldTarget(int distance) {
		boolean safe = true;
		// get gold tile location based on distance given by beacon
		// check above the miner and beacon
		if (row - distance >= 0) {
			if (cave.getTile(row - distance, col) instanceof Gold) {
				for (int k = 1; k < distance; k++)
					if (cave.getTile(row - k, col) instanceof Pit)
						safe = false;
				if (safe) {
					target = cave.getTile(row - distance, col);
					curDirection = 'U';
				}
			}
		}
		// check below the miner and beacon
		if (row + distance <= cave.getTiles().length - 1) {
			if (cave.getTile(row + distance, col) instanceof Gold) {
				for (int k = 1; k < distance; k++)
					if (cave.getTile(row + k, col) instanceof Pit)
						safe = false;
				if (safe) {
					target = cave.getTile(row + distance, col);
					curDirection = 'D';
				}
			}
		}
		// check to the left of miner and beacon
		if (col - distance >= 0) {
			if (cave.getTile(row, col - distance) instanceof Gold) {
				for (int k = 1; k < distance; k++)
					if (cave.getTile(row, col - k) instanceof Pit)
						safe = false;
				if (safe) {
					target = cave.getTile(row, col - distance);
					curDirection = 'L';
				}
			}
		}
		// check to the right of miner and beacon
		if (col + distance <= cave.getTiles().length - 1) {
			if (cave.getTile(row, col + distance) instanceof Gold) {
				for (int k = 1; k < distance; k++)
					if (cave.getTile(row, col + k) instanceof Pit)
						safe = false;
				if (safe) {
					target = cave.getTile(row, col + distance);
					curDirection = 'R';
				}
			}
		}
	}

	/**
	 * For setting the valid directions to be analyzed based on miner's current position
	 */
	public void setDirections() {
		directions = new ArrayList<>();
		if (row != 0) {
			// check if unvisited
			if (!visited.contains(cave.getTile(row - 1, col))) {
				// System.out.println("Up not yet visited");
				directions.add('U');
			}
		}
		// DOWN
		if (row != cave.getTiles().length - 1) {
			if (!visited.contains(cave.getTile(row + 1, col))) {
				// System.out.println("Down not yet visited");
				directions.add('D');
			}
		}
		// LEFT
		if (col != 0) {
			if (!visited.contains(cave.getTile(row, col - 1))) {
				// System.out.println("Left not yet visited");
				directions.add('L');
			}
		}
		// RIGHT
		if (col != cave.getTiles().length - 1) {
			if (!visited.contains(cave.getTile(row, col + 1))) {
				// System.out.println("Right not yet visited");
				directions.add('R');
			}
		}
	}

	/**
	 * Performs the scan action of the miner on the current direction's corresponding tile
	 * to check for an object
	 * 
	 * @return	the object found on the current direction's corresponding tile
	 */
	public Tile scan() {
		// increment scan counter
		scanCtr++; 

		// temporary row and col values for corresponding tile checking
		int tRow = row;
		int tCol = col;

		switch (direction) {
		case 'U': 
			if (tRow - 1 != -1)
				tRow--; 
			break;
		case 'D': 
			if (tRow + 1 != cave.getTiles().length)
				tRow++; 
			break;
		case 'L': 
			if (tCol - 1 != -1)
				tCol--; 
			break;
		case 'R': 
			if (tCol + 1 != cave.getTiles().length)
				tCol++; 
		}

		if (cave.getTile (tRow, tCol) instanceof Gold ||
			cave.getTile (tRow, tCol) instanceof Beacon ||
			cave.getTile (tRow, tCol) instanceof Pit)
			return cave.getTile (tRow, tCol);
		else return null;
	}

	/**
	 * Performs the move action of the miner on the current direction
	 */
	public void move () {
		switch (direction) {
			case 'U': if (row != 0) row--; break;
			case 'D': if (row != cave.getTiles ().length - 1) row++; break;
			case 'L': if (col != 0) col--; break;
			case 'R': if (col != cave.getTiles ().length - 1) col++;
		}
		moveCtr++;
	}

	/**
	 * Performs the rotate action of the miner on the chosen direction
	 * 
	 * @param 	direction	the direction that the miner will rotate to
	 */
	public void rotate (char direction) {
		this.direction = direction;
		rotateCtr++;
	}

	/**
	 * Gets the current direction of the miner
	 * 
	 * @return	the current direction of the miner
	 */
	public char getDirection () {
		return direction;
	}

	/**
	 * Gets the current movement count from the search
	 * 
	 * @return	the movement count value
	 */
	public int getMoveCtr () {
		return moveCtr;
	}

	/**
	 * Gets the current rotation count from the search
	 * 
	 * @return	the rotation count value
	 */
	public int getRotateCtr () {
		return rotateCtr;
	}

	/**
	 * Gets the current scan count from the search
	 * 
	 * @return	the scan count value
	 */
	public int getScanCtr () {
		return scanCtr;
	}

	/**
	 * Gets the current cave instance being searched by the miner
	 * 
	 * @return	the Cave instance
	 */
	public Cave getCave () {
		return cave;
	}

	/**
	 * Gets the size of the current cave instance
	 * 
	 * @return	the size of the Cave instance
	 */
	public int getCaveSize () {
		return cave.getTiles ().length;
	}

	/**
	 * Gets the current position (Tile instance) of the miner
	 * 
	 * @return	the Tile instance which is the current position of the miner
	 */
	public Tile getCurPosition () {
		return cave.getTile (row, col);
	}

	/**
	 * Sets the target direction of the miner
	 */
	public void setCurDirection () {
		curDirection = direction;
	}

	/**
	 * Gets the target direction of the miner
	 * 
	 * @return	the target direction
	 */
	public char getCurDirection() {
		return curDirection;
	}

	/**
	 * Gets the list of directions to be analyzed based on current position
	 * 
	 * @return	the ArrayList of directions to be analyzed
	 */
	public ArrayList<Character> getDirections () {
		return directions;
	}

	/**
	 * Adds the current position of the miner to the backtrack stack
	 */
	public void addToBacktrack () {
		backtrack.push (cave.getTile (row, col));
	}

	/**
	 * Performs the backtracking of the miner
	 */
	public void backtrackMiner () {
		Tile temp = backtrack.pop ();
		row = temp.getRow();
		col = temp.getCol();
		moveCtr++; // increment move counter as per backtracking
	}

	/**
	 * Gets the stack of Tile instances for backtracking
	 * 
	 * @return	the backtrack stack instance
	 */
	public Stack<Tile> getBacktracks() {
		return backtrack;
	}

	/**
	 * To signal the miner to backtrack as necessary
	 */
	public void setBacktrack () {
		bTrack = true;
	}

	/**
	 * To reset the signal for backtracking
	 */
	public void resetBacktrack () {
		bTrack = false;
	}

	/**
	 * Gets the backtrack signal to determine if backtrack is needed or not
	 * 
	 * @return	the boolean value indicating if backtrack is needed
	 */
	public boolean isBacktrack() {
		return bTrack;
	}

	/**
	 * Adds the current position of the miner to the list of visited tiles
	 */
	public void addToVisited () {
		visited.add (cave.getTile (row, col));
	}

	/**
	 * Gets the list of visited tiles
	 * 
	 * @return	the ArrayList of visited tile instances
	 */
	public ArrayList<Tile> getVisited() {
		return visited;
	}

	/**
	 * Sets the target object (Tile instance) of the miner
	 * 
	 * @param 	t	the Tile instance to be targeted
	 */
	public void setTarget (Tile t) {
		target = t;
	}

	/**
	 * Gets the target object of the miner
	 * 
	 * @return	the target object
	 */
	public Tile getTarget() {
		return target;
	}

	/**
	 * Clears the target object of the miner; makes miner have no object targeted
	 */
	public void clearTarget () {
		target = null;
	}

	/**
	 * Sets the current object found during the scan process
	 * 
	 * @param 	t	the Tile instance currently found from the scan process
	 */
	public void setFound (Tile t) {
		found = t;
	}

	/**
	 * Gets the current object found from the scan process
	 * 
	 * @return	the Tile instance found
	 */
	public Tile getFound() {
		return found;
	}

	/**
	 * Clears the found object from the scanning process
	 */
	public void clearFound () {
		found = null;
	}
	
	/**
	 * Signals the search process to end immediately
	 */
	public void endSearch() {
		end = true;
	}

	/**
	 * Gets the signal to determine if search process will end or not
	 * 
	 * @return	the boolean value indicating if the search process will end
	 */
	public boolean isEnd() {
		return end;
	}
}
