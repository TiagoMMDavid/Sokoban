package isel.poo.li22d.g6.sokoban.model;

import java.util.*;

/**
 * Loads a game level from the file read with the scanner indicated in the constructor.<br/>
 * The file contains several levels.<br/>
 * Each level has a number from 1 to N.<br/><br/>
 * The first line of description for a level must conform to the format:<br/>
 * <code>#NNN HEIGHT x WIDTH</code><br/>
 * Where: <code>NNN</code> is the level number.<br/>
 * <code>HEIGHT</code> is the number of lines.<br/>
 * <code>WIDTH</code> is the number of columns.<br/>
 *
 */
public class Loader {
    private final Scanner in;   // Scanner used to read the file
    private int lineNumber;     // Current line number
    private String line;        // Text of current line

    private Level model;      // The loaded model
    private int height, width;   // Dimensions of current level
    private MultiChars multiChars = new MultiChars();

    /**
     * Build the loader to read it from the file through the scanner
     * @param in The scanner to use
     */
    Loader(Scanner in) {
        this.in = in;
    }

    /**
     * Reads the level identified by the number.<br/>
     * @param levelNumber The level number
     * @return The model for the loaded level or null if level not found
     * @throws LevelFormatException If an error is found in the file
     */
    Level load(int levelNumber) throws LevelFormatException {
        if (!findHeader(levelNumber))    // Find the header line
            return null;
        model = new Level(levelNumber,height,width);    // Build the model
        loadGrid();                         // Load cell information
        return model;
    }

    /**
     * Reads again the level.<br/>
     * Assumes no error can found in the file
     * @param oldModel The level to reload
     */
    void reload(Level oldModel) {
        try {
            model = oldModel;
            findHeader(model.getNumber());    // Find the header line
            model.reset();
            loadGrid();                         // Load cell information
        } catch (LevelFormatException e) {
            e.printStackTrace();
        }
    }
    /**
     * Read the square grid and instantiate each square according to its description.<br/>
     * @throws LevelFormatException If an error is found in square descriptions
     */
    private void loadGrid() throws LevelFormatException {
        multiChars.clear();
        for(int l=0; l<height ; ++lineNumber,++l) {
            line = in.nextLine();                  // Read a line of cell
            if (line.length()>width)               // Verify number of cell in line
                error("Wrong number of cell in line");
            for(int c = 0; c<line.length() ; c++) {
                char type = line.charAt(c);
                if (type>='a' && type<='z')
                    multiChars.add(type,l,c);
                else
                    model.put(l, c, type);           // Add cell information to the model
            }
        }
        multiChars.load();
    }

    /**
     * Find the header line for the level<br/>
     * Stores the dimensions of the level in <code>height</code> and <code>width</code> fields.
     * @param level The level number
     * @throws LevelFormatException If an errors is found in the file or level not found.
     */
    private boolean findHeader(int level) throws LevelFormatException {
        try {
            int idx;
            for (lineNumber = 1; ; ++lineNumber) {
                line = in.nextLine();
                if (line.length() == 0 || line.charAt(0) != '#') continue;
                if ((idx = line.indexOf(' ')) <= 1) error("Invalid header line");
                if (Integer.parseInt(line.substring(1, idx)) == level) break;
            }
            int idxSep = line.indexOf('x',idx+1);
            if (idxSep<=0) error("Missing dimensions of level "+level);
            height = Integer.parseInt(line.substring(idx+1,idxSep).trim());
            width = Integer.parseInt(line.substring(idxSep+1).trim());
        } catch (NumberFormatException e) {
            error("Invalid number");
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    /**
     * Helper method to launch a LevelFormatException in internal methods.
     * @param msg The exception message
     * @throws LevelFormatException
     */
    private void error(String msg) throws LevelFormatException {
        throw new LevelFormatException(msg);
    }

    /**
     * Launched when a level loading error is detected.
     * The message describes the type of error.
     * Has the line number and the line where the error was detected.
     */
    public class LevelFormatException extends Exception {
        LevelFormatException(String msg) {
            super(msg);
        }
        public int getLineNumber() { return lineNumber; }
        public String getLine() { return line; }
    }

    private static class Pos {
        int line, col;
        Pos(int l, int c) { line=l; col=c; }
    }

    private class MultiChars extends ArrayList<List<Pos>> {
        void add(char letter, int line, int col) {
            int idx = letter-'a';
            while(size()<=idx) add(null);
            List<Pos> elems = get(idx);
            if (elems==null) {
                elems=new LinkedList<Pos>();
                set(idx,elems);
            }
            elems.add( new Pos(line,col));
        }
        void load() throws LevelFormatException {
            for (int i = 0; i < size(); i++)
                loadLine();
            for (int i = 0; i < size(); i++)
                if (get(i)!=null) error("missing multi-char letter:"+(char)('a'+i));
        }
        private void loadLine() throws LevelFormatException {
            line = in.nextLine();
            ++lineNumber;
            int idxEq = line.indexOf('=');
            if (idxEq<=0) error("expected multi-char line");
            char letter = line.charAt(0);
            int idx = letter-'a';
            if (idx<0 || idx>=size()) error("invalid multi-char letter:"+letter);
            line = line.substring(idxEq+1);
            List<Pos> elems = get(idx);
            if (elems==null) error("unused multi-char letter:"+letter);
            for (int l = 0; l < line.length(); l++) {
                char type = line.charAt(l);
                for (Pos pos : elems)
                    model.put(pos.line, pos.col, type);
            }
            set(idx,null);
        }
    }
}
