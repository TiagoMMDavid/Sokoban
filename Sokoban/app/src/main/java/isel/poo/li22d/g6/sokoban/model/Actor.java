package isel.poo.li22d.g6.sokoban.model;

public abstract class Actor {
    private final char TYPE;

    /**
     * Constructs an Actor based on the type passed as parameter.
     * @param type the type of Actor to be constructed.
     */
    protected Actor(char type) {
        this.TYPE = type;
    }

    /**
     * Gets the type of this Actor.
     * @return the value of the field TYPE.
     */
    public char getType() {
        return TYPE;
    }

    /**
     * Checks if the Actor can be moved to a certain Cell.
     * Since an Actor depends on next 2 Cells to know if it
     * can be moved (e.g. the Man needs to check if the Box
     * can be moved, but the Box also needs to check if it
     * can be moved itself), we need to specify the next 2
     * cells as parameters.
     * @param nextCell the next immediate Cell
     * @param lastCell the Cell next to nextCell
     * @return the result of the evaluation of movement possibility
     */
    public abstract boolean canMove(Cell nextCell, Cell lastCell);
}
