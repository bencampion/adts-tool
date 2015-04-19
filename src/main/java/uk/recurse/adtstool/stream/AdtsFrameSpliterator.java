package uk.recurse.adtstool.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.recurse.bitwrapper.BitWrapper;

import java.nio.ByteBuffer;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

class AdtsFrameSpliterator extends Spliterators.AbstractSpliterator<AdtsFrame> {

    private static final short SYNC_WORD = -16;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ByteBuffer buffer;
    private final BitWrapper wrapper;
    private boolean inSync = true;

    public AdtsFrameSpliterator(ByteBuffer buffer, BitWrapper wrapper) {
        super(Long.MAX_VALUE, Spliterator.NONNULL | Spliterator.ORDERED);
        this.buffer = buffer;
        this.wrapper = wrapper;
    }

    @Override
    public boolean tryAdvance(Consumer<? super AdtsFrame> action) {
        AdtsFrame nextFrame = nextFrame();
        if (nextFrame != null) {
            action.accept(nextFrame);
            return true;
        }
        return false;
    }

    private AdtsFrame nextFrame() {
        while (buffer.remaining() > 1 && !atSyncWord()) {
            buffer.get();
        }
        if (buffer.remaining() > 1) {
            AdtsFrame nextFrame = wrapper.wrap(buffer, AdtsFrame.class);
            buffer.position(buffer.position() + Math.max(1, nextFrame.length()));
            return nextFrame;
        }
        return null;
    }

    private boolean atSyncWord() {
        buffer.mark();
        short word = buffer.getShort();
        boolean atSyncWord = (short) (word & SYNC_WORD) == SYNC_WORD;
        buffer.reset();
        if (atSyncWord && !inSync) {
            log.info("ADTS sync word found");
            inSync = true;
        } else if (!atSyncWord && inSync) {
            log.warn("Ignoring non-ADTS data");
            inSync = false;
        }
        return atSyncWord;
    }
}
