package isel.poo.li22d.g6.sokoban.model.cell.actor;

import isel.poo.li22d.g6.sokoban.model.Actor;
import isel.poo.li22d.g6.sokoban.model.Cell;

import static isel.poo.li22d.g6.sokoban.model.Cell.MAN;
import static isel.poo.li22d.g6.sokoban.model.Cell.OBSTACLE;

public class Man extends Actor {
    public Man() {
        super(MAN);
    }

    /**
     * Checks if the Man can be moved to nextCell.
     * Defined events:
     * - If nextCell isn't an obstacle;
     * - If there's an actor in the next Cell, which checks if that one can be moved.
     *   In this case, the only other type of actor is a Box, so the lastCell can be passed as null.
     *
     * @param nextCell the next immediate Cell
     * @param lastCell the Cell next to nextCell
     * @return
     */
    @Override
    public boolean canMove(Cell nextCell, Cell lastCell) {
        if (nextCell.getType() == OBSTACLE)
            return false;

        if (nextCell.getActor() != null)
            return nextCell.getActor().canMove(lastCell, null);

        return true;
    }
}