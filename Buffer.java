import java.lang.StringBuilder;
import java.util.ArrayList;

/**
 * Class Buffer is responsable for the logic part of the text editor. Is here that is saved all the text, cursor location and all the methods to modify the buffer like insert or delete a character, all the movements inside the buffer, etc.
 */
public class Buffer {

    /** logicLines is a ArrayList that contains all the text/logic lines as StringBuilders. */
    public ArrayList<StringBuilder> logicLines;
    public int cursorRow;
    public int cursorCol;

    /** Constructor of object Buffer. It initializes an empty ArrayList() and the cursor position (cursorRow = 0, cursorCol = 0). */
    Buffer() {

        logicLines = new ArrayList<>();
        StringBuilder str = new StringBuilder("");
        logicLines.add(str);

        cursorRow = 0;
        cursorCol = 0;

    }

    /**
     * Constructor of object Buffer. It initializes a ArrayList(logicLines) and the cursor position (cursorRow = 0, cursorCol = 0).
     * @param logicLines ArrayList of StringBuilders that contains the logic lines to initialise the buffer.
     */
    Buffer(ArrayList<StringBuilder> logicLines) {

        this.logicLines = logicLines;
        cursorRow = 0;
        cursorCol = 0;

    }

    /**
     * Insert a String(line) at the cursor position. line can't contain '\n'.
     * @param line String to insert.
     */
    public void insertStr(String line) {

        StringBuilder tmp;
        if(line.contains("\n"))
            throw new IllegalArgumentException();
        else{
            tmp = logicLines.get(cursorRow);
            tmp.insert(cursorCol, line );
            logicLines.set(cursorRow, tmp);
            cursorCol = tmp.length();
        }

     }

    /**
     * Insert a new line at the cursor position.
     */
    public void insertLn(){

        String toAdd1 = logicLines.get(cursorRow).substring(0, cursorCol);
        String toAdd2 = logicLines.get(cursorRow).substring(cursorCol);

        StringBuilder Add1 = new StringBuilder();
        Add1.append(toAdd1);

        StringBuilder Add2 = new StringBuilder();
        Add2.append(toAdd2);

        logicLines.set(cursorRow    , Add1);
        logicLines.add(cursorRow + 1, Add2);

        cursorCol  = 0;
        cursorRow += 1;

    }

    /**
     * Insert a character at the cursor position.
     * @param c char to insert.
     */
    public void insert(char c){

        if(c == '\n') {
            insertLn();
        }
        else {
            logicLines.get(cursorRow).insert(cursorCol, c);
            cursorCol += 1;
        }

    }

    /**
     * Delete a character immediatly before the cursor position.
     */
    public void delete(){

        if(cursorCol > 0){
            StringBuilder line = logicLines.get(cursorRow);

            line.deleteCharAt(cursorCol - 1);

            logicLines.set(cursorRow, line);

            cursorCol -= 1;

        }

        else if(cursorRow > 0){
            StringBuilder line = logicLines.get(cursorRow - 1);

            int colPosition = line.length();

            line.append(logicLines.get(cursorRow));

            logicLines.remove(cursorRow);

            cursorCol  = colPosition;
            cursorRow -= 1;
        }
    }

    /**
     * Move the cursor one logic line above.
     */
    public void moveUp(){

        if(cursorRow > 0) {
            cursorRow -= 1;
            cursorCol = Math.min(cursorCol, logicLines.get(cursorRow).length());

        }

    }

    /**
     * Move the cursor one logic line below.
     */
    public void moveDown(){

        if(cursorRow+1 < logicLines.size()) {
            cursorRow += 1;
            cursorCol = Math.min(cursorCol, logicLines.get(cursorRow).length());

        }

    }

    /**
     * Move the cursor to the next valid position.
     */
    public void moveNext(){

        if(cursorCol < logicLines.get(cursorRow).length() )
            cursorCol += 1;

        else if(cursorRow != logicLines.size() - 1 && cursorCol == logicLines.get(cursorRow).length()) {
            cursorCol  = 0;
            cursorRow += 1;
        }
    }

    /**
     * Move the cursor to the previous valid position.
     */
    public void movePrev(){

        if(cursorCol > 0)
            cursorCol -= 1;

        else if(cursorRow > 0){
            cursorRow -= 1;
            cursorCol  = logicLines.get(cursorRow).length();
        }

    }

    /**
     * Get the number of logic lines that exists at the buffer.
     * @return logicLines.size()
     */
     public int getLinesCount(){
        int n = logicLines.size();

        return n;
    }

    /**
     * Return the logic line at the index.
     * @param n Index of logic line that will be returned.
     * @return StringBuilder at the index n.
     */
    public StringBuilder getNthLine(int n){
        if( n >= 0 && n < logicLines.size()){
            StringBuilder line = logicLines.get(n);
            return line;
        }

        else
            throw new IllegalArgumentException();
    }

    /**
     * Get all the logic lines from the buffer.
     * @return ArrayList of StringBuilders that corresponds to all the logic lines.
     */
    public ArrayList<StringBuilder> getAllLines(){
        return logicLines;
    }

    //Definir a posição do cursor

    /**
     * Define the cursor position.
     * @param row New cursor row.
     * @param col New cursor column.
     */
    public void setter(int row, int col) {

        if (row >= 0 && row < logicLines.size()) {
            if (col >= 0 && col <= logicLines.get(row).length()) {
                cursorRow = row;
                cursorCol = col;
            }
        }

        else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    //Concultar a posição do cursor

    /**
     * Get cursor position
     * @return Array with the cursor Row and cursor Col positions by that order.
     */
    public int[] getter() {

        int[] cursorPosition = new int[2];

        cursorPosition[0] = cursorRow;
        cursorPosition[1] = cursorCol;

        return cursorPosition;
    }
}
