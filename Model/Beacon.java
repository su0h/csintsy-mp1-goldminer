package Model;

public class Beacon extends Tile 
{
  /**
   * The value indicating the distance of this beacon from the gold
   */
	private int distance;

  /**
   * Constructor. Creates this beacon instance
   * 
   * @param   row   the row position of this beacon on the board
   * @param   col   the column position of this beacon on the board
   */
	public Beacon (int row, int col) {
		super (row, col);
	}

  /**
   * Gets the distance of the beacon from the gold
   * 
   * @return  the distance value
   */
	public int getDistance () {
		return distance;
	}

  /**
   * Sets the distance of the beacon from the gold
   * 
   * @param   distance  the distance of the beacon from the gold
   */
  public void setDistance (int distance) {
    this.distance = distance;
  }

  @Override
  public String toString () {
    return "Row: " + row + " | Col: " + col + " | Distance: " + distance;
  }
}