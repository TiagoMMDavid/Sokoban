package isel.poo.li22d.g6.sokoban.model;

public enum Dir {
    UP(0,-1),
    DOWN(0,1),
    LEFT(-1,0),
    RIGHT(1,0);

    final int dX, dY;

    Dir (int x, int y) {
        this.dX = x;
        this.dY = y;
    }
}
