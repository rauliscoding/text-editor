import org.junit.*;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;

/**
 * Class responsable for testing methods of class Beffer.
 */
public class BufferTest {

    /**
     * buffer that will be used at the tests.
     */
    private Buffer buf = new Buffer();

    @Test
    public void setUp() throws Exception {

    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Test for method buf.insertStr()
     * @throws Exception
     */
    @Before
    public void insertStr() throws Exception {

        buf.insertStr("string we will be testing");
        int[] v = buf.getter();
        assertEquals(v[0],0);
        assertEquals(v[1],buf.getNthLine(0).length());

        buf.insertStr("Another string");
        int[] v1 = buf.getter();
        assertEquals("string we will be testingAnother string", buf.getNthLine(0).substring(0));
        assertEquals(v1[0],0);
        assertEquals(v1[1],buf.getNthLine(0).length());

    }

    /**
     * Test for method buf.insertLn()
     * @throws Exception
     */
    @Test
    public void insertLn() throws Exception {

        buf.setter(0,25);
        buf.insertLn();
        String x = buf.getNthLine(0).substring(0);
        String y = buf.getNthLine(1).substring(0);

        assertEquals("string we will be testing", x);
        assertEquals("Another string", y);

        buf.setter(1, buf.getNthLine(1).length());
        buf.insertLn();
        String empty = buf.getNthLine(2).substring(0);
        assertEquals("", empty);

    }

    /**
     * Test for method buf.insert()
     * @throws Exception
     */
    @Test
    public void insert() throws Exception {

        buf.setter(0,0);
        buf.insert('A');
        String s = buf.getNthLine(0).substring(0);
        assertEquals("Astring we will be testingAnother string", s);

        buf.setter(0,20);
        buf.insert('B');
        s = buf.getNthLine(0).substring(0);
        assertEquals("Astring we will be tBestingAnother string", s);

        buf.setter(0,buf.getNthLine(0).length());
        buf.insert('C');
        s = buf.getNthLine(0).substring(0);
        assertEquals("Astring we will be tBestingAnother stringC", s);

    }

    /**
     * Test for method buf.delete(). Delete at the middle, final and the beginning of a row.
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {

        buf.setter(0, 26);
        buf.delete();
        String s = buf.getNthLine(0).substring(0);
        assertEquals("string we will be testingnother string", s);

        buf.setter(0, 0);
        buf.delete();
        s = buf.getNthLine(0).substring(0);
        assertEquals("string we will be testingnother string", s);

    }

    /**
     * Test for method buf.moveUp(). Move up when cursor is at the first row and when is at other row.
     * @throws Exception
     */
    @Test
    public void moveUp() throws Exception {

        int[] buf_cursor = buf.getter();

        buf.moveUp();
        assertEquals(buf_cursor[0], 0);
        assertEquals(39, buf.getNthLine(0).length());

        buf.insertLn();
        buf.insertLn();
        buf.moveUp();
        assertEquals(1, buf_cursor[0]);
        assertEquals(0, buf.getNthLine(1).length());
    }

    /**
     * Test for method buf.moveDown(). Move down when cursor is at the last row and when it's not.
     * @throws Exception
     */
    @Test
    public void moveDown() throws Exception {

        int[] buf_cursor = buf.getter();

        buf.moveDown();
        assertEquals(0, buf_cursor[0]);

        buf.insertLn();
        buf.insertLn();
        buf.moveUp();
        buf.moveDown();
        assertEquals(0, buf_cursor[1]);
        assertEquals(2, buf_cursor[0]);

    }

    /**
     * Test for method buf.moveNext(). Move next when cursor is at the last column of a row and when cursor is at the biggining of a row.
     * @throws Exception
     */
    @Test
    public void moveNext() throws Exception {

        int[] buf_cursor = buf.getter();

        buf.moveNext();
        assertEquals(buf.getNthLine(0).length(), buf_cursor[1]);

        buf.setter(0,0);
        buf.moveNext();
        assertEquals(1, buf_cursor[1]);
    }

    /**
     * Test for method buf.movePrev(). Move Previous when cursor is at the biggining of a row and when it is at the middle of a row.
     * @throws Exception
     */
    @Test
    public void movePrev() throws Exception {

        int[] buf_cursor = buf.getter();

        buf.setter(0,0);
        buf.movePrev();
        assertEquals(buf_cursor[1], 0);

        buf.setter(0, 3);
        buf.movePrev();
        assertEquals(buf_cursor[1], 2);

    }

    /**
     * Test for method buf.getLinesCount(). Get lines Count when buffer id empty and when it have 2 rows.
     * @throws Exception
     */
    @Test
    public void getLinesCount() throws Exception {

        assertEquals(1, buf.getLinesCount());

        buf.setter(0, buf.getNthLine(0).length());
        buf.insertLn();
        buf.insertStr("Just adding some text");
        assertEquals(2, buf.getLinesCount());

    }

    /**
     * Test for method buf.getNthLine(). Get Nth Line when buffer only have one row with "string we will be testingAnother string".
     * @throws Exception
     */
    @Test
    public void getNthLine() throws Exception {

        String s = buf.getNthLine(0).substring(0);
        assertEquals("string we will be testingAnother string", s);

        exception.expect(IllegalArgumentException.class);
        s = buf.getNthLine(3).substring(0);

    }

    /**
     * Test for method buf.setter(). Verify when cursor position is valid or not.
     * @throws Exception
     */
    @Test
    public void setter() throws Exception {

        int[] buf_cursor = buf.getter();

        exception.expect(IndexOutOfBoundsException.class);
        buf.setter(6, 2);

        buf.setter(0, 2);
        assertEquals(0, buf_cursor[0]);
        assertEquals(2, buf_cursor[1]);
    }

    /**
     * Test for method buf.getter().
     * @throws Exception
     */
    @Test
    public void getter() throws Exception {

        int[] t = buf.getter();
        assertEquals(0, t[0]);
        assertEquals(buf.getNthLine(0).length(), t[1]);

        //alterar posição do cursor e testar novamente o método
        buf.setter(0,10);
        t = buf.getter();
        assertEquals(0, t[0]);
        assertEquals(10, t[1]);

    }

}