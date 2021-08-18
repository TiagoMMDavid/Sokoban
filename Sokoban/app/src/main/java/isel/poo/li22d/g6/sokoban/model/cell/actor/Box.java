package isel.poo.li22d.g6.sokoban.model.cell.actor;

import isel.poo.li22d.g6.sokoban.model.Actor;
import isel.poo.li22d.g6.sokoban.model.Cell;

import static isel.poo.li22d.g6.sokoban.model.Cell.BOX;
import static isel.poo.li22d.g6.sokoban.model.Cell.OBSTACLE;

public class Box extends Actor {
    public Box() {
        super(BOX);
    }

    /**
     * Checks if the Box can be moved to the nextCell.
     * Defined events:
     * - If the nextCell doesn't have an actor, and it it isn't an obstacle.
     *
     * @param nextCell the next immediate Cell
     * @param lastCell the Cell next to nextCell
     * @return
     */
    @Override
    public boolean canMove(Cell nextCell, Cell lastCell) {
        return (nextCell.getActor() == null && nextCell.getType() != OBSTACLE);
    }
}
