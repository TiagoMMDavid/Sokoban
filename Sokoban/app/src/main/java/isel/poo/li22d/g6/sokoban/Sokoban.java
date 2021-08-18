package isel.poo.li22d.g6.sokoban;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import isel.poo.li22d.g6.sokoban.model.Dir;
import isel.poo.li22d.g6.sokoban.model.Game;
import isel.poo.li22d.g6.sokoban.model.Level;
import isel.poo.li22d.g6.sokoban.model.Loader;
import isel.poo.li22d.g6.sokoban.view.LevelView;

import static java.lang.Math.abs;

public class Sokoban extends AppCompatActivity {
    private static final String LEVELS_FILE = "levels.txt" ;        // Name of levels file
    private static final int SWIPE_DEADZONE_RADIUS = 150;

    private final Game MODEL = new Game();                          // Model of game
    private Level level;                                            // Model of current level
    private LevelView view;                                         // View of level
    private final GameListener gameListener = new GameListener();   // Listener of game events

    private boolean canMove = true;
    private boolean isFirstLaunch = true;

    private static final String FIRST_LAUNCH_KEY = "isel.poo.li22d.g6.Sokoban.isFirstLaunch";
    private static final String MODEL_KEY = "isel.poo.li22d.g6.Sokoban.MODEL";
    private static final String LEVEL_KEY = "isel.poo.li22d.g6.Sokoban.level.number";
    private static final String CAN_MOVE_KEY = "isel.poo.li22d.g6.Sokoban.canMove";

