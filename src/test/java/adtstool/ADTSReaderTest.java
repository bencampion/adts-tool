package adtstool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.adtstool.ADTSReader;

public class ADTSReaderTest {

    private byte[] frames;
    InputStream input;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        int number = 8;
        int length = 16;
        frames = new byte[number * length];
        for (int offset = 0; offset < frames.length; offset += length) {
            frames[0 + offset] = (byte) Integer.parseInt("11111111", 2);
            frames[1 + offset] = (byte) Integer.parseInt("11110000", 2);
            frames[4 + offset] = (byte) Integer.parseInt("00000010", 2);
        }
        input = new ByteArrayInputStream(frames);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of hasNext method, of class ADTSReader.
     */
    @Test
    public void testHasNext() throws IOException {
        ADTSReader reader = new ADTSReader(input);
        assertTrue(reader.hasNext());
        reader.close();
    }

    /**
     * Test of next method, of class ADTSReader.
     */
    @Test
    public void testNext() throws IOException {
        ADTSReader reader = new ADTSReader(input);
        try {
            for (int i = 0; i < 8; i++) {
                reader.next();
            }
            exception.expect(NoSuchElementException.class);
            reader.next();
        }
        finally {
            reader.close();
        }
    }

    /**
     * Test of getNextFrame method, of class ADTSReader.
     */
    @Test
    public void testIterate() throws IOException {
        ADTSReader reader = new ADTSReader(input);
        assertNumberOfFrames(reader, 8);
        reader.close();
    }

    /**
     * Tests input resynchronisation.
     */
    @Test
    public void testSync() throws IOException {
        frames[16] = 0;
        ADTSReader reader = new ADTSReader(input);
        assertNumberOfFrames(reader, 7);
        reader.close();
    }

    private void assertNumberOfFrames(ADTSReader r, int i) throws IOException {
        while (r.hasNext()) {
            r.next();
        }
        assertEquals(i, r.framesRead());
    }
}
