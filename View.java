import javafx.application.*;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.geometry.*;

import java.io.*;
import java.net.URL;
import java.util.*;

import Model.*;

public class View extends Application {
    /* SOUND FX: Media Player */
    /**
     * Media Player for Mouse click
     */
    public MediaPlayer mpMouse;

    /* STAGES */
    /**
     * The main stage of Gold Miner.
     */
    Stage mainStage;
    /**
     * The stage for manual settings of Gold Miner.
     */
    Stage manualInputStage;

    /* MAIN SCENES */
    /**
     * Main Menu scene of Gold Miner.
     */
    Scene mainMenuScene;
    /**
     * Game Setup scene of Gold Miner.
     */
    Scene gameSetupScene;
    /**
     * About the Game Scene.
     */
    Scene aboutGameScene;
    /**
     * About the Game Scene: Next Page.
     */
    Scene nextScene;
    /**
     * In Game scene of Gold Miner
     */
    Scene inGameScene;

    /* MAIN LAYOUTS */
    /**
     * Main layout for the Main Menu scene.
     */
    BorderPane mainLayout;
    /**
     * Main layout for the Game Setup scene.
     */
    BorderPane setupLayout;
    /**
     * Main layout for the About Game Setup scene.
     */
    BorderPane aboutLayout, nextLayout;
    /**
     * Main layout for the In-Game scene
     */
    BorderPane gameLayout;

    /* MAIN MENU ELEMENTS */
    /**
     * Start Game button for the Main Menu scene. Leads to Game Setup scene.
     */
    Button startBtn;
    /**
     * About Game button for the Main Menu scene. Leads to About Game scene.
     */
    Button instructionsBtn;
    /**
     * Exit Game button for the Main Menu scene. Exits Gold Miner.
     */
    Button exitBtn;

    /* GAME SETUP ELEMENTS */
    /**
     * TextField for Board Dimension in the Game Setup scene.
     */
    TextField boardDimension;
    /**
     * ComboBox for Agent Type in the Game Setup scene.
     */
    //ComboBox agentType;
    /**
     * Main Menu button for the Game Setup scene.
     */
    Button backBtn;
    /**
     * Start Game button for the Game Setup scene.
     */
    Button startGameBtn;

    /* ABOUT THE GAME SETUP ELEMENTS */
    /**
     * Next Button
     */
    Button nextButton;
    /**
     * Back Button
     */
    Button backButton;
    /**
     * Back Button on Next Page
     */
    Button back;

    /* GUI ELEMENTS FOR IN GAME SET UP */
    /**
     * ToggleGroup for Agent Types (Smart/Random)
     */
    ToggleGroup agentType;
    /**
     * Button for Random Agent Type. Used in ToggleGroup
     */
    RadioButton randomAgentBtn;
    /**
     * Button for Smart Agent Type. Used in ToggleGroup
     */
    RadioButton smartAgentBtn;

    /**
     * ToggleGroup for Board Type (Manual/Random)
     */
    ToggleGroup boardType;
    /**
     * Button for Manual Board Type. Used in ToggleGroup
     */
    RadioButton manualBoardBtn;
    /**
     * Button for Random Board Type. Used in ToggleGroup
     */
    RadioButton randomBoardBtn;

    /* MANUAL SET UP GAME ELEMENTS */
    /**
     * Cancel Button for manual coordinate input window
     */
    Button manualCancelBtn;
    /**
     * Save Button for manual coordinate input window
     */
    Button manualSaveBtn;
    /**
     * TextArea input for the Beacon Coordinates
     */
    TextArea beaconCoordinates;
    /**
     * TextArea input for the Gold Coordinates
     */
    TextArea goldCoordinates;
    /**
     * TextArea input for the Pit Coordinates
     */
    TextArea pitCoordinates;

    /* IN GAME INFORMATION PANEL TEXT ELEMENTS */
    /**
     * Miner's current tile type (Dirt/Pit/Beacon/Gold)
     */
    Text currentTile;
    /**
     * Move counter of Miner
     */
    Text moveCtr;
    /**
     * Rotate counter of Miner
     */
    Text rotateCtr;
    /**
     * Scan counter of Miner
     */
    Text scanCtr;
    /**
     * Current direction the Miner is facing
     */
    Text minerDirection;
    /**
     * Miner's current action
     */
    Text minerStatus;
    /**
     * Distance to Gold when Beacon is found
     */
    Text goldDistance;
    /**
     * Slider to control the animation speed of the Miner
     */
    Slider speedSlider;

    /* IN GAME GUI ELEMENTS */
    /**
     * Main Game board
     */
    GridPane board;
    /**
     * Cave tiles
     */
    ArrayList<StackPane> tiles;
    /**
     * Miner GIF facing North
     */
    ImageView minerNorth;
    /**
     * Miner GIF facing South
     */
    ImageView minerSouth;
    /**
     * Miner GIF facing West
     */
    ImageView minerWest;
    /**
     * Miner GIF facing East
     */
    ImageView minerEast;

    /* SOUND FX */
    /**
     * URL path for Main Menu ambient music
     */
    URL ambianceUrl;
    /**
     * String path for Main Menu ambient music
     */
    String ambianceStringUrl;
    /**
     * Media object for Main Menu ambient music
     */
    Media mainMenuMedia;
    /**
     * MediaPlayer for Main Menu ambient music
     */
    MediaPlayer mpMainMenu;

    /**
     * URL path for In-Game Scene ambient sound effects
     */
    URL miningUrl;
    /**
     * String path for In-Game Scene ambient sound effects
     */
    String miningStringUrl;
    /**
     * Media object for In-Game Scene ambient sound effects
     */
    Media ingameMedia;
    /**
     * MediaPLayer for In-Game Scene ambient sound effects
     */
    MediaPlayer mpIngame;

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        System.out.println (new File (".").getAbsoluteFile ());

        /* SETUP GUI SCENES */
        try {
            ambientSetup ();
            mainMenuSetup ();
            gameSetSetup ();
            manualInputSetup ();
            aboutSetup ();
            gameSetup ();
            nextInstruct ();
        } catch (Exception e) {
            e.printStackTrace ();
            System.out.println ("An error occurred in accessing file resource/s! File/s may not exist!");
        }

        /* SET CONTROLLER */
        new Controller (this);

