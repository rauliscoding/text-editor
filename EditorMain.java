/**
 * Main class. It is responsable to call run()
 */
public class EditorMain {

    /**
     * Main method.
     * @param args args can contain one file to be open at the buffer.
     */
    public static void main(String[] args) throws InterruptedException{

        BufferView bufferView = new BufferView(args);
        bufferView.run();

    }

}
