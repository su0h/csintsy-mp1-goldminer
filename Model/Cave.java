package Model;
import java.util.*;

public class Cave {
  /**
   * The board of Tile instances with specified objects
   */
  private Tile[][] board;

  /* OBJECT RATIOS */
  /**
   * Beacon object ratio to be used for calculating the number of beacons 
   * depending on the given board size
   */
  public static final double BEACON_RATIO = 0.5;
  /**
   * Pit object ratio to be used for calculating the number of pits depending
   * on the given board size
   */
  public static final double PIT_RATIO = 0.65;

  /**
   * Constructor. Generates the game board given a set of input data.
   *
   * @param   dimension   the dimensions of the board
   * @param   beaconTiles 2D array containing the coordinates of the beacon tiles
   * @param   goldTile    array containing the coordinates of the gold tile
   * @param   pitTiles    2D array containing the coordinates of the pit tiles.
   */
  public Cave (int dimension, int[][] beaconTiles, int[] goldTile, int[][] pitTiles) {
    /* INSTANTIATE BOARD */
    board = new Tile[dimension][dimension];

    /* INSTANSTIATE GOLD TILE */
    board[goldTile[0]][goldTile[1]] = new Gold (goldTile[0], goldTile[1]);

    int row, col;

    /* INSTANTIATE BEACONS */
    for (int i = 0; i < beaconTiles.length; i++) {
      row = beaconTiles[i][0];
      col = beaconTiles[i][1];
      board[row][col] = new Beacon (row, col);
      ((Beacon) board[row][col]).setDistance (getGoldDistance(board[row][col], board[goldTile[0]][goldTile[1]]));
    }

    /* INSTANTIATE PITS */
    for (int i = 0; i < pitTiles.length; i++) {
      row = pitTiles[i][0];
      col = pitTiles[i][1];
      board[row][col] = new Pit (row, col);
    }

    /* FILL IN TILES */
    for (int i = 0; i < dimension; i++)
      for (int j = 0; j < dimension; j++)
        if (board[i][j] == null)
          board[i][j] = new Tile (i, j);
  }

  /**
   * Constructor. Generates a randomized game board, independent from any set of input data. 
   * The amount of each board object is dependent on the pre-determined object ratio.
   *
   * @param   dimension   the dimensions of the board
   */
  public Cave (int dimension) {
    /* INSTANTIATE BOARD */
    board = new Tile[dimension][dimension];

    int row, col, goldRow, goldCol;
    Random rand = new Random ();

    /* INSTANTIATE GOLD */
    goldRow = rand.nextInt (dimension);
    goldCol = rand.nextInt (dimension);

    board[goldRow][goldCol] = new Gold (goldRow, goldCol);

    /* INSTANTIATE BEACONS */
    double beaconAmt = dimension * BEACON_RATIO;
    for (int i = 0; i < beaconAmt;) {
      int inLine = rand.nextInt (10) + 1;

      /* 60% CHANGE TO BE IN-LINE */
      if (inLine <= 6) {
        /* 50% CHANCE TO BE ON THE GOLD'S HORIZONTAL AXIS */
        if (rand.nextInt (2) == 1) {
          row = goldRow;
          /* RAND A COLUMN UNTIL A VACANT TILE IS FOUND */
          do {
            col = rand.nextInt (dimension);
          } while (board[row][col] != null);
        /* 50% CHANCE TO BE ON THE GOLD'S VERTICAL AXIS */
        } else {
          col = goldCol;
          /* RAND A ROW UNTIL A VACANT TILE IS FOUND */
          do {
            row = rand.nextInt (dimension);
          } while (board[row][col] != null);
        }
      /* 40% CHANCE TO BE NOT IN-LINE */
      } else {
        /* RAND A ROW & COLUMN UNTIL A VACANT TILE IS FOUND */
        do {
          row = rand.nextInt (dimension);
          col = rand.nextInt (dimension);
        } while (board[row][col] != null);
      }
      
      /* INSTANTIATE BEACON, NOT ON TOP LEFT TILE (START POSITION OF MINER) */
      if (row != 0 || col != 0) {
        board[row][col] = new Beacon (row, col);
        ((Beacon) board[row][col]).setDistance (getGoldDistance(board[row][col], board[goldRow][goldCol]));
        i++;
      }
    }

    /* INSTANTIATE PITS */
    double pitAmt = dimension * PIT_RATIO;
    for (int i = 0; i < pitAmt;) {
      row = rand.nextInt (dimension);
      col = rand.nextInt (dimension);

      if (board[row][col] == null && (row != 0 || col != 0)) {
        board[row][col] = new Pit (row, col);
        i++;
      }
    }

    /* FILL IN TILES */
    for (int i = 0; i < dimension; i++)
      for (int j = 0; j < dimension; j++)
        if (board[i][j] == null)
          board[i][j] = new Tile (i, j);
  }

  /**
   * Returns the distance from a beacon tile to the gold tile. If the gold tile 
   * is not on the northern, southern, eastern, nor western sides of the beacon, 
   * this returns a distance value of 0; otherwise, it returns the actual distance
   * between the beacon and gold tile.
   *
   * @param   beacon  the beacon tile
   * @param   gold    the gold tile
   *
   * @return  the distance between the beacon and the gold tiles.
   */
  public int getGoldDistance (Tile beacon, Tile gold) {
    if (beacon.getRow () == gold.getRow ())
      return Math.abs (beacon.getCol () - gold.getCol ());
    else if (beacon.getCol () == gold.getCol ())
      return Math.abs (beacon.getRow () - gold.getRow ());
    else
      return 0;
  }

  /**
   * Prints out the current board state.
   */
  public void printBoard (int row, int col) {
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board.length; j++) {
        if (row == i && j == col)
          System.out.print ("M ");
        else if (board[i][j] instanceof Gold)
          System.out.print ("G ");
        else if (board[i][j] instanceof Pit)
          System.out.print ("P ");
        else if (board[i][j] instanceof Beacon)
          System.out.print ("B ");
        else System.out.print ("# ");
      }
      System.out.println ();
    }
  }

  /**
   * Returns the whole board.
   *
   * @return  the whole board and its contents
   */
  public Tile[][] getTiles () {
    return board;
  }

  /**
   * Returns a specific tile on the board given a row and column value.
   *
   * @param   row   the row value
   * @param   col   the col value
   *
   * @return  the tile on the row and column coordinate
   */
  public Tile getTile (int row, int col) {
    return board[row][col];
  }
}