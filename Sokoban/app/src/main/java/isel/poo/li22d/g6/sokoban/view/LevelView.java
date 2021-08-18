package isel.poo.li22d.g6.sokoban.view;

import android.content.Context;
import android.util.AttributeSet;
import isel.poo.li22d.g6.lib.TilePanel;
import isel.poo.li22d.g6.sokoban.model.Cell;
import isel.poo.li22d.g6.sokoban.model.Level;

public class LevelView extends TilePanel {

    public LevelView(Context context) {
        super(context);
    }

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LevelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Initiates the level by setting the size, loading it, and setting the observer
     * @param context the context of this view
     * @param model the level to be associated with
     */
    public void initModel(final Context context, Level model) {
        setSize(model.getWidth(),model.getHeight());
        loadModel(context, model);

        model.setObserver(new Level.Observer() {
            @Override
            public void cellUpdated(int l, int c, Cell cell) {

            }

            @Override
            public void cellReplaced(int l, int c, Cell cell) {
                setTile(c,l,CellTile.tileOf(context, cell));
            }
        });
    }

    /**
     * Loads the model by setting each tile of the view as the
     * corresponding CellTile of each cell of the level
     * @param context context of the tiles
     * @param model the level to load the cells from
     */
    private void loadModel(Context context, Level model) {
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth() ; x++) {
                setTile(x, y, CellTile.tileOf(context, model.getCell(y,x)));
            }
        }
    }
}
