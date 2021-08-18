package isel.poo.li22d.g6.sokoban.view.tiles;

import android.content.Context;
import android.graphics.Color;

import isel.poo.li22d.g6.sokoban.model.Cell;
import isel.poo.li22d.g6.sokoban.view.CellTile;

public class FloorTile extends CellTile {

    public FloorTile(Context context, Cell cell) {
        super(FLOOR_COLOR);
    }
}
