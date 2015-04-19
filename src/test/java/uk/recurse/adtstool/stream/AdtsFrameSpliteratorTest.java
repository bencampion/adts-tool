package uk.recurse.adtstool.stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.recurse.bitwrapper.BitWrapper;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdtsFrameSpliteratorTest {

    @Mock
    private BitWrapper wrapper;

    @Mock
    private AdtsFrame frame;

    @Mock
    private Consumer<AdtsFrame> action;

    @Before
    public void setup() {
        when(wrapper.wrap(any(ByteBuffer.class), eq(AdtsFrame.class))).thenReturn(frame);
        when(frame.length()).thenReturn(2);
    }

    @Test
    public void tryAdvance_emptyBuffer_returnsFalseAndDoesNotConsume() {
        AdtsFrameSpliterator spliterator = new AdtsFrameSpliterator(ByteBuffer.allocate(0), wrapper);

        assertFalse(spliterator.tryAdvance(action));
        verifyZeroInteractions(action);
    }

    @Test
    public void tryAdvance_bufferWithOneByte_returnsFalseAndDoesNotConsume() {
        AdtsFrameSpliterator spliterator = new AdtsFrameSpliterator(ByteBuffer.allocate(1), wrapper);

        assertFalse(spliterator.tryAdvance(action));
        verifyZeroInteractions(action);
    }

    @Test
    public void tryAdvance_bufferWithTenBytes_returnsFalseAndDoesNotConsume() {
        AdtsFrameSpliterator spliterator = new AdtsFrameSpliterator(ByteBuffer.allocate(10), wrapper);

        assertFalse(spliterator.tryAdvance(action));
        verifyZeroInteractions(action);
    }

    @Test
    public void tryAdvance_bufferWithOneSyncWord_consumesOnce() {
        ByteBuffer buffer = bytes(0b11111111, 0b11110000);
        AdtsFrameSpliterator spliterator = new AdtsFrameSpliterator(buffer, wrapper);

        assertTrue(spliterator.tryAdvance(action));
        assertFalse(spliterator.tryAdvance(action));
        verify(action, times(1)).accept(frame);
    }

    @Test
    public void tryAdvance_bufferWithOneSyncWordAndBits_consumesOnce() {
        ByteBuffer buffer = bytes(0b11111111, 0b11111111);
        AdtsFrameSpliterator spliterator = new AdtsFrameSpliterator(buffer, wrapper);

        assertTrue(spliterator.tryAdvance(action));
        assertFalse(spliterator.tryAdvance(action));
        verify(action, times(1)).accept(frame);
    }

    @Test
    public void tryAdvance_bufferWithOneSyncWordAndBytes_consumesOnce() {
        ByteBuffer buffer = bytes(0, 0b11111111, 0b11110000, 0);
        AdtsFrameSpliterator spliterator = new AdtsFrameSpliterator(buffer, wrapper);

        assertTrue(spliterator.tryAdvance(action));
        assertFalse(spliterator.tryAdvance(action));
        verify(action, times(1)).accept(frame);
    }

    @Test
    public void tryAdvance_bufferWithTwoSyncWords_consumesTwice() {
        ByteBuffer buffer = bytes(0b11111111, 0b11110000, 0b11111111, 0b11110000);
        AdtsFrameSpliterator spliterator = new AdtsFrameSpliterator(buffer, wrapper);

        assertTrue(spliterator.tryAdvance(action));
        assertTrue(spliterator.tryAdvance(action));
        assertFalse(spliterator.tryAdvance(action));
        verify(action, times(2)).accept(frame);
    }

    private ByteBuffer bytes(int... bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        IntStream.of(bytes).forEachOrdered((i) -> buffer.put((byte) i));
        buffer.rewind();
        return buffer;
    }
}