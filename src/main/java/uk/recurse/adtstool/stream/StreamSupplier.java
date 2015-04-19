package uk.recurse.adtstool.stream;

import uk.recurse.bitwrapper.BitWrapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.open;

public class StreamSupplier implements Supplier<Stream<AdtsFrame>> {

    private final Path input;
    private final BitWrapper bitWrapper;

    public StreamSupplier(Path input, BitWrapper bitWrapper) {
        this.input = input;
        this.bitWrapper = bitWrapper;
    }

    @Override
    public Stream<AdtsFrame> get() {
        try (FileChannel in = open(input)) {
            ByteBuffer buffer = in.map(READ_ONLY, 0, in.size());
            Spliterator<AdtsFrame> spliterator = new AdtsFrameSpliterator(buffer, bitWrapper);
            return StreamSupport.stream(spliterator, false);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
