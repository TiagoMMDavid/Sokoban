package isel.poo.li22d.g6.sokoban.view.tiles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import isel.poo.li22d.g6.lib.Img;
import isel.poo.li22d.g6.sokoban.R;
import isel.poo.li22d.g6.sokoban.model.Cell;
import isel.poo.li22d.g6.sokoban.view.CellTile;

public class HoleTile extends CellTile {

    public HoleTile(Context context, Cell cell) {
        super(Color.BLACK);
        setImage(new Img(context, R.drawable.water));
    }
}
