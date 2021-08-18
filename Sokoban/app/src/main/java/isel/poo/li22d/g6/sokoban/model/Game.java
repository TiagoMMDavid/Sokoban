package isel.poo.li22d.g6.sokoban.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Game {
    private int levelNumber = 0;
    private Level curLevel = null;

    // --- Methods to be used by Controller ---

    public Level loadNextLevel(InputStream levelsFile) throws Loader.LevelFormatException {
        curLevel = new Loader(createScanner(levelsFile)).load(++levelNumber);
        if (curLevel!=null)
            curLevel.init(this);
        return curLevel;
    }

    public void restart(InputStream levelsFile) {
        new Loader(createScanner(levelsFile)).reload(curLevel);
        curLevel.init(this);
    }

    private Scanner createScanner(InputStream levelsFile) {
        InputStream input = levelsFile.markSupported() ? levelsFile : new BufferedInputStream(levelsFile);
        input.mark(40*1024);
        return new Scanner(input);
    }

    public String saveState() {
        return curLevel.saveState();
    }

    public Level loadState(Scanner in, int levelNumber) throws Loader.LevelFormatException {
        this.levelNumber = levelNumber;
        curLevel = new Loader(in).load(levelNumber);
        curLevel.loadState(in);
        return curLevel;
    }
}
