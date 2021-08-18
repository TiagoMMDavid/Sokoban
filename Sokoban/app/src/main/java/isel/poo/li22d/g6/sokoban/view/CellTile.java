package isel.poo.li22d.g6.sokoban.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import isel.poo.li22d.g6.lib.Img;
import isel.poo.li22d.g6.lib.Tile;
import isel.poo.li22d.g6.sokoban.model.Cell;
import isel.poo.li22d.g6.sokoban.view.tiles.*;
import isel.poo.li22d.g6.sokoban.view.tiles.actors.*;

import static isel.poo.li22d.g6.sokoban.model.Cell.*;

public abstract class CellTile implements Tile {
    private Paint brush;

    protected static int FLOOR_COLOR = Color.WHITE;
    protected static int VOID_COLOR = Color.BLACK;
    protected static int TARGET_COLOR = Color.rgb(200, 254, 200);

    private boolean isImageDrawable = false;
    private Img image;

    protected CellTile(int color) {
        brush = new Paint();
        brush.setColor(color);
        brush.setStyle(Paint.Style.FILL);
    }

    /**
     * Makes the Tile use an image to draw itself, rather than a color
     * @param image The Image to be drawn
     */
    protected void setImage(Img image) {
        this.image = image;
        isImageDrawable = true;
    }

    /**
     * Gets the corresponding CellTile object for the Cell passed as parameter
     * @param context the context for the tiles
     * @param cell the Cell of the corresponding CellTile
     * @return
     */
    public static Tile tileOf(Context context, Cell cell) {
        char cellType = cell.hasActor() ? cell.getActorType() : cell.getType();

        switch (cellType) {
            case VOID:
                return new VoidTile(context, cell);
            case FLOOR:
                return new FloorTile(context, cell);
            case OBSTACLE:
                return new ObstacleTile(context, cell);
            case TARGET:
                return new TargetTile(context, cell);
            case HOLE:
                return new HoleTile(context, cell);
            case MAN:
                return new ManTile(context, cell);
            case BOX:
                return new BoxTile(context, cell);
            default:
                return null;
        }
    }

    /**
     * Will draw the Tile and, if applicable, the image that's
     * associated with it.
     * @param canvas To draw the tile
     * @param side The width of tile in pixels
     */
    @Override
    public void draw(Canvas canvas, int side) {
        canvas.drawRect(0,0, side, side, brush);
        if (isImageDrawable)
            image.draw(canvas, side, side, brush);
    }


    @Override
    public boolean setSelect(boolean selected) {
        return false;
    }
}
