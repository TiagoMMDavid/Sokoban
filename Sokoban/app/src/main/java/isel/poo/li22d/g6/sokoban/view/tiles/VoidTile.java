package isel.poo.li22d.g6.sokoban.view.tiles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import isel.poo.li22d.g6.sokoban.model.Cell;
import isel.poo.li22d.g6.sokoban.view.CellTile;

public class VoidTile extends CellTile {

    public VoidTile(Context context, Cell cell) {
        super(VOID_COLOR);
    }
}