    private Button levelFinishedButton;
    private Button restartLevelButton;
    private TextView messageBox;
    private TextView numOfMoves;
    private TextView remainingBoxes;
    private TextView levelNumber;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameplay_layout);

        // Level Finished Button
        levelFinishedButton = findViewById(R.id.nextLevelButton);

        // Message Box
        messageBox = findViewById(R.id.messageBox);

        // Restart Level Button
        restartLevelButton = findViewById(R.id.restartLevelButton);
        restartLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartCurrentLevel();
            }
        });

        // Number of Moves
        numOfMoves = findViewById(R.id.numOfMoves);

        // Remaining Boxes
        remainingBoxes = findViewById(R.id.numOfBoxes);

        // Level Number
        levelNumber = findViewById(R.id.levelNumber);

        // Play Area
        view = findViewById(R.id.levelView);

        // Swipe Area
        View swipeZone = findViewById(R.id.swipeZone);
        swipeZone.setOnTouchListener(new View.OnTouchListener() {
            private int startX, startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!canMove) return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        x = x - startX;
                        y = y - startY;
                        if (abs(x) <= SWIPE_DEADZONE_RADIUS && abs(y) <= SWIPE_DEADZONE_RADIUS)
                            return false;

                        Dir dir;

                        // X axis movements
                        if (abs(x) > abs(y)) {
                            dir = x > 0 ? Dir.RIGHT : Dir.LEFT;
                        }
                        else {
                            dir = y > 0 ? Dir.DOWN : Dir.UP;
                        }
                        level.moveMan(dir);
                }
                return true;
            }
        });

        // Check if it's coming from a previously saved state
        if (savedInstanceState != null) {
            Scanner in = new Scanner(savedInstanceState.getString(MODEL_KEY));
            try {
                level = MODEL.loadState(in, savedInstanceState.getInt(LEVEL_KEY));
            }
            catch (Loader.LevelFormatException e) {
                Log.e("Sokoban", "Error reloading state");
            }
            isFirstLaunch = savedInstanceState.getBoolean(FIRST_LAUNCH_KEY);
            if (level.manIsDead()) {
                setLayoutToManDead();
            }
            else {
                setDefaultLayout(level.isFinished());
            }
            initLevel(savedInstanceState.getBoolean(CAN_MOVE_KEY));
        }
        else nextLevel();

    }

    /**
     * Tries to load the level that's next to this one.
     * @return the result of the attempt of loading the level.
     */
    private boolean nextLevel() {
        try(InputStream file = getAssets().open(LEVELS_FILE)) {
            if ((level = MODEL.loadNextLevel(file)) == null)
                return false;
        } catch (Loader.LevelFormatException e) {
            Log.e("Sokoban", e.getMessage());
        } catch(FileNotFoundException io) {
            Log.e("Sokoban", "Levels file not found");
        } catch(IOException io) {
            Log.e("Sokoban", "Error while loading the levels file!");
        }
        setDefaultLayout(false);
        initLevel(true);
        return true;
    }

    /**
     * Initiates the current level by setting the listener and initiating the view
     * @param canMove Value to define if the player can be moved or not.
     */
    private void initLevel(boolean canMove) {
        view.initModel(this,level);
        level.setGameListener(gameListener);
        this.canMove = canMove;
    }

    /**
     * Restarts the current level by restarting the MODEL and re-initiating the view
     */
    private void restartCurrentLevel() {
        try(InputStream file = getAssets().open(LEVELS_FILE)) {
            MODEL.restart(file);
        } catch(FileNotFoundException io) {
            Log.e("Sokoban", "Levels file not found");
        } catch(IOException io) {
            Log.e("Sokoban", "Error while loading the levels file!");
        }
        view.initModel(this, level);
        canMove = true;
        setDefaultLayout(false);
        clearMessage();
    }

    /**
     * Writes a message on the message box
     * @param stringId The ID of the string to be written
     */
    private void writeMessage(int stringId) {
        messageBox.setText(stringId);
    }

    /**
     * Writes a blank text message in the message box, in order to clean it
     */
    private void clearMessage() {
        messageBox.setText("");
    }

    /**
     * Sets the layout (buttons, level view, etc.) to its default state,
     * as in, checks every parameter in order to set it to the state you'd expect
     * when the level starts.
     * @param isLoadingFromFinishedState if it's loading from a state where the game has finished.
     */
    private void setDefaultLayout(boolean isLoadingFromFinishedState) {
        if (!isLoadingFromFinishedState) hideAndDisableLevelFinishButton();
        levelFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMessage();
                if (!nextLevel()) {
                    setLayoutToNoMoreLevels();
                }
            }
        });
        restartLevelButton.setEnabled(level.getMoves() >= 1);
        remainingBoxes.setText(Integer.toString(level.getRemainingBoxes()));
        levelNumber.setText(Integer.toString(level.getNumber()));
        numOfMoves.setText(Integer.toString(level.getMoves()));
        if (isFirstLaunch)
            writeMessage(R.string.welcome);
    }

    /**
     * Sets the layout to the state of where there's no more levels to be loaded
     */
    private void setLayoutToNoMoreLevels() {
        restartLevelButton.setEnabled(false);
        levelFinishedButton.setText(R.string.exit);
        levelFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        writeMessage(R.string.no_more_levels);
    }

    /**
     * Sets the layout to the state of when the level has been finished.
     */
    private void setLayoutToLevelFinish() {
        showAndEnableLevelFinishButton(R.string.next_level);
        writeMessage(R.string.level_finished);
    }

    /**
     * Sets the layout to the state of when the player has died (in this case, drowned)
     */
    private void setLayoutToManDead() {
        showAndEnableLevelFinishButton(R.string.exit);
        levelFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        writeMessage(R.string.man_dead);
    }

    /**
     * Enables the level finished button, and makes it visible with a message.
     * @param stringId the message to be written in the button
     */
    private void showAndEnableLevelFinishButton(int stringId) {
        levelFinishedButton.setEnabled(true);
        levelFinishedButton.setText(stringId);
        levelFinishedButton.setVisibility(View.VISIBLE);
    }

    /**
     * Disables the level finished button, and turns it invisible.
     */
    private void hideAndDisableLevelFinishButton() {
        levelFinishedButton.setEnabled(false);
        levelFinishedButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Updates the move count on the move counter, and enables the restart level
     * button if the number of moves has reached "1"
     */
    private void updateLayoutMoveCount() {
        int moves = level.getMoves();
        numOfMoves.setText(Integer.toString(moves));
        if (moves == 1) {
            restartLevelButton.setEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MODEL_KEY, MODEL.saveState());
        outState.putInt(LEVEL_KEY, level.getNumber());
        outState.putBoolean(FIRST_LAUNCH_KEY, isFirstLaunch);
        outState.putBoolean(CAN_MOVE_KEY, canMove);
    }

    private class GameListener implements Level.GameEventListener {
        @Override
        public void manMoved() {
            updateLayoutMoveCount();

            // Clear welcome message and disable first launch
            if (isFirstLaunch) {
                clearMessage();
                isFirstLaunch = false;
            }
            level.isFinished();
        }

        @Override
        public void manNotMoved() {
        }

        @Override
        public void levelFinished() {
            setLayoutToLevelFinish();
            canMove = false;
        }

        @Override
        public void manDied() {
            setLayoutToManDead();
            canMove = false;
        }

        @Override
        public void boxPlaced() {
            remainingBoxes.setText(Integer.toString(level.getRemainingBoxes()));
        }

        @Override
        public void boxMoved() {
        }

        @Override
        public void boxRemoved() {
            remainingBoxes.setText(Integer.toString(level.getRemainingBoxes()));
        }

        @Override
        public void boxInHole() {
        }
    }
}
