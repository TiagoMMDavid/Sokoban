package isel.poo.li22d.g6.sokoban.view.tiles.actors;

import android.content.Context;
import android.graphics.Color;

import isel.poo.li22d.g6.lib.Img;
import isel.poo.li22d.g6.sokoban.R;
import isel.poo.li22d.g6.sokoban.model.Cell;
import isel.poo.li22d.g6.sokoban.view.CellTile;

public class ManTile extends CellTile {
    public ManTile(Context context, Cell cell) {
        super(cell.getType() == Cell.TARGET ? TARGET_COLOR : FLOOR_COLOR);

        if (cell.getType() == Cell.HOLE)
            setImage(new Img(context,R.drawable.drowned));
        else
            setImage(new Img(context, R.drawable.man));
    }
}
