package isel.poo.li22d.g6.sokoban.model;

import java.util.Scanner;

import isel.poo.li22d.g6.sokoban.model.cell.*;
import isel.poo.li22d.g6.sokoban.model.cell.Void;

import static isel.poo.li22d.g6.sokoban.model.Cell.*;

public class Level {
    private final int LEVEL_NUMBER;

    /**
     * Array containing the position of various Cells.
     * Coordinates are defined as [Y][X].
     */
    private final Cell[][] POS;
    private final int HEIGHT, WIDTH;

    private Observer observer;
    private GameEventListener gameListener;

    // To be used by StatusPanel
    private int remainingBoxes;
    private int moves;

    // Man's current position
    private int playerPosX, playerPosY;

    /**
     * Interface of a listener (also called observer) that listens for events related
     * to the board (e.g. a board cell was replaced by another one).
     */
    public interface Observer {
        void cellUpdated(int l, int c, Cell cell);

        void cellReplaced(int l, int c, Cell cell);
    }


    /**
     * Interface of a listener that listens to game specific events (e.g. The player moved,
     * the level has finished, a box was placed...).
     */
    public interface GameEventListener {
        void manMoved();
        void manNotMoved();
        void levelFinished();
        void manDied();
        void boxPlaced();
        void boxMoved();
        void boxRemoved();
        void boxInHole();
    }

     /**
     * Creates a new level.
     * @param levelNumber The number of the level.
     * @param height The level's height
     * @param width The level's width
     */
    public Level(int levelNumber, int height, int width) {
        this.LEVEL_NUMBER = levelNumber;
        POS = new Cell[this.HEIGHT = height][this.WIDTH = width];
    }

    public void init(Game game) {
    }

    /**
     * Reset's the level, by resetting the number of moves and remaining boxes.
     */
    public void reset() {
        moves = 0;
        remainingBoxes = 0;
    }
    /**
     * Getter methods for various level field values.
     * @return In order:
     *      - Level's number
     *      - Number of moves made
     *      - Remaining boxes
     *      - Level's width
     *      - Level's height
     */
    public int getNumber() {
        return LEVEL_NUMBER;
    }
    public int getMoves() {
        return this.moves;
    }
    public int getRemainingBoxes() {
        return this.remainingBoxes;
    }
    public int getWidth() {
        return this.WIDTH;
    }
    public int getHeight() {
        return this.HEIGHT;
    }

    /**
     * Updates a Cell of the level, i.e. inserts a Cell in the
     * POS array, containing the various Cells of the level.
     * Also updates player position, if it's a player type, or increments
     * box count if it's a box type.
     * @param line Line of the cell
     * @param col Column of the cell
     * @param type Cell type
     */
    public void put(int line, int col, char type) {
        switch (type) {
            case VOID:
                POS[line][col] = new Void();
                break;
            case FLOOR:
                POS[line][col] = new Floor();
                break;
            case OBSTACLE:
                POS[line][col] = new Obstacle();
                break;
            case TARGET:
                POS[line][col] = new Target();
                break;
            case HOLE:
                POS[line][col] = new Hole();
                break;

            case BOX:
                if (POS[line][col] == null) {
                    POS[line][col] = new Floor();
                }
                POS[line][col].addActor(BOX);
                if (POS[line][col].getType() != TARGET) {
                    ++remainingBoxes;
                }
                break;
            case MAN:
                if (POS[line][col] == null) {
                    POS[line][col] = new Floor();
                }
                POS[line][col].addActor(MAN);
                playerPosY = line;
                playerPosX = col;
        }
    }

    /**
     * Evaluates if either the player is dead or not.
     * @return "true" if the player (known as Man) is dead, i.e fell on a hole.
     */
    public boolean manIsDead() {
        return (POS[playerPosY][playerPosX].getType() == HOLE);
    }

    /**
     * Evaluates if the level has finished, either by completing it,
     * or by killing the player (Man).
     * @return "true" if the level has been finished.
     */
    public boolean isFinished() {
        if (manIsDead()) {
            gameListener.manDied();
            return true;
        }
        if (remainingBoxes == 0) {
            gameListener.levelFinished();
            return true;
        }
        else return false;
    }

