package adtstool;

import org.junit.Test;

import com.github.adtstool.ADTSFrame;

import static org.junit.Assert.*;

public class ADTSFrameTest {

    private byte[] array = new byte[7];

    /**
     * Test of getID method, of class ADTSFrame.
     */
    @Test
    public void testGetID() {
        array[1] = (byte) Integer.parseInt("00001000", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertEquals(f.getID(), 1);
    }

    /**
     * Test of getLayer method, of class ADTSFrame.
     */
    @Test
    public void testGetLayer() {
        array[1] = (byte) Integer.parseInt("00000110", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertEquals(f.getLayer(), 3);
    }

    /**
     * Test of hasProtection method, of class ADTSFrame.
     */
    @Test
    public void testHasProtection() {
        array[1] = (byte) Integer.parseInt("00000001", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertFalse(f.hasProtection());
    }

    /**
     * Test of getProfileIndex method, of class ADTSFrame.
     */
    @Test
    public void testGetProfileIndex() {
        array[2] = (byte) Integer.parseInt("11000000", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertEquals(f.getProfileIndex(), 3);
    }

    /**
     * Test of getSamplingFrequencyIndex method, of class ADTSFrame.
     */
    @Test
    public void testGetSamplingFrequencyIndex() {
        array[2] = (byte) Integer.parseInt("00111100", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertEquals(f.getSamplingFrequencyIndex(), 15);
    }

    /**
     * Test of hasPrivateBit method, of class ADTSFrame.
     */
    @Test
    public void testHasPrivateBit() {
        array[2] = (byte) Integer.parseInt("00000010", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertTrue(f.hasPrivateBit());
    }

    /**
     * Test of getChannelConfigurationIndex method, of class ADTSFrame.
     */
    @Test
    public void testGetChannelConfigurationIndex() {
        array[2] = (byte) Integer.parseInt("00000001", 2);
        array[3] = (byte) Integer.parseInt("11000000", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertEquals(f.getChannelConfigurationIndex(), 7);
    }

    /**
     * Test of isOriginalCopy method, of class ADTSFrame.
     */
    @Test
    public void testIsOriginalCopy() {
        array[3] = (byte) Integer.parseInt("00100000", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertTrue(f.isOriginalCopy());
    }

    /**
     * Test of isHome method, of class ADTSFrame.
     */
    @Test
    public void testIsHome() {
        array[3] = (byte) Integer.parseInt("00010000", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertTrue(f.isHome());
    }

    /**
     * Test of getCopyrightIdentificationBit method, of class ADTSFrame.
     */
    @Test
    public void testGetCopyrightIdentificationBit() {
        array[3] = (byte) Integer.parseInt("00001000", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertEquals(f.getCopyrightIdentificationBit(), 1);
    }

    /**
     * Test of isCopyrightIdentificationStart method, of class ADTSFrame.
     */
    @Test
    public void testIsCopyrightIdentificationStart() {
        array[3] = (byte) Integer.parseInt("00000100", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertTrue(f.isCopyrightIdentificationStart());
    }

    /**
     * Test of getADTSBufferFullness method, of class ADTSFrame.
     */
    @Test
    public void testGetADTSBufferFullness() {
        array[5] = (byte) Integer.parseInt("00011111", 2);
        array[6] = (byte) Integer.parseInt("11111100", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertEquals(f.getADTSBufferFullness(), 0x7ff);
    }

    /**
     * Test of getNumberOfRawDataBlocksInFrame method, of class ADTSFrame.
     */
    @Test
    public void testGetNumberOfRawDataBlocksInFrame() {
        array[6] = (byte) Integer.parseInt("00000011", 2);
        ADTSFrame f = new ADTSFrame(array);
        assertEquals(f.getNumberOfRawDataBlocksInFrame(), 4);
    }
}
