package Model;

public abstract class Position
{
	/**
	 * The row value of this instance
	 */
	protected int row; 
	/**
	 * The column value of this instance 
	 */
	protected int col;

	/**
	 * Constructor. Creates this Position instance
	 * 
	 * @param row the row value of this instance 
	 * @param col the column value of this instance 
	 */
	public Position (int row, int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * Gets the row value of this instance
	 * 
	 * @return	the value indicating the row position on the board
	 */
	public int getRow () {
		return row;
	}

	/**
	 * Gets the column value of this instance
	 * 
	 * @return the value indicating the column position on the board
	 */
	public int getCol () {
		return col;
	}

	@Override 
	public boolean equals (Object obj) {
		if (obj == null)
			return false;
		
		return ((Position) obj).row == this.row && 
			   ((Position) obj).col == this.col;
	}
}