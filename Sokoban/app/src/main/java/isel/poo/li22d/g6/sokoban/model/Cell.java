package isel.poo.li22d.g6.sokoban.model;

import isel.poo.li22d.g6.sokoban.model.cell.actor.Box;
import isel.poo.li22d.g6.sokoban.model.cell.actor.Man;

/**
 * Represents the various positions that are available in a level.
 */
public abstract class Cell {
    public static final char VOID = '.';
    public static final char FLOOR = ' ';
    public static final char OBSTACLE = 'X';
    public static final char TARGET = '*';
    public static final char HOLE = 'H';
    public static final char BOX = 'B';
    public static final char MAN = '@';
    public static final char NO_ACTOR = '!';

    private final char TYPE;

    private boolean hasActor;
    private Actor actor;

    /**
     * Constructs a Cell based on the type passed as parameter.
     * @param type the type of Cell to be constructed.
     */
    protected Cell(char type) {
        this.TYPE = type;
    }

    /**
     * Removes the actor present in the cell. Since the getter method only returns
     * the Actor type if the field hasActor is true, this method only needs to set
     * that field to "false" to remove the actor.
     */
    public void removeActor() {
        hasActor = false;
    }

    /**
     * Adds an actor to the current cell, if there's no box coming to an hole.
     * @param type the type of Actor object that will be created.
     */
    public void addActor(char type) {
        if (!isBoxAndHasHole(type)) {
            hasActor = true;
            this.actor = ((type == MAN) ? new Man() : new Box());
        }
        else hasActor = false;
    }

    /**
     * Checks if the coming type is a box, and if the current Cell type is an hole.
     * @param type the type of Actor that will be coming to the cell.
     * @return
     */
    private boolean isBoxAndHasHole(char type) {
        return type == BOX && this.TYPE == HOLE;
    }

    /**
     * Gets the current Cell type.
     * @return type of cell.
     */
    public char getType() {
        return TYPE;
    }

    /**
     * Gets the Actor that's contained in this cell. If none, gives a null pointer.
     * @return Actor present in cell. If none, null.
     */
    public Actor getActor() {
        if (!hasActor) return null;
        return actor;
    }

    /**
     * Gets the type of the Actor present in the current Cell.
     * @return The Actor type. If there's no Actor, it returns the constant NO_ACTOR.
     */
    public char getActorType() {
        if (!hasActor) return NO_ACTOR;
        return actor.getType();
    }

    /**
     * Checks if the cell has an Actor present in it.
     * @return the value of the field hasActor.
     */
    public boolean hasActor() {
        return hasActor;
    }
}