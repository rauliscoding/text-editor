import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;

public class FileBuffer extends Buffer {

    private String savePath;
    public boolean modified;

    public boolean save() {

        if(modified) {
            try {

                if(savePath == null)
                    return false;

                FileWriter fileWriter = new FileWriter(savePath);

                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                int hasNext = 0;
                while (hasNext < getLinesCount()) {

                    String toWrite = getNthLine(hasNext).substring(0);
                    bufferedWriter.write(toWrite);
                    bufferedWriter.newLine();
                    hasNext++;

                }
                bufferedWriter.close();

            } catch (FileNotFoundException ex) {
                System.out.println("Unable to open file '" + savePath + "'");
            } catch (IOException ex) {
                System.out.println("Error writing to file '" + savePath + "'");

            }

        }
        return true;

    }

    public void saveAs(String path) {

            try {

                savePath = path;
                File file = new File(path);

                FileWriter fileWriter = new FileWriter(file);

                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                int hasNext = 0;
                while (hasNext < getLinesCount()) {

                    String toWrite = getNthLine(hasNext).substring(0);
                    bufferedWriter.write(toWrite);
                    bufferedWriter.newLine();
                    hasNext++;

                }
                savePath = path;
                bufferedWriter.close();

            } catch (FileNotFoundException ex) {
                System.out.println("Unable to open file '" + path + "'");
            } catch (IOException ex) {
                System.out.println("Error writing to file '" + path + "'");

            }

    }

    public void open(Path file) {

        try {

            modified = false;
            savePath = file.toString();

            InputStream in = Files.newInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            String line = bufferedReader.readLine();
            while(line != null) {

                insertStr(line);
                line = bufferedReader.readLine();
                insertLn();

            }

            setter(0,0);

            bufferedReader.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + file + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + file + "'");
        }

    }

}