        /* MAIN STAGE SETUP */
        mainStage.getIcons ().add (new Image (new FileInputStream ("images/goldpick.png")));
        mainStage.setTitle ("Grab that Pot of Gold!");
        mainStage.setScene (mainMenuScene);
        // mainStage.setResizable (false);
        mainStage.show ();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Setup for the ambient music used in the GUI
     */
    public void ambientSetup () {
        ambianceUrl = getClass().getResource("sound/ambiance.mp3");
        ambianceStringUrl = ambianceUrl.toExternalForm();
        mainMenuMedia = new Media(ambianceStringUrl);
        mpMainMenu = new MediaPlayer (mainMenuMedia);
        miningUrl = getClass().getResource("sound/mining.mp3");
        miningStringUrl = miningUrl.toExternalForm();
        ingameMedia = new Media(miningStringUrl);
        mpIngame = new MediaPlayer (ingameMedia);
    }

    /**
     * Setup for the Main Menu Scene.
     */
    public void mainMenuSetup () throws FileNotFoundException {
        try {
            // Automatically play + continue lang
            mpMainMenu.setAutoPlay(true);
            mpMainMenu.setCycleCount(MediaPlayer.INDEFINITE);
            // AMBIANCE: Create a Media View for scene graph node as required
            MediaView mvMainMenu = new MediaView(mpMainMenu);

            /* INITIALIZE LAYOUTS */
            mainLayout = new BorderPane ();
            VBox buttonLayout = new VBox(5);

            /* INITIALIZE TITLE */
            Text mainTitle = new Text ("Gold\nMiner");
            mainTitle.setFont (Font.loadFont (new FileInputStream ("fonts/TITLE.ttf"), 105));
            mainTitle.setFill (Color.GOLDENROD);
            mainTitle.setStroke (Color.SADDLEBROWN);
            mainTitle.setStrokeWidth (1);

            /* FORMAT BUTTONS + BUTTON LAYOUT */
            buttonLayout.setAlignment (Pos.CENTER_LEFT);
            // START BUTTON
            FileInputStream startFile = new FileInputStream("images/start.png");
            Image start = new Image(startFile);
            ImageView startView = new ImageView(start);
            startView.setFitHeight(30);
            startView.setFitWidth(30);
            startBtn = new Button ("START A GAME", startView);
            startBtn.setPrefSize (300, 30);
            startBtn.setPadding (new Insets(1, 3, 1, 3));
            startBtn.setFont(Font.loadFont (new FileInputStream("fonts/OPTIONS.otf"), 28));
            startBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

            // ABOUT GAME BUTTON
            instructionsBtn = new Button ("ABOUT GAME");
            instructionsBtn.setPrefSize (300, 30);
            instructionsBtn.setPadding (new Insets (1, 3, 1, 3));
            instructionsBtn.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 28));
            instructionsBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
            // EXIT GAME BUTTON
            exitBtn = new Button ("EXIT GAME");
            exitBtn.setPrefSize (300, 30);
            exitBtn.setPadding (new Insets (1, 3, 1, 3));
            exitBtn.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 28));
            exitBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

            // SET BUTTON IDs
            startBtn.setId ("startGame");
            instructionsBtn.setId ("instructions");
            exitBtn.setId ("exitGame");

            /* Design: MOVING STEVE */
            //Creating an image
            Image steve = new Image(new FileInputStream("images/steve4.gif"));
            //Setting the image view
            ImageView steveView = new ImageView(steve);
            //Setting the position of the image
            steveView.setX(210);
            steveView.setY(-15);
            //setting the fit height and width of the image view
            steveView.setFitHeight(490);
            steveView.setFitWidth(500);
            //Setting the preserve ratio of the image view
            steveView.setPreserveRatio(true);
            //Creating a Group object
            Group root = new Group(steveView);

            /* ADDING OF CHILDREN TO LAYOUTS */
            buttonLayout.getChildren ().addAll (startBtn, instructionsBtn, exitBtn);
            buttonLayout.setPadding (new Insets (30, 5, 20, 20)); //trbl
            mainLayout.setTop (mainTitle);
            mainLayout.getChildren().add(steveView);
            mainLayout.setAlignment (steveView, Pos.BASELINE_RIGHT);
            mainLayout.setCenter (buttonLayout);
            mainLayout.setAlignment (buttonLayout, Pos.CENTER_LEFT);
            mainLayout.setAlignment (mainTitle, Pos.TOP_LEFT);
            mainLayout.setPadding (new Insets (30, 5, 20, 20)); //trbl

            /* SETUP BACKGROUND IMAGE/GIF FOR INSTRUCTIONS SCENE */
            BackgroundImage bg = new BackgroundImage (
                    new Image (new FileInputStream ("images/ng2.jpg")), BackgroundRepeat.REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize (600, 450, false, false, false, false));
            mainLayout.setBackground (new Background(bg));

            /* SET SCENE */
            mainLayout.getChildren().add(mvMainMenu);
            mainMenuScene = new Scene (mainLayout, 600, 450);

        } catch (Exception e) {e.printStackTrace ();}
    }

    /**
     * Sets the Main Menu Scene as the Main Stage's current scene.
     */
    public void showMainMenu () {
        mainStage.setScene (mainMenuScene);
        mainStage.show ();
    }

    /**
     * Setup for the Game Setup Scene.
     * 
     * @throws  FileNotFoundException   if the files needed are not found
     */
    public void gameSetSetup () throws FileNotFoundException {
        /* INITIALIZE LAYOUTS */
        setupLayout = new BorderPane ();
        VBox contentLayout = new VBox (5);
        HBox buttonLayout = new HBox (5);

        /* INITIALIZE TITLE */
        Text gameSetupTitle = new Text ("Game Setup");
        gameSetupTitle.setFont (Font.loadFont (new FileInputStream ("fonts/TITLE.ttf"), 76));
        gameSetupTitle.setFill (Color.DARKGRAY);
        gameSetupTitle.setStroke (Color.BLACK);
        gameSetupTitle.setStrokeWidth (1);

        /* FORMAT LAYOUTS*/
        buttonLayout.setAlignment (Pos.CENTER);
        contentLayout.setAlignment (Pos.CENTER);

        /* FORMAT TEXTFIELD FOR BOARD DIMENSION */
        Text boardDimensionText = new Text ("Board Dimension [8-64]:");
        boardDimensionText.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 20));
        boardDimensionText.setStyle ("-fx-text-fill: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        boardDimensionText.setFill (Color.WHITE);

        boardDimension = new TextField ();
        boardDimension.setMaxWidth (53);
        boardDimension.setPromptText ("00");
        boardDimension.setFont (Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 20));
        boardDimension.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

        /* FORMAT RADIO BUTTONS FOR AGENT TYPE */
        Text agentSelectionText = new Text ("Select Agent Type:");
        agentSelectionText.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 20));
        agentSelectionText.setStyle ("-fx-text-fill: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        agentSelectionText.setFill (Color.WHITE);
        // RADIO BUTTON TOGGLE GROUP (Para isang radio button lang masselect at a time)
        agentType = new ToggleGroup ();
        // RADIO BUTTONS + FORMATTING
        randomAgentBtn = new RadioButton ("Random");
        smartAgentBtn = new RadioButton ("Smart");

        randomAgentBtn.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 20));
        randomAgentBtn.getStyleClass().remove("radio-button");
        randomAgentBtn.getStyleClass().add("toggle-button");
        randomAgentBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        smartAgentBtn.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 20));
        smartAgentBtn.getStyleClass().remove("radio-button");
        smartAgentBtn.getStyleClass().add("toggle-button");
        smartAgentBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

        randomAgentBtn.setToggleGroup (agentType);
        smartAgentBtn.setToggleGroup (agentType);

        randomAgentBtn.setSelected (true);

        // LAYOUT FOR RADIO BUTTONS FOR AGENT TYPE
        HBox agentRadioBtns = new HBox (5);
        agentRadioBtns.getChildren ().addAll (randomAgentBtn, smartAgentBtn);
        agentRadioBtns.setAlignment (Pos.CENTER);
       
        /* FORMAT RADIO BUTTONS FOR BOARD GENERATION TYPE */
        Text boardSelectionText = new Text ("Select Board Type:");
        boardSelectionText.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 20));
        boardSelectionText.setStyle ("-fx-text-fill: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        boardSelectionText.setFill (Color.WHITE);
        boardType = new ToggleGroup ();

        manualBoardBtn = new RadioButton ("Manual");
        manualBoardBtn.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 20));
        manualBoardBtn.getStyleClass().remove("radio-button");
        manualBoardBtn.getStyleClass().add("toggle-button");
        manualBoardBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        randomBoardBtn = new RadioButton ("Random");
        randomBoardBtn.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 20));
        randomBoardBtn.getStyleClass().remove("radio-button");
        randomBoardBtn.getStyleClass().add("toggle-button");
        randomBoardBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

        manualBoardBtn.setToggleGroup (boardType);
        randomBoardBtn.setToggleGroup (boardType);

        manualBoardBtn.setSelected (true);
        updateRadioBtns ();

        HBox boardRadioBtns = new HBox (5);
        boardRadioBtns.getChildren ().addAll (manualBoardBtn, randomBoardBtn);
        boardRadioBtns.setAlignment (Pos.CENTER);

        /* FORMAT BUTTONS */
        // BACK TO MAIN MENU BUTTON
        backBtn = new Button ("GO BACK");
        backBtn.setId ("mainMenu");
        backBtn.setPrefSize (180, 30);
        backBtn.setPadding (new Insets (1, 3, 1, 3));
        backBtn.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 28));
        backBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        // START GAME BUTTON
        startGameBtn = new Button ("START!");
        startGameBtn.setId ("startMining");
        startGameBtn.setPrefSize (180, 30);
        startGameBtn.setPadding (new Insets (1, 3, 1, 3));
        startGameBtn.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 28));
        startGameBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        startGameBtn.setDisable (true);

        /* ADDING OF CHILDREN TO LAYOUTS */
        buttonLayout.getChildren ().addAll (backBtn, startGameBtn);
        contentLayout.getChildren ().addAll (boardDimensionText, boardDimension, agentSelectionText, agentRadioBtns, boardSelectionText, boardRadioBtns);
        setupLayout.setTop (gameSetupTitle);
        setupLayout.setCenter (contentLayout);
        setupLayout.setBottom (buttonLayout);
        setupLayout.setAlignment (gameSetupTitle, Pos.TOP_CENTER);
        setupLayout.setAlignment (contentLayout, Pos.CENTER);
        setupLayout.setPadding (new Insets (45, 10, 25, 10));

        /* SETUP BACKGROUND IMAGE/GIF FOR INSTRUCTIONS SCENE */
        BackgroundImage bg = new BackgroundImage (
                new Image (new FileInputStream ("images/ng2.jpg")), BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize (600, 450, false, false, false, false));
        setupLayout.setBackground (new Background(bg));

        /* SET SCENE */
        gameSetupScene = new Scene (setupLayout, 600, 450);
    }

    /**
     * Setup for the manual coordinates input. Used in manual board generation.
     * 
     * @throws FileNotFoundException    when the files needed are not found
     */
    public void manualInputSetup () throws FileNotFoundException {
        /* MAIN LAYOUT */
        BorderPane manualInputLayout;
        Scene manualInputScene;

        manualInputLayout = new BorderPane ();
        manualInputLayout.setPadding (new Insets (5, 5, 5, 5));

        /* TEXT SETUP */
        Text beaconText;
        Text goldText;
        Text pitText; 

        beaconText = new Text ("Beacons:");
        beaconText.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));
        goldText = new Text ("Gold:");
        goldText.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));
        pitText = new Text ("Pits:");
        pitText.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));

        /* TEXT AREA SETUP */
        beaconCoordinates = new TextArea ();
        beaconCoordinates.setPrefHeight (96);
        beaconCoordinates.setPrefWidth (168);
        beaconCoordinates.setFont (Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 16));
        beaconCoordinates.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px;");
        beaconCoordinates.setPromptText ("Ex. 5 6");
        goldCoordinates = new TextArea ();
        goldCoordinates.setPrefHeight (16);
        goldCoordinates.setFont (Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 16));
        goldCoordinates.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px;");
        goldCoordinates.setPromptText ("Ex. 0 1");
        pitCoordinates = new TextArea ();
        pitCoordinates.setPrefHeight (96);
        pitCoordinates.setPrefWidth (168);
        pitCoordinates.setFont (Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 16));
        pitCoordinates.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px;");
        pitCoordinates.setPromptText ("Ex. 1 6");
    
        /* LAYOUTS FOR BEACON, GOLD, AND PIT */
        VBox beaconLayout = new VBox (5);
        VBox goldLayout = new VBox (5);
        VBox pitLayout = new VBox (5);

        /* LAYOUT FOR BUTTONS */
        HBox buttonLayout = new HBox (5);
        buttonLayout.setAlignment (Pos.CENTER);
        manualCancelBtn = new Button ("CANCEL");
        manualCancelBtn.setId ("cancelManual");
        manualCancelBtn.setPrefSize (180, 30);
        manualCancelBtn.setPadding (new Insets (1, 3, 1, 3));
        manualCancelBtn.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 28));
        manualCancelBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        manualSaveBtn = new Button ("SAVE");
        manualSaveBtn.setId ("saveManual");
        manualSaveBtn.setPrefSize (180, 30);
        manualSaveBtn.setPadding (new Insets (1, 3, 1, 3));
        manualSaveBtn.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 28));
        manualSaveBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

        /* ADDING OF CHILDREN */
        buttonLayout.getChildren ().addAll (manualCancelBtn, manualSaveBtn);

        beaconLayout.getChildren ().addAll (beaconText, beaconCoordinates);
        goldLayout.getChildren ().addAll (goldText, goldCoordinates);
        pitLayout.getChildren ().addAll (pitText, pitCoordinates);

        manualInputLayout.setTop (goldLayout);
        manualInputLayout.setLeft (beaconLayout);
        manualInputLayout.setRight (pitLayout);
        manualInputLayout.setBottom (buttonLayout);
        buttonLayout.requestFocus ();

        manualInputScene = new Scene (manualInputLayout, 350, 260);
        manualInputStage = new Stage ();
        manualInputStage.getIcons ().add (new Image (new FileInputStream ("images/goldpick.png")));
        manualInputStage.initStyle (StageStyle.UNDECORATED);
        manualInputStage.setResizable (false);
        manualInputStage.setScene (manualInputScene);
        
        /* REMOVES FOCUS FROM TEXT ARE SO THAT HINT TEXT CAN BE SEEN */
        manualCancelBtn.requestFocus ();
    }

    /**
     * Shows the manual input window.
     */
    public void showManualInput () {
        manualInputStage.showAndWait ();
    }

    /**
     * Closes the manual input window.
     */
    public void closeManualInput () {
        manualInputStage.close ();
    }

    /**
     * Returns the Beacon coordinates inputted by the user. Used in manual board generation.
     * Format: number<space>number.
     * Ex. 
     * 7 6
     * 6 5
     * 4 5
     * 
     * @return  a 2D array containing the coordinates
     */
    public int[][] getBeaconCoordinates () {
        String[] input = beaconCoordinates.getText ().split ("[ \n]");
        int[][] coordinates = new int[input.length/2][2];

        int nCtr = 0;
        for (int i = 0; i < input.length/2; i++) {
            coordinates[i][0] = Integer.parseInt (input[nCtr]);
            coordinates[i][1] = Integer.parseInt (input[nCtr+1]);

            coordinates[i][0] -= 1;
            coordinates[i][1] -= 1;

            nCtr += 2;
        }
        
        return coordinates;
    }

    /**
     * Returns the Pit coordinates inputted by the user. Used in manual board generation.
     * Format: number<space>number.
     * Ex. 
     * 7 6
     * 6 5
     * 4 5
     * 
     * @return  a 2D array containing the coordinates
     */
    public int[][] getPitCoordinates () {
        String[] input = pitCoordinates.getText ().split ("[ \n]");
        int[][] coordinates = new int[input.length/2][2];

        int nCtr = 0;
        for (int i = 0; i < input.length/2; i++) {
            coordinates[i][0] = Integer.parseInt (input[nCtr]);
            coordinates[i][1] = Integer.parseInt (input[nCtr+1]);

            coordinates[i][0] -= 1;
            coordinates[i][1] -= 1;

            nCtr += 2;
        }

        return coordinates;
    }

    /**
     * Returns the Gold coordinates inputted by the user. Used in manual board generation.
     * Format: number<space>number. Ex. 6 7
     * 
     * @return an array containing the coordinates
     */
    public int[] getGoldCoordinates () {
        String[] input = goldCoordinates.getText ().split ("[ \n]");
        int[] coordinates = new int[2];

        coordinates[0] = Integer.parseInt (input[0]);
        coordinates[1] = Integer.parseInt (input[1]);

        coordinates[0] -= 1;
        coordinates[1] -= 1;

        return coordinates;
    }

    /**
     * Updates the radio button looks when a radio button is pressed. Used in the Game Setup scene.
     */
    public void updateRadioBtns () {
        if (randomAgentBtn.isSelected ()) {
            randomAgentBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0; -fx-opacity: 1");
            smartAgentBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0; -fx-opacity: 0.6");
        } else if (smartAgentBtn.isSelected ()) {
            smartAgentBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0; -fx-opacity: 1");
            randomAgentBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0; -fx-opacity: 0.6");
        }

        if (manualBoardBtn.isSelected ()) {
            manualBoardBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0; -fx-opacity: 1");
            randomBoardBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0; -fx-opacity: 0.6");
        } else if (randomBoardBtn.isSelected ()) {
            randomBoardBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0; -fx-opacity: 1");
            manualBoardBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0; -fx-opacity: 0.6");
        }
    }

    /**
     * Returns the value inside the Board Dimension TextField.
     * 
     * @return  the board dimension value
     */
    public int getBoardDimension () {
        return Integer.parseInt (boardDimension.getText ());
    }

    /**
     * Returns the Board Dimension TextField itself. Mainly used for ChangeListener in Controller.
     * 
     * @return  the Board Dimension TextField
     */
    public TextField getDimensionField () {
        return boardDimension;
    }

    /**
     * Returns the agent type currently selected in the AgentType ComboBox.
     * 
     * @return  the agent type
     */
    public String getAgentType () {
        return ((RadioButton) agentType.getSelectedToggle ()).getText ();
    }

    /**
     * Returns the board generation type currently selected in the BoardType Combobox.
     * 
     * @return  the board generation type selected
     */
    public String getGenerationType () {
        return ((RadioButton) boardType.getSelectedToggle ()).getText ();
    }

    /**
     * Disables/Enables the start game button in the Game Setup Scene.
     * 
     * @param   bool    true to disable the button, otherwise false
     */
    public void setDisableStartGameBtn (boolean bool) {
        startGameBtn.setDisable (bool);
    }

    /**
     * Sets the Game Setup Scene as the MainStage's current scene.
     */
    public void showGameSetup () {
        mainStage.setScene (gameSetupScene);
        mainStage.show ();
    }

    /**
     * Setup for About Game Scene.
     */
    public void aboutSetup () throws FileNotFoundException {
        /* INITIALIZE LAYOUT */
        aboutLayout= new BorderPane();
        HBox buttonLayout = new HBox (5);

        /*INITIALIZE TITLE */
        Text aboutGameTitle = new Text ("   ABOUT\nGOLD MINER");
        aboutGameTitle.setFont (Font.loadFont (new FileInputStream("fonts/TITLE.ttf"), 70));
        aboutGameTitle.setFill (Color.LIGHTGRAY);
        aboutGameTitle.setStroke (Color.BLACK);
        aboutGameTitle.setStrokeWidth (1);

        /* CONTENT */
        String instruction1 = ("Agent Goal:\n" +
                "Given a rectangular grid of n x n squares in which\nn is a specific value from 8 to 64 that forms " +
                "the \nmining area and contains four possible objects: the\n" + "miner, the pot of gold, a pit, " +
                "and a beacon, produce\na resulting state where the miner finds its way \ntowards the golden square.");
        Button content = new Button(instruction1);
        content.setPrefSize (580, 200);
        content.setPadding (new Insets (1, 3, 1, 3));
        content.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 20));
        content.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");


        /* FORMAT BUTTONS + BUTTON LAYOUT */
        buttonLayout.setAlignment (Pos.BOTTOM_CENTER);
        //BACK TO MAIN MENU BUTTON
        FileInputStream homeFile = new FileInputStream("images/home.png");
        Image home = new Image(homeFile);
        ImageView homeView = new ImageView(home);
        homeView.setFitHeight(20);
        homeView.setFitWidth(20);
        backButton = new Button ("BACK", homeView);
        backButton.setId ("goBack");
        backButton.setPrefSize (300, 30);
        backButton.setPadding (new Insets (1, 3, 1, 3));
        backButton.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 20));
        backButton.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

        //NEXT BUTTON
        FileInputStream nextFile = new FileInputStream("images/next.png");
        Image next = new Image(nextFile);
        ImageView nextView = new ImageView(next);
        nextView.setFitHeight(20);
        nextView.setFitWidth(20);
        nextButton = new Button ("Next", nextView);
        nextButton.setPrefSize (300, 30);
        nextButton.setPadding (new Insets(1, 3, 1, 3));
        nextButton.setFont (Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 20));
        nextButton.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

        //SET BUTTON IDs
        nextButton.setId ("next");
        backButton.setId ("back");

        /* ADDING OF CHILDREN TO LAY OUTS */
        buttonLayout.getChildren().addAll (backButton, nextButton);
        aboutLayout.setTop (aboutGameTitle);
        aboutLayout.setCenter(content);
        aboutLayout.setBottom (buttonLayout);
        BorderPane.setAlignment(aboutGameTitle, Pos.TOP_CENTER);
        BorderPane.setAlignment(content, Pos.CENTER);
        aboutLayout.setPadding (new Insets (30, 15, 15, 15)); // tRBBL

        /* SETUP BACKGROUND IMAGE/GIF FOR INSTRUCTIONS SCENE */
        BackgroundImage bg = new BackgroundImage (
                new Image (new FileInputStream ("images/ng2.jpg")), BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize (600, 450, false, false, false, false));
        aboutLayout.setBackground (new Background(bg));

        /* SET SCENE*/
        aboutGameScene = new Scene (aboutLayout, 600, 450);
    }
    
    /**
     * Shows the About Game scene.
     */
    public void showAbout () {
        mainStage.setScene (aboutGameScene);
        mainStage.show ();
    }

    public void nextInstruct () throws FileNotFoundException {

        /* CONTENT STRING DEFINITION AND INITIALIZATION */
        String desc2= "Random Agent:\n" + "It does not use any strategy and its behavior is to\n" +
                      "move and execute actions randomly around the\n" +
                      "mining area (grid), which can have a chance of\n" + "reaching the goal state or a resulting state\n" +
                      "wherein the agent falls into the pit.\n\n";
        String desc1= "Smart Agent:\n" + "It uses a formulated goal strategy and analyzes\n" +
                      "the states produced on each valid move done,\n" +
                      "avoiding pits and reaching the goal state in\n" +
                      "shortest amount of moves possible as smaller the\n" +
                      "number of moves, the more rational it is.\n";
        String desc = desc2.concat(desc1);


        /* LAYOUT DESIGN */
        nextLayout = new BorderPane ();
        HBox buttonLayout = new HBox (5);
        buttonLayout.setAlignment(Pos.BOTTOM_LEFT);

        /* ADD BUTTONS TO BUTTON LAYOUT AND FORMAT */
        //image on the button
        FileInputStream homeFile = new FileInputStream("images/home.png");
        Image home = new Image(homeFile);
        ImageView homeView = new ImageView(home);
        homeView.setFitHeight(20);
        homeView.setFitWidth(20);
        //button layout
        back = new Button ("BACK", homeView);
        back.setId ("goBack");
        back.setPrefSize (300, 25);
        back.setPadding (new Insets (1, 3, 1, 3));
        back.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 19));
        back.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");
        buttonLayout.getChildren().addAll (back);
        buttonLayout.setAlignment (Pos.BOTTOM_LEFT);
        buttonLayout.setPadding (new Insets(0, 0, 0, 10)); //T-R-B-L

        /* DESCRIPTION FONT INITIALIZATION AND SET UP */
        Button contentHolder = new Button (desc);
        contentHolder.setFont (Font.loadFont (new FileInputStream("fonts/OPTIONS.otf"), 20));
        contentHolder.setPrefSize (550, 360);
        contentHolder.setPadding (new Insets (1, 3, 1, 3));
        contentHolder.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

        /* INSERTION OF ELEMENTS INTO BORDER PANE */
        nextLayout.setTop (contentHolder);
        nextLayout.setBottom (buttonLayout);
        /* FORMATTING OF BORDER PANE ELEMENTS */
        BorderPane.setAlignment(contentHolder, Pos.TOP_CENTER);
        nextLayout.setPadding (new Insets(15, 15, 15, 15)); //t-r-b-l

        /* FORMATTING OF BUTTONS*/
        back.setStyle ("-fx-border-color: #000000; -fx-border-width: 1px; -fx-background-color: #FFFFF0");

        /* SETUP BACKGROUND IMAGE/GIF FOR INSTRUCTIONS SCENE */
        BackgroundImage bg = new BackgroundImage (
                new Image(new FileInputStream ("images/ng2.jpg")), BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize (600, 450, false, false, false, false));
        nextLayout.setBackground (new Background(bg));
        nextScene = new Scene(nextLayout, 600, 450);
    }

    /**
     * Shows the About Game scene page 2.
     */
    public void showNext () {
        mainStage.setScene (nextScene);
        mainStage.show ();
    }

    /**
     * Setup for the In-Game Scene.
     * 
     * @throws  FileNotFoundException   when the needed files are not found
     */
    public void gameSetup () throws FileNotFoundException {
        /* MAIN GAME LAYOUT */
        gameLayout = new BorderPane ();
        gameLayout.setPadding (new Insets (5, 5, 5, 5));

        // AMBIANCE: Create a Media View for scene graph node as required
        MediaView mvIngame = new MediaView(mpIngame);

        /* INFORMATION PANEL LAYOUT */
        VBox infoLayout = new VBox (17);
        infoLayout.setStyle ("-fx-border-color: #FFFFFF; -fx-border-width: 1px; -fx-background-color: #FFFFFF");
        infoLayout.setPadding (new Insets (5, 5, 5, 5));
        infoLayout.setPrefWidth (150);
        // MAIN TITLE
        Text title = new Text ("STATUS:");
        title.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 28));
        // CURRENT TILE
        currentTile = new Text ("Tile: Dirt");
        currentTile.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));
        // MOVEMENT COUNTER
        moveCtr = new Text ("Moves: 0");
        moveCtr.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));
        // ROTATION COUNTER
        rotateCtr = new Text ("Rotates: 0");
        rotateCtr.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));
        // SCAN COUNTER
        scanCtr = new Text ("Scans: 0");
        scanCtr.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));
        // DIRECTION
        minerDirection = new Text ("Direction: R");
        minerDirection.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));

        // DISTANCE FROM GOLD
        goldDistance = new Text ("Distance: 0");
        goldDistance.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));

        // SLIDER
        speedSlider = new Slider();
        speedSlider.setMin(1);
        speedSlider.setMax(100);
        speedSlider.setValue(50);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(100);

        Text sliderLabel = new Text ("Speed:");
        sliderLabel.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));

        HBox sliderLayout = new HBox (5);

        ImageView slowIcon = new ImageView (
            new Image (new FileInputStream ("images/turtle.png"), 24, 24, true, true)
        );
        ImageView fastIcon = new ImageView (
            new Image (new FileInputStream ("images/bunny.png"), 22, 22, true, true)
        );

        sliderLayout.getChildren ().addAll (fastIcon, speedSlider, slowIcon);

        // GOLD COORDINATES
        minerStatus = new Text ("Miner:\nStarting...");
        minerStatus.setFont(Font.loadFont (new FileInputStream ("fonts/MCRegular.otf"), 18));

        /* SETUP BACKGROUND IMAGE/GIF FOR INSTRUCTIONS SCENE */
        BackgroundImage bg = new BackgroundImage (
                new Image (new FileInputStream ("images/black.jpg")), BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize (620, 470, false, false, false, false));
        gameLayout.setBackground (new Background(bg));
        
        /* ADDING OF CHILDREN */
        infoLayout.getChildren ().addAll (title, minerStatus, currentTile, moveCtr, rotateCtr, scanCtr, minerDirection, goldDistance, sliderLabel, sliderLayout);
        gameLayout.setLeft (infoLayout);
        gameLayout.getChildren().add(mvIngame);

        inGameScene = new Scene (gameLayout, 596, 446); // dimension * 77.5, dimension * 58.75
    }

    /**
     * Shows the in-game scene on the main stage.
     */
    public void showInGame () {
        mpMainMenu.setMute(true);
        // Automatically play + continue lang
        mpIngame.setAutoPlay(true);
        mpIngame.setMute(false);
        mpIngame.setCycleCount(MediaPlayer.INDEFINITE);
        mainStage.setScene (inGameScene);
        mainStage.centerOnScreen ();
        mainStage.show ();
    }

    /**
     * Updates the info found on the Information Panel on the left side of the in-game scene.
     * 
     * @param moveCtr   the current movement counter of the miner agent
     * @param rotateCtr the current rotate counter of the miner agent
     * @param scanCtr   the current scan counter of the miner agent
     * @param currTile  the current tile of the miner agent
     * @param direction the current direction the miner is facing
     */
    public void updateInfoPanel (int moveCtr, int rotateCtr, int scanCtr, Tile currTile, char direction) {
        /* UPDATE CURRENT TILE INFO */
        if (currTile instanceof Gold)
            currentTile.setText ("Tile: Gold");
        else if (currTile instanceof Pit)
            currentTile.setText ("Tile: Pit");
        else if (currTile instanceof Beacon)
            currentTile.setText ("Tile: Beacon");
        else currentTile.setText ("Tile: Dirt");

        /* UPDATE COUNTER INFO */
        this.moveCtr.setText ("Moves: " + moveCtr);
        this.rotateCtr.setText ("Rotates: " + rotateCtr);
        this.scanCtr.setText ("Scans: " + scanCtr);

        /* UPDATE DIRECTION INFO */
        minerDirection.setText ("Direction: " + direction);

        /* UPDATE GOLD LOCATION */
        if (currTile instanceof Beacon && ((Beacon) currTile).getDistance () > 0)
            minerStatus.setText ("Miner:\nGold nearby!");
        else if (currTile instanceof Gold)
            minerStatus.setText ("Miner:\nGold! <3");
        else minerStatus.setText ("Miner:\nSearching...");

        if (currTile instanceof Pit)
            minerStatus.setText ("Miner:\n*burns*");

        /* UPDATE BEACON DISTANCE FROM GOLD */
        if (currTile instanceof Beacon)
            goldDistance.setText ("Distance: " + ((Beacon) currTile).getDistance ());
        else goldDistance.setText ("Distance: 0");
    }

    /**
     * Board (cave) setup for the in-game scene. Uses the board generated by the Model to create a 
     * GUI representation of it.
     * 
     * @param cave  the generated board (cave) that will be used as reference for generating a board GUI
     */
    public void boardSetup (Cave cave) {
        /* GRIDPANE FOR THE WHOLE BOARD STRUCTURE */
        board = new GridPane ();
        board.setPadding (new Insets (10, 10, 10, 10));

        /* TO STORE THE TILES OF THE GENERATED CAVE */
        Tile[][] generatedBoard = cave.getTiles ();

        /* ARRAYLIST<STACKPANE> FOR TEMPORARY STORING OF STACKPANES */
        tiles = new ArrayList<>();

        try {
            int k = 0;
            /* GO THROUGH EACH CAVE TILE */
            for (int row = 0; row < generatedBoard.length; row++) {
                for (int col = 0; col < generatedBoard.length; col++) {
                    /* CREATE A STACKPANE FOR EACH CAVE TILE */
                    tiles.add (new StackPane ());
                    /* IF GOLD */
                    if (generatedBoard[row][col] instanceof Gold) {
                        tiles.get (k).getChildren ().add (new ImageView (
                            new Image (new FileInputStream ("images/gold.png"), 50, 50, true, true)
                        ));
                    /* IF PIT */
                    } else if (generatedBoard[row][col] instanceof Pit) {
                        tiles.get (k).getChildren ().add (new ImageView (
                            new Image (new FileInputStream ("images/lava.gif"), 50, 50, true, true)
                        ));
                    /* IF BEACON */
                    } else if (generatedBoard[row][col] instanceof Beacon) {
                        tiles.get (k).getChildren ().add (new ImageView (
                            new Image (new FileInputStream ("images/beacon.png"), 50, 50, true, true)
                        ));
                    /* IF DIRT */
                    } else {
                        tiles.get (k).getChildren ().add (new ImageView (
                            new Image (new FileInputStream ("images/dirt.jpg"), 50, 50, true, true)
                        ));
                    }
                    /* INCREMENT TO POINT TO NEXT STACKPANE */
                    k++;
                }
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println ("Board assets missing! " + fnfe.toString ());
        }

        int dimensions = getBoardDimension ();

        int k = 0;

        /* ADDING OF STACKPANES TO GRIDPANE */
        for (int row = 0; row < dimensions; row++) {
            for (int col = 0; col < dimensions; col++) {
                /* SET GRIDPAIN CHILDREN CONSTRAINTS */
                board.setConstraints (tiles.get (k), col, row);
                /* ADD CHILDREN TO GRIDPANE */
                board.getChildren ().add (tiles.get (k));
                /* POINT TO NEXT STACK PANE */
                k++;
            }
        }

        /* TO SET HORIZONTAL AND VERTICAL GAPS BETWEEN EACH TILE */
        board.setHgap (2);
        board.setVgap (2);

        /* TO ENABLE SCROLLING FOR HUGE BOARDS */
        ScrollPane scroll = new ScrollPane (board);
        scroll.setStyle ("-fx-background: #000000; -fx-background-color: #000000");
        gameLayout.setCenter (scroll);

        /* SETUP MINER AGENT GIFS */
        try {
            minerNorth = new ImageView (new Image (new FileInputStream ("images/miner_up.gif"), 50, 50, true, true));
            minerSouth = new ImageView (new Image (new FileInputStream ("images/miner_down.gif"), 50, 50, true, true));
            minerEast = new ImageView (new Image (new FileInputStream ("images/miner_right.gif"), 50, 50, true, true));
            minerWest = new ImageView (new Image (new FileInputStream ("images/miner_left.gif"), 50, 50, true, true));
        } catch (FileNotFoundException fnfe) {
            System.out.println ("Miner Image not found! " + fnfe.toString ());
        }

        /* TO SET INITIAL POSITION OF MINER AGENT */
        updateBoard (0, 0, 'R');
    }

    /**
     * Updates the position of the miner agent on the board (cave).
     * 
     * @param minerRow  the current row of the miner agent
     * @param minerCol  the current column of the miner agent
     * @param direction the current direction the miner agent is facing
     */
    public void updateBoard (int minerRow, int minerCol, char direction) {
        /* TO REMOVE MINER AGENT IMAGE FROM PREVIOUS POSITION */
        removeMiner ();
        /* ADD MINER AGENT TO NEW POSITION */
        switch (direction) {
            /* FACING UP */
            case 'U':
                board.setConstraints (minerNorth, minerCol, minerRow);
                board.getChildren ().add (minerNorth);
            break;
            /* FACING DOWN */
            case 'D':
                board.setConstraints (minerSouth, minerCol, minerRow);
                board.getChildren ().add (minerSouth);
            break;
            /* FACING RIGHT */
            case 'R':
                board.setConstraints (minerEast, minerCol, minerRow);
                board.getChildren ().add (minerEast);
            break;
            /* FACING LEFT */
            case 'L':
                board.setConstraints (minerWest, minerCol, minerRow);
                board.getChildren ().add (minerWest);
            break;
        }
    }

    /**
     * Removes all miner agent images from the board.
     */
    public void removeMiner () {
        board.getChildren ().remove (minerNorth);
        board.getChildren ().remove (minerSouth);
        board.getChildren ().remove (minerEast);
        board.getChildren ().remove (minerWest);
    }

    /**
     * Returns the current value of the slider corresponding to the animation speed of Timeline.
     * 
     * @return  the current value of the slider
     */
    public int getAnimationSpeed () {
        return (int) speedSlider.getValue ();
    }


    public void displayResult (String title, int nMoves, int nRotates, int nScan, String url) {
        /* MOUSE CLICK SOUND FX */
        // MOUSE CLICK: Locate the sound fx in CLASSPATH
        URL mouseUrl = getClass().getResource("sound/mouse.mp3");
        String mouseStringUrl = mouseUrl.toExternalForm();
        // MOUSE CLICK: Create a Media from the mp3
        Media mouse = new Media(mouseStringUrl);
        // MOUSE CLICK: Create a Media Player to play
        MediaPlayer mpMouse2 = new MediaPlayer (mouse);
        //to stop the sound fx currently playing
        mpIngame.setMute(true);
        // SUCCESS, FAIL: Locate the sound fx in CLASSPATH
        URL resultUrl = getClass().getResource(url);
        String resultStringUrl = resultUrl.toExternalForm();
        Media resultMedia = new Media(resultStringUrl);
        MediaPlayer mpResult = new MediaPlayer (resultMedia);
        // Automatically play + continue lang
        mpResult.setAutoPlay(true);
        try {

            MediaView mvResult = new MediaView(mpResult);
            /* FORMAT LAYOUTS */
            Stage resultWindow = new Stage();

            BorderPane resultBoxLayout = new BorderPane();
            VBox contentLayout = new VBox (5);
            HBox buttonLayout = new HBox (5);
            buttonLayout.setAlignment (Pos.BOTTOM_CENTER);
            contentLayout.setAlignment (Pos.CENTER);
            //blocks input events from being delivered to all windows from the same application
            // resultWindow.initModality(Modality.APPLICATION_MODAL);
            resultWindow.initStyle (StageStyle.UNDECORATED);
            resultWindow.setResizable (false);

            /* INITIALIZE AND SET UP THE TEXT AND BUTTONS */
            //Text: search result
            Text resultBoxText = new Text (title);
            resultBoxText.setFont (Font.loadFont (new FileInputStream("fonts/OPTIONS.otf"), 15));
            //Text: no of moves
            Button statusBox1= new Button ("No. of Moves: " + nMoves);
            statusBox1.setPrefSize (500, 30);
            statusBox1.setPadding (new Insets(1, 3, 1, 3));
            statusBox1.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 15));
            statusBox1.setStyle ("-fx-border-color: #000000; -fx-border-width: 1.5px; -fx-background-color: #696969; -fx-text-fill: #F8F8FF");
            //Text: no of rotations
            Button statusBox2= new Button ("No. of Rotations: " + nRotates);
            statusBox2.setPrefSize (500, 30);
            statusBox2.setPadding (new Insets(1, 3, 1, 3));
            statusBox2.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 15));
            statusBox2.setStyle ("-fx-border-color: #000000; -fx-border-width: 1.5px; -fx-background-color: #696969; -fx-text-fill: #F8F8FF");
            //Text: no of scans
            Button statusBox3= new Button ("No. of Scans: " + nScan);
            statusBox3.setPrefSize (500, 30);
            statusBox3.setPadding (new Insets(1, 3, 1, 3));
            statusBox3.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 15));
            statusBox3.setStyle ("-fx-border-color: #000000; -fx-border-width: 1.5px; -fx-background-color: #696969; -fx-text-fill: #F8F8FF");
            // BACK TO MAIN MENU BUTTON
            Button backBtn = new Button("Main Menu");
            backBtn.setId ("mainMenuFinish");
            backBtn.setPrefSize (500, 30);
            backBtn.setPadding (new Insets(1, 3, 1, 3));
            backBtn.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 15));
            backBtn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1.5px; -fx-background-color: #DCDCDC");
            backBtn.setOnAction((e) -> {
                // Automatically play + continue lang
                mpMouse2.setAutoPlay(true);
                mpMainMenu.setMute(false);
                showMainMenu ();
                speedSlider.setValue (50);
                resultWindow.close();
            });
            // TRY AGAIN BUTTON
            Button tryagn = new Button("Try Again");
            tryagn.setId ("tryAgain");
            tryagn.setPrefSize (500, 30);
            tryagn.setPadding (new Insets(1, 3, 1, 3));
            tryagn.setFont(Font.loadFont (new FileInputStream ("fonts/OPTIONS.otf"), 15));
            tryagn.setStyle ("-fx-border-color: #000000; -fx-border-width: 1.5px; -fx-background-color: #DCDCDC");
            tryagn.setOnAction((e) -> {
                // Automatically play + continue lang
                mpMouse2.setAutoPlay(true);
                showGameSetup ();
                speedSlider.setValue (50);
                resultWindow.close();
            });

                /* ADDING OF CHILDREN TO LAYOUTS */
                BorderPane.setAlignment(resultBoxText, Pos.TOP_LEFT);
                buttonLayout.getChildren ().addAll (backBtn, tryagn);
                contentLayout.getChildren ().addAll (statusBox1, statusBox2, statusBox3);
                resultBoxLayout.setTop (resultBoxText);
                resultBoxLayout.setCenter (contentLayout);
                resultBoxLayout.setBottom (buttonLayout);
                resultBoxLayout.setPadding (new Insets (7, 10, 45, 10));

                /* SETUP BACKGROUND IMAGE/GIF FOR RESULT BOX SCENE */
                BackgroundImage bg = new BackgroundImage (
                        new Image(new FileInputStream ("images/pop.jpg")), BackgroundRepeat.REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                        new BackgroundSize (600, 450, false, false, false, false));
                resultBoxLayout.setBackground (new Background(bg));
                resultBoxLayout.getChildren().add(mvResult);

                /* SET SCENE */
                Scene resultBoxScene = new Scene(resultBoxLayout, 500, 250);

                resultWindow.setScene(resultBoxScene);
                resultWindow.show();
            } catch (Exception e) {
                System.out.println ("File/s needed not found! Exiting!" + e.toString ());
                Platform.exit ();
            }
    }

    /**
     * Event Handlers for all the GUI elements.
     * 
     * @param   controller  the Controller for the GUI elements
     */
    public void setEventHandlers (Controller controller) {
        /* MAIN MENU BUTTONS */
        startBtn.setOnAction ((EventHandler) controller);
        instructionsBtn.setOnAction ((EventHandler) controller);
        exitBtn.setOnAction ((EventHandler) controller);

        /* ABOUT THE GAME BUTTONS */
        backButton.setOnAction((EventHandler) controller);
        nextButton.setOnAction((EventHandler) controller);
        back.setOnAction((EventHandler) controller);
    
        /* GAME SETUP ELEMENTS */
        backBtn.setOnAction ((EventHandler) controller);
        startGameBtn.setOnAction ((EventHandler) controller);
        boardDimension.textProperty ().addListener (controller);

        randomAgentBtn.setOnMouseClicked ((EventHandler) controller);
        smartAgentBtn.setOnMouseClicked ((EventHandler) controller);

        /* MANUAL COORDINATE INPUT BUTTONS */
        manualBoardBtn.setOnMouseClicked ((EventHandler) controller);
        randomBoardBtn.setOnMouseClicked ((EventHandler) controller);

        manualCancelBtn.setOnAction ((EventHandler) controller);
        manualSaveBtn.setOnAction ((EventHandler) controller);
    }

    /**
     * Exits the whole application.
     */
    public void exitGame () {
        Platform.exit ();
    }
}