    /**
     * Moves the player's (Man) position in a designated
     * direction, if it's able to be moved.
     * Also moves a box, if it exists (i.e. calls the moveBoxIfNeeded method)
     * @param dir containing the X and Y vector of the next movement
     */
    public void moveMan(Dir dir) {
        int nextPosX = playerPosX + dir.dX;
        int nextPosY = playerPosY + dir.dY;
        int lastPosX = playerPosX + (dir.dX * 2);
        int lastPosY = playerPosY + (dir.dY * 2);

        Cell nextCell = getCell(nextPosY,nextPosX);
        Cell playerCell = getCell(playerPosY,playerPosX);

        Cell lastCell;
        if ( lastPosY < 0 || lastPosY >= HEIGHT || lastPosX < 0 || lastPosX >= WIDTH)
            lastCell = null;
        else
            lastCell = getCell(lastPosY,lastPosX);

        if (playerCell.getActor().canMove(nextCell, lastCell)) {
            moveBoxIfNeeded(nextCell,lastPosY,lastPosX);

            nextCell.addActor(MAN);
            observer.cellReplaced(nextPosY, nextPosX, nextCell);
            playerCell.removeActor();
            observer.cellReplaced(playerPosY, playerPosX, playerCell);

            playerPosX += dir.dX;
            playerPosY += dir.dY;
            ++moves;

            gameListener.manMoved();
        }
        else {
            gameListener.manNotMoved();
        }
    }

    /**
     * Moves the box in currCell, if it exists.
     * @param currCell Cell where the box is supposedly present
     * @param nextPosY Y coordinate of the Box's next position
     * @param nextPosX X coordinate of the Box's next position
     */
    private void moveBoxIfNeeded(Cell currCell, int nextPosY, int nextPosX) {
        if (currCell.hasActor() && currCell.getActorType() == BOX) {
            Cell nextCell = POS[nextPosY][nextPosX];
            char currCellType = currCell.getType();

            switch (nextCell.getType()) {
                case HOLE:
                    POS[nextPosY][nextPosX] = nextCell = new Floor();
                    gameListener.boxInHole();
                    break;
                case TARGET:
                    if (currCellType != TARGET) --remainingBoxes;
                    gameListener.boxPlaced();
                default:
                    nextCell.addActor(BOX);
            }

            if (currCellType == TARGET && nextCell.getType() != TARGET) {
                ++remainingBoxes;
                gameListener.boxRemoved();
            }

            observer.cellReplaced(nextPosY, nextPosX, nextCell);
            gameListener.boxMoved();
        }
    }

    /**
     * Getter method for the Cell in a specified position.
     * @param line Line where the Cell is positioned
     * @param col Column where the Cell is positioned
     * @return the Cell in the specified position.
     */
    public Cell getCell(int line, int col) {
        return POS[line][col];
    }


    /**
     * Sets the observer of the current level.
     * @param observer The observer object to be set
     */
    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    /**
     * Sets the listener for game events of the current level.
     * @param listener The GameEventListener object to be set.
     */
    public void setGameListener(GameEventListener listener) {
        this.gameListener = listener;
    }

    /**
     * Saves the current state of the level to a String.
     * The format is similar to how levels are formatted in the levels
     * text file, but it includes the ammount of moves at the end.
     * @return A String representation of the current level status
     */
    public String saveState() {
        StringBuilder builder = new StringBuilder();
        StringBuilder multiCharBuilder = new StringBuilder();

        builder.append('#')
                .append(LEVEL_NUMBER)
                .append(' ')
                .append(HEIGHT)
                .append(' ').append('x').append(' ')
                .append(WIDTH);

        char multiChar = 'a';
        for (Cell[] pos : POS) {
            builder.append('\n');
            for(Cell cell : pos) {
                if (cell.hasActor()) {
                    if (cell.getType() != FLOOR) {
                        builder.append(multiChar);
                        multiCharBuilder.append(multiChar++)
                                .append('=')
                                .append(cell.getType())
                                .append(cell.getActorType())
                                .append('\n');
                    }
                    else
                        builder.append(cell.getActorType());
                }
                else builder.append(cell.getType());
            }
        }
        builder.append('\n');
        builder.append(multiCharBuilder.toString());
        builder.append(moves);

        return builder.toString();
    }


    public void loadState(Scanner in) {
        moves = in.nextInt();
    }
}
