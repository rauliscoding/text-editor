import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.input.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BufferView {

    private Screen term;
    private int[] startShow;
    private int[] cpPast;
    private FileBuffer buf;

    BufferView(String[] args) {

        buf = new FileBuffer();

        if (args.length > 0) {

            Path path = Paths.get(args[0]);
            buf.open(path);

        }
        startShow = new int[]{0,0};
        cpPast = new int[]{0,0,0,0};

    }

    public void run() throws InterruptedException {

        term = TerminalFacade.createScreen();
        term.startScreen();

        while (true) {

            showBuffer();
            Key k = readChar();
            processInput(k);

        }

    }

    private void showBuffer() {

        int fisicalLine = 2;
        int fisicalCol;
        String line;

        term.clear();
        formatTerminal();
        term.updateScreenSize();
        getfirstVisualRow();

        for (int i = startShow[0]; i < buf.getLinesCount(); i++) {

            if (i == startShow[0])
                line = buf.getNthLine(i).substring(startShow[1]);
            else
                line = buf.getNthLine(i).substring(0);

            fisicalCol = 0;
            for (char ch : line.toCharArray()) {

                if (fisicalCol < term.getTerminalSize().getColumns()) {

                    term.putString(fisicalCol, fisicalLine, Character.toString(ch), Terminal.Color.WHITE, Terminal.Color.DEFAULT);
                    fisicalCol++;

                } else {

                    fisicalCol = 0;
                    fisicalLine++;
                    term.putString(fisicalCol, fisicalLine, Character.toString(ch), Terminal.Color.WHITE, Terminal.Color.DEFAULT);
                    fisicalCol++;

                }
                if (fisicalLine > term.getTerminalSize().getRows() - 5)
                    break;
            }

            fisicalLine++;
            if (fisicalLine > term.getTerminalSize().getRows() - 5)
                break;

        }
        int[] position = convertToFisical();
        term.setCursorPosition(position[1], position[0]);
        term.refresh();

    }

    private Key readChar() throws InterruptedException{

        while (true) {
            Thread.sleep(35);
            Key k = term.readInput();

            if (k != null) {

                switch (k.getKind()) {

                    case Escape:
                        term.stopScreen();
                        System.exit(0);
                    default:
                        return k;

                }

            } else if (term.updateScreenSize())
                showBuffer();

        }

    }

    private void processInput(Key k) throws InterruptedException {

        int[] buf_cursor = buf.getter();

        if (k.isCtrlPressed()) {

            if (k.getCharacter() == 's' || k.getCharacter() == 'S') {

                if(!buf.save()) {
                    String savePath = popUp();
                    buf.saveAs(savePath);
                }


            }

            else if (k.getCharacter() == 'a' || k.getCharacter() == 'A') {

                String savePath = popUp();
                buf.saveAs(savePath);
            }
            else if(k.getCharacter() == 'b'){
                System.out.println("entrou");
                int[] tmp = buf.getter();
                cpPast[0] = tmp[0];
                cpPast[1] = tmp[1];
            }

            else if(k.getCharacter() == 'c' || k.getCharacter() == 'C'){
                int[] tmp = buf.getter();
                cpPast[2] = tmp[0];
                cpPast[3] = tmp[1];

                if(cpPast[0] > cpPast[2]){
                    int tmp2 = cpPast[0];
                    cpPast[0] = cpPast[2];
                    cpPast[2] = tmp2;
                    tmp2 = cpPast[1];
                    cpPast[1] = cpPast[3];
                    cpPast[3] = tmp2;
                }
                else if(cpPast[0] == cpPast[2]){
                    if(cpPast[1] > cpPast[3]){
                        int tmp2 = cpPast[1];
                        cpPast[1] = cpPast[3];
                        cpPast[3] = tmp2;
                    }
                }

                if(buf.getNthLine(cpPast[2]).length() == cpPast[3] && buf.getNthLine(cpPast[2]).length() != 0)
                    cpPast[3]--;
            }

            else if(k.getCharacter() == 'v' || k.getCharacter() == 'V'){

                paste();

            }

        } else {

            switch (k.getKind()) {

                case ArrowLeft:
                    buf.movePrev();
                    buf_cursor = buf.getter();
                    break;
                case ArrowRight:
                    buf.moveNext();
                    buf_cursor = buf.getter();
                    break;
                case ArrowDown:

                    if (buf_cursor[0] == buf.getLinesCount() - 1) {

                        if (buf_cursor[1] > buf.getNthLine(buf_cursor[0]).length() - (buf_cursor[1] % term.getTerminalSize().getColumns()))
                            buf_cursor[1] = buf.getNthLine(buf_cursor[0]).length();
                        else {
                            if (buf_cursor[1] + term.getTerminalSize().getColumns() < buf.getNthLine(buf_cursor[0]).length() - 1)
                                buf_cursor[1] += term.getTerminalSize().getColumns();
                            else
                                buf_cursor[1] = buf.getNthLine(buf_cursor[0]).length();
                        }
                    }

                    else if (buf_cursor[1] >= buf.getNthLine(buf_cursor[0]).length() - (buf.getNthLine(buf_cursor[0]).length() % term.getTerminalSize().getColumns())) {

                        if (buf.getNthLine(buf_cursor[0] + 1).length() <= buf_cursor[1] % term.getTerminalSize().getColumns())
                            buf_cursor[1] = buf.getNthLine(buf_cursor[0] + 1).length();
                        else
                            buf_cursor[1] = buf_cursor[1] % term.getTerminalSize().getColumns();
                        buf_cursor[0]++;
                    }

                    else {
                        if (buf_cursor[1] + term.getTerminalSize().getColumns() < buf.getNthLine(buf_cursor[0]).length() - 1)
                            buf_cursor[1] += term.getTerminalSize().getColumns();
                        else
                            buf_cursor[1] = buf.getNthLine(buf_cursor[0]).length();
                    }
                    break;
                case ArrowUp:
                    if(buf.cursorRow == 0 && term.getCursorPosition().getRow() == 2);

                    else if (buf_cursor[1] - term.getTerminalSize().getColumns() >= 0) {
                        buf_cursor[1] -= term.getTerminalSize().getColumns();

                    }

                    else {

                        if (buf.getNthLine(buf_cursor[0] - 1).length() == 0)
                            buf_cursor[1] = 0;
                        else if (buf.getNthLine(buf_cursor[0] - 1).length() % term.getTerminalSize().getColumns() < buf_cursor[1])
                            buf_cursor[1] = buf.getNthLine(buf_cursor[0] - 1).length() - 1;
                        else {
                            buf_cursor[1] = term.getTerminalSize().getColumns() * (buf.getNthLine(buf_cursor[0] - 1).length() / term.getTerminalSize().getColumns()) + buf_cursor[1];
                        }
                        buf_cursor[0]--;

                    }
                    break;
                case Backspace:
                    buf.delete();
                    buf_cursor = buf.getter();
                    break;
                case Enter:
                    buf.insertLn();
                    buf_cursor = buf.getter();
                    break;
                default:
                    char c = k.toString().charAt(11);
                    buf.insert(c);
                    buf_cursor = buf.getter();
                    break;

            }

        }
        buf.modified = true;
        buf.setter(buf_cursor[0], buf_cursor[1]);

    }

    private void formatTerminal() {

        for (int j = 0; j < 2; j++)
            for (int w = 0; w < term.getTerminalSize().getColumns(); w++)
                term.putString(w, j, "0", Terminal.Color.WHITE, Terminal.Color.WHITE);


        for (int j = 0; j < term.getTerminalSize().getColumns(); j++)
            term.putString(j, term.getTerminalSize().getRows() - 1, "0", Terminal.Color.WHITE, Terminal.Color.WHITE);

        for (int j = 0; j < term.getTerminalSize().getColumns(); j++)
            term.putString(j, term.getTerminalSize().getRows() - 4, "0", Terminal.Color.WHITE, Terminal.Color.WHITE);

        for (int j = 2; j <= 3; j++) {
            term.putString(0, term.getTerminalSize().getRows() - j, "0", Terminal.Color.WHITE, Terminal.Color.WHITE);
            term.putString(term.getTerminalSize().getColumns() - 1, term.getTerminalSize().getRows() - j, "0", Terminal.Color.WHITE, Terminal.Color.WHITE);

        }

    }

    private int[] convertToFisical() {

        int[] buf_cursor = buf.getter();
        int row = 0, col;
        int largura = term.getTerminalSize().getColumns();
        int tmp_startCol = startShow[1];
        String str;

        if (buf_cursor[0] != startShow[0]) {

            while (tmp_startCol <= buf.getNthLine(startShow[0]).length()) {   //< ou <=
                row++;
                tmp_startCol += term.getTerminalSize().getColumns();

            }
            for (int i = startShow[0] + 1; i < buf_cursor[0]; i++) {
                str = buf.getNthLine(i).substring(0);
                if (str.length() % largura == 0 && str.length() > 0)
                    row += str.length() / largura;
                else
                    row += str.length() / largura + 1;

            }
            row += buf_cursor[1] / largura;
            col = buf_cursor[1] % largura;

        } else {
            while (tmp_startCol <= buf_cursor[1]) {
                tmp_startCol += term.getTerminalSize().getColumns();
                if (tmp_startCol <= buf_cursor[1])
                    row++;

            }
            col = buf_cursor[1] % largura;

        }
        return new int[]{row + 2, col};

    }

    private void getfirstVisualRow() {

        int position[] = convertToFisical();

        if (position[0] == term.getTerminalSize().getRows() - 5) {

            if (startShow[1] + term.getTerminalSize().getColumns() <= buf.getNthLine(startShow[0]).length())
                startShow[1] += term.getTerminalSize().getColumns();

            else {
                startShow[0]++;
                startShow[1] = 0;

            }

        } else if (position[0] == 2) {

            if (startShow[1] - term.getTerminalSize().getColumns() >= 0)
                startShow[1] -= term.getTerminalSize().getColumns();

            else {
                if (startShow[0] != 0) {
                    int res = buf.getNthLine(startShow[0] - 1).length() % term.getTerminalSize().getColumns();
                    startShow[1] = buf.getNthLine(startShow[0] - 1).length() - res;
                    startShow[0]--;

                }

            }

        }

    }

    private String popUp() throws InterruptedException {

        int x = term.getCursorPosition().getRow();
        int y = term.getCursorPosition().getColumn();

        String path = "";
        term.putString(2, term.getTerminalSize().getRows() - 3, "Save as: ", Terminal.Color.RED, Terminal.Color.DEFAULT);
        term.setCursorPosition(15, term.getTerminalSize().getRows() - 3);
        term.refresh();
        int count = 15;
        Key k;
        while (true) {
            Thread.sleep(35);
            k = term.readInput();
            if(k != null) {

                if (k.getKind() == Key.Kind.Escape)
                    break;
                else if(k.getKind() == Key.Kind.Backspace){
                    if(path.length() != 0){
                        term.putString(term.getCursorPosition().getColumn()-1, term.getCursorPosition().getRow(), " ", Terminal.Color.DEFAULT, Terminal.Color.DEFAULT );
                        term.refresh();
                        path = path.substring(0,path.length()-1);
                        count--;

                    }
                }

                else {
                    path += k.getCharacter();
                    count++;
                }
                term.putString(15, term.getTerminalSize().getRows() - 3, path, Terminal.Color.WHITE, Terminal.Color.DEFAULT);
                term.setCursorPosition(count, term.getTerminalSize().getRows() - 3);
                term.refresh();
            }

        }
        term.setCursorPosition(y,x);
        return path;

    }

    private void paste(){
        ArrayList<StringBuilder> cp = copy();
        String str;

        for(int i = 0; i < cp.size(); i++)
            System.out.println(cp.get(i));

        for(int i = 0; i < cp.size()-1 ; i++){
            str = cp.get(i).toString();
            buf.insertStr(str);
            buf.insertLn();
        }

        str = cp.get(cp.size()-1).toString();
        buf.insertStr(str);
            }

    private ArrayList<StringBuilder> copy(){
        ArrayList<StringBuilder> cp = new ArrayList<StringBuilder>();
        StringBuilder str2 = new StringBuilder();

        if(cpPast[0] == cpPast[2]){
            if(cpPast[1] == cpPast[3])
                str2.append(buf.getNthLine(cpPast[0]).charAt(cpPast[1]));
            else
            str2.append(buf.getNthLine(cpPast[0]).substring(cpPast[1], cpPast[3]+1));
            cp.add(str2);
        }
        else{
            str2.append(buf.getNthLine(cpPast[0]).substring(cpPast[1], buf.getNthLine(cpPast[0]).length()));
            cp.add(str2);
            for( int i = cpPast[0]+1 ; i < cpPast[2]; i++){
                str2 = new StringBuilder();
                str2.append(buf.getNthLine(i).substring(0, buf.getNthLine(i).length()));
                cp.add(str2);
            }
            str2 = new StringBuilder();
            str2.append(buf.getNthLine(cpPast[2]).substring(0, cpPast[3]+1));
            cp.add(str2);
        }
        return cp;
    }

}