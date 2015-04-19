package uk.recurse.adtstool.stream;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.nio.channels.FileChannel.open;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;

public class FrameConsumer implements Closeable, Consumer<AdtsFrame> {

    private final FileChannel channel;

    public FrameConsumer(Path path) throws IOException {
        channel = open(path, CREATE_NEW, WRITE);
    }

    @Override
    public void accept(AdtsFrame adtsFrame) {
        try {
            channel.write(adtsFrame.data());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
