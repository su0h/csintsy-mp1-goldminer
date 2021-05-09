import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.beans.value.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.application.Platform;

import Model.*;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Controller implements EventHandler<Event>, ChangeListener<String> {
    /**
     * View class instance
     */
    View view;
    /**
     * GoldMiner class instance
     */
    GoldMiner miner;
    /**
     * Direction list counter during scanning process
     */
    int ctr; 

    /**
     * Constructor. Creates this Controller instance
     * 
     * @param view  the view instance to be manipulated by this Controller
     */
    public Controller (View view) {
        /* ASSIGN VIEW TO ATTRIBUTE */
        this.view = view;
        /* SET THIS CONTROLLER AS THE EVENT HANDLER OF VIEW */
        this.view.setEventHandlers (this);
    }

    /**
     * Main loop for the Random Level Miner Agent. This uses the Random class to determine which 
     * move the agent will make every time.
     */
	public void startAgent () {
        // if miner is not yet done searching
		if (!(miner.getCave ().getTile (miner.getRow (), miner.getCol ()) instanceof Gold) && 
            !(miner.getCave ().getTile (miner.getRow (), miner.getCol ()) instanceof Pit) &&
            !miner.isEnd ()) {
            // move miner
            if (view.getAgentType ().equalsIgnoreCase ("Random"))
                moveRandomMiner ();
            else smartMiner ();
        } 
        // else, check what tile miner landed on 
        else {
            // if gold tile, then miner did a successful search
            if (miner.getCave ().getTile (miner.getRow (), miner.getCol ()) instanceof Gold) {
                System.out.println ("Success!");
                view.displayResult("Search Successful!", miner.getMoveCtr(), miner.getRotateCtr(), miner.getScanCtr(), "sound/success.mp3");
            // if pit tile, then miner fell down and did an unsuccessful search
            } else if (miner.getCave ().getTile (miner.getRow (), miner.getCol ()) instanceof Pit) {
                System.out.println ("Fell down a pit!");
                view.displayResult ("Fell down the pit! Search unsuccessful!", miner.getMoveCtr (), miner.getRotateCtr (), miner.getScanCtr (), "sound/damage.mp3");
            // else, miner had no more possible moves left, therefore an unsuccessful search
            } else { 
                System.out.println ("No moves left!");
                view.displayResult ("No more possible moves left! Search unsuccessful!", miner.getMoveCtr (), miner.getRotateCtr (), miner.getScanCtr (), "sound/fail.mp3");
            }
            view.updateInfoPanel (
                miner.getMoveCtr (), miner.getRotateCtr (), miner.getScanCtr (), 
                miner.getCave ().getTile (miner.getRow (), miner.getCol ()), 
                miner.getDirection ()
            );
        }
    }

    /**
     * Timeline animation for the random miner agent. This will add a delay between each movement made 
     * by the miner.
     */
    public void moveRandomMiner () {
		Random rand = new Random ();

        /* ADD DELAY IN THE PROCESS */
        Timeline delay = new Timeline (
            new KeyFrame (
                Duration.millis (10 * view.getAnimationSpeed ()),
                e -> {
                    // RANDOM: move only (60%) or rotate then move (40%)
                    int choice = rand.nextInt (100) + 1; 

                    /* 1/3 CHANCE TO ROTATE */
                    if (choice <= 33) {
                        miner.randomRotate ();
                    /* 1/3 CHANCE TO MOVE */
                    } else if (choice > 33 && choice <= 66) {
                        miner.move ();
                    /* 1/3 CHANCE TO SCAN */
                    } else miner.scan ();

                    /* UPDATE THE MINER AGENT'S POSITION ON THE GUI BOARD */
                    view.updateBoard (miner.getRow (), miner.getCol (), miner.getDirection ());
                    /* CHECKS THE CURRENT TILE THE MINER IS ON, AND UPDATES INFO PANEL ACCORDINGLY */
                    checkTile ();
                    /* RE-CALL RANDOM AGENT METHOD TO CONTINUE LOOP */
                    startAgent ();
                }
            )
        );
        /* START PROCESS AND MOVEMENT ANIMATION WITH DELAY */
        delay.play ();  
    }

    /**
     * Start of movement process for smart miner agent. Initializes the sequence of movements
     * based on current position.
     */
    public void smartMiner () {
        /* IF GOLD IS NOT YET FOUND NOR TARGETED BY MINER */
        if (!(miner.getTarget () instanceof Gold)) {
            // set backtrack to false if currently true
            if (miner.isBacktrack ())
                miner.resetBacktrack ();  
            // set current direction for tracking
            miner.setCurDirection ();
            // get all current valid directions for miner to scan and rotate with 
            miner.setDirections ();
            // rotate and scan all valid directions to check for objects
            ctr = miner.getDirections ().size (); // counter for scanning all directions
            // reset found object for next direction scanning
            miner.clearFound ();
            setObjectScanned ();
        } 
        /* ELSE PROCEED IMMEDIATELY TO MOVING MINER */
        else 
            moveSmartMiner (); 
    }

    /**
     * For smart level only. Scans all directions and get object to be possibly
     * targeted by miner with priority (Gold > Beacon > null).
     */
    public void setObjectScanned () { 
        /* IF NOT ALL DIRECTIONS HAVE BEEN SCANNED YET */
        if (ctr > 0 && !(miner.getTarget() instanceof Gold)) 
            scanAndRotate ();
        /* ELSE, PROCEED TO MOVING MINER */
        else
            moveSmartMiner ();
    }

    /**
     * For smart level only. Rotates miner to another direction and scan to get
     * object to be possibly targeted by miner.
     */
    public void scanAndRotate () {
        /* ADD DELAY IN THE PROCESS */
        Timeline delay = new Timeline (
            new KeyFrame (
                Duration.millis(10 * view.getAnimationSpeed()),
                e -> {
                    /* ROTATE MINER FIRST IN ORDER BEFORE SCANNING */
                    // if current direction is the last element in the direction list
                    if (!miner.getDirections().contains(miner.getDirection()) ||
                        miner.getDirections().indexOf(miner.getDirection()) + 1 == miner.getDirections().size())
                        // rotate miner to the firstelement in the direction list
                        miner.rotate(miner.getDirections().get(0));
                    // else, move to the next corresponding element in the direction list
                    else
                        miner.rotate(
                            miner.getDirections().get(miner.getDirections().indexOf(miner.getDirection()) + 1)
                        );

                    // scan current direction and get object found
                    miner.setFound(miner.scan());

                    // if found object is not a pit and not yet visited 
                    if (!miner.getVisited().contains(miner.getFound()) && !(miner.getFound() instanceof Pit)) {
                        /* PRIORITY: GOLD > BEACON */
                        // if found object is gold, target immediately by miner
                        if (miner.getFound() instanceof Gold) {
                            miner.setTarget(miner.getFound());
                            miner.setCurDirection(); // set as target direction for miner to move
                        }
                        // else if found object is a beacon and miner has no current target yet
                        else if (miner.getFound() instanceof Beacon && miner.getTarget() == null) {
                            miner.setTarget(miner.getFound());
                            miner.setCurDirection();
                        }
                        // else if found object is null and miner has no current target yet
                        else if (miner.getFound() == null && miner.getTarget() == null) {
                            miner.setTarget(miner.getFound());
                            miner.setCurDirection();
                        }
                    }

                    /* UPDATE THE MINER AGENT'S POSITION ON THE GUI BOARD */
                    view.updateBoard(miner.getRow(), miner.getCol(), miner.getDirection());
                    /* UPDATE COUNTER VALUES AND DIRECTION ON GUI */
                    view.updateInfoPanel (
                        miner.getMoveCtr (), miner.getRotateCtr (), miner.getScanCtr (), 
                        miner.getCave ().getTile (miner.getRow (), miner.getCol ()), 
                        miner.getDirection ()
                    );

                    // decrement direction list scan counter
                    ctr--; 
                    // check if all objects have been scanned
                    setObjectScanned();
                }
            )
        );
        /* START PROCESS AND MOVEMENT ANIMATION WITH DELAY */
        delay.play();
    }

    /**
     * For smart level only. Moves miner based on conditions as acquired from scanning.
     */
    public void moveSmartMiner () {
        /* ADD DELAY IN THE PROCESS */
        Timeline delay = new Timeline (
            new KeyFrame (
                Duration.millis (10 * view.getAnimationSpeed ()),
                e -> {
                    /* IF NO POSSIBLE MOVEMENTS FOR MINER TO DO, BACKTRACK */
                    if (miner.getDirections().size() == 0 && !(miner.getTarget() instanceof Gold))
                        // miner shall backtrack
                        miner.setBacktrack(); // bTrack = true
                    
                    /* IF MINER IS NOT ON TARGETED DIRECTION, ROTATE */
                    if (miner.getDirection() != miner.getCurDirection() &&
                        miner.getDirections().contains(miner.getCurDirection()))
                        miner.rotate(miner.getCurDirection());
                        
                    // scan current direction again to check if object found is not a pit
                    // do not scan anymore if target found is gold
                    if (!(miner.getTarget() instanceof Gold))
                        miner.setFound(miner.scan());
                        
                    // if object found is a pit, backtrack miner
                    if (miner.getFound() instanceof Pit)
                        miner.setBacktrack ();
                    
                    /* IF MINER WILL BACKTRACK */
                    if (miner.isBacktrack()) {
                        // if no more tiles in the backtrack stack list, no more possible moves
                        if (miner.getBacktracks().size() == 0)
                            // end search immediately
                            miner.endSearch(); 
                        // else, backtrack
                        else {
                            // add current position to visited tiles
                            miner.addToVisited();
                            // rotate
                            rotateBacktrack (miner.getRow (), miner.getCol ());
                            // backtrack miner
                            miner.backtrackMiner(); // move
                        }
                    }
                    /* ELSE, MOVE MINER */
                    else {
                        // add current position to backtrack stack list and mark as visited
                        miner.addToBacktrack();
                        miner.addToVisited();
                        // move miner
                        miner.move ();
                    }
                    
                    /* UPDATE THE MINER AGENT'S POSITION ON THE GUI BOARD */
                    view.updateBoard(miner.getRow(), miner.getCol(), miner.getDirection());
                    /* UPDATE COUNTER VALUES AND DIRECTION ON GUI */
                    view.updateInfoPanel (
                        miner.getMoveCtr (), miner.getRotateCtr (), miner.getScanCtr (), 
                        miner.getCave ().getTile (miner.getRow (), miner.getCol ()), 
                        miner.getDirection ()
                    );
                    /* CHECKS THE CURRENT TILE THE MINER IS ON, AND UPDATES INFO PANEL ACCORDINGLY */
                    checkTile();
                    /* RE-CALL SMART AGENT METHOD TO CONTINUE LOOP */
                    startAgent();
                }
            )
        );
        delay.play ();
    }

	public void rotateBacktrack (int oldRow, int oldCol) {
        int backtrackRow = miner.getBacktracks ().peek ().getRow ();
        int backTrackCol = miner.getBacktracks ().peek ().getCol ();

        if (oldRow == backtrackRow) {
            if (oldCol > backTrackCol) {
                // direction = 'L';
                miner.rotate ('L');
                // System.out.println ("SET LEFT");
            } else if (oldCol < backTrackCol) {
                // direction = 'R';
                miner.rotate ('R');
                // System.out.println ("SET RIGHT");
            }
        } else if (oldCol == backTrackCol) {
            if (oldRow < backtrackRow) {
                // direction = 'D';
                miner.rotate ('D');
                // System.out.println ("SET DOWN");
            } else if (oldRow > backtrackRow) {
                // direction = 'U';
                miner.rotate ('U');
                // System.out.println ("SET UP");
            }
        }
        /* UPDATE THE MINER AGENT'S POSITION ON THE GUI BOARD */
        view.updateBoard(miner.getRow(), miner.getCol(), miner.getDirection());
        /* UPDATE COUNTER VALUES AND DIRECTION ON GUI */
        view.updateInfoPanel (
            miner.getMoveCtr (), miner.getRotateCtr (), miner.getScanCtr (), 
            miner.getCave ().getTile (miner.getRow (), miner.getCol ()), 
            miner.getDirection ()
        );
	}

    /**
     * Checks what tile the miner agent landed on upon making a move. This also updates the 
     * Information Panel on the GUI based on the miner agent's current tile.
     */
    public void checkTile () {
        // check if target tile has been landed on by miner, reset target if true
        if (view.getAgentType ().equalsIgnoreCase ("Smart") && miner.getCurPosition() == miner.getTarget()) {
            // System.out.println ("Target reached!");
            miner.clearTarget ();
        }
        /* FOR DEBUGGING PURPOSES ONLY: */
        // check if current tile landed is a beacon
        if (miner.getCurPosition () instanceof Beacon) {
            System.out.println ("Landed on Beacon!");
            if (((Beacon) miner.getCave ().getTile (miner.getRow (), miner.getCol ())).getDistance () == 0)
                System.out.println ("Gold cannot be found!");
            else  {
                if (view.getAgentType ().equalsIgnoreCase ("Smart"))
                    miner.setGoldTarget (((Beacon) miner.getCurPosition ()).getDistance ());
                System.out.println(
                    ((Beacon) miner.getCave().getTile(miner.getRow(), miner.getCol())).getDistance()
                    + " tiles away from the gold!"
                );
            } 
        }
        // TEMPORARY: display current counter values in console
        System.out.println ("Moves: " + miner.getMoveCtr () + " Rotations: " + miner.getRotateCtr () + " Scans: " + miner.getScanCtr ());
        // TEMPORARY: display current direction of movement in console
        System.out.println ("Direction: " + miner.getDirection ());

        /* TO UPDATE THE INFORMATION PANEL ON THE LEFTSIDE OF THE IN-GAME SCREEN */
        view.updateInfoPanel (
            miner.getMoveCtr (), miner.getRotateCtr (), miner.getScanCtr (), 
            miner.getCave ().getTile (miner.getRow (), miner.getCol ()), 
            miner.getDirection ()
        );
    }

    @Override
    public void changed (ObservableValue<? extends String> observableValue, String s, String t1) {
        /* CHECK IF NUMBER IS WITHIN RANGE */
        if (!isValidNumber (t1)) 
            view.setDisableStartGameBtn (true);
        else view.setDisableStartGameBtn (false);

        /* TO LIMIT INPUT LENGTH FOR THE TEXTFIELD */
        if (view.getDimensionField ().getText ().length () > 1)
            view.getDimensionField ().setText (view.getDimensionField ().getText().substring(0, 2));
    }

    /**
     * Checks if a string is an integer within 8 to 64.  
     * 
     * @param   s the string to be checked
     * @return  true if the string is an integer and is within 8 to 64, otherwise false.
     */
    private boolean isValidNumber (String s) {
        int num;

        /* ATTEMPT TO PARSE STRING INTO AN INTEGER */
        try {
            num = Integer.parseInt (s);
        /* CATCH IF NOT AN INTEGER */
        } catch (NumberFormatException nfe) {
            return false;
        }

        /* IF AN INTEGER, CHECK IF WITHIN 8 TO 64 */
        if (num >= 8 && num <= 128)
            return true;
        else return false;
    }

    @Override
    public void handle(Event event) {
        /* MOUSE CLICK SOUND FX */
        // MOUSE CLICK: Locate the sound fx in CLASSPATH
        URL mouseUrl = getClass().getResource("sound/mouse.mp3");
        String mouseStringUrl = mouseUrl.toExternalForm();
        // MOUSE CLICK: Create a Media from the mp3
        Media mouseMedia = new Media(mouseStringUrl);
        // MOUSE CLICK: Create a Media Player to play
        MediaPlayer mpMouse = new MediaPlayer (mouseMedia);
        // MOUSE CLICK: Create a Media View for scene graph node as required +
        MediaView mvMouse = new MediaView(mpMouse);

        /* IF EVENT SOURCE IS A BUTTON (A BUTTON WAS CLICKED) */
        if (event.getSource () instanceof Button) {
            switch (((Button) event.getSource ()).getId ()) {
                /* START GAME BUTTON IN MAIN MENU */
                case "startGame":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.showGameSetup ();
                break;
                /* ABOUT GAME BUTTON IN MAIN MENU */
                case "instructions":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.showAbout ();
                break;
                /* EXIT GAME BUTTON IN MAIN MENU */
                case "exitGame":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.exitGame ();
                break;
                /* MAIN MENU BUTTON IN GAME SETUP SCENE */
                case "mainMenu":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.showMainMenu ();
                break;
                /* START BUTTON IN GAME SETUP SCENE */
                case "startMining":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    System.out.println ("START! Dimension: " + view.getBoardDimension () + " | Agent: " + view.getAgentType () + " | Board: " + view.getGenerationType ());
                    /* IF MANUAL BOARD, OPEN MANUAL COORDINATE INPUT WINDOW */
                    if (view.getGenerationType ().equalsIgnoreCase ("Manual")) {
                        view.showManualInput ();
                    /* ELSE, GENERATE RANDOM BOARD */
                    } else {
                        // Create board
                        System.out.println ("Generating random board...");
                        Cave cave = new Cave (view.getBoardDimension ());
                        miner = new GoldMiner (cave);
                        view.boardSetup (cave);
                        view.showInGame ();
                        System.out.println ("Miner starting...");
                        startAgent ();
                    }
                break;
                /* CANCEL BUTTON IN MANUAL COORDINATE INPUT WINDOW */
                case "cancelManual":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.closeManualInput ();
                break;
                /* SAVE BUTTON IN MANUAL COORDINATE INPUT WINDOW */
                case "saveManual":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.closeManualInput ();
                    // Create board
                    System.out.println ("Generating custom board...");
                    Cave cave = new Cave (view.getBoardDimension (), view.getBeaconCoordinates(), view.getGoldCoordinates (), view.getPitCoordinates ());
                    miner = new GoldMiner (cave);
                    view.boardSetup (cave);
                    view.showInGame ();
                    System.out.println ("Miner starting...");
                    startAgent ();
                break;
                /* NEXT BUTTON IN ABOUT GAME SCENE */
                case "next":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.showNext();
                    break;
                /* MAIN MENU BACK BUTTON IN ABOUT GAME SCENE */
                case "back":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.showMainMenu();
                break;
                /* BACK BUTTON IN ABOUT GAME SCENE */
                case "goBack":
                    //Mouse click: play
                    mpMouse.setAutoPlay(true);
                    view.showAbout();
                    break;
            }
        /* TO UPDATE TOGGLE GROUPS */
        } else if (event.getSource () instanceof RadioButton) {
            view.updateRadioBtns ();
        }
    }
}
