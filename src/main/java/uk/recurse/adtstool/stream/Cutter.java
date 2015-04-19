package uk.recurse.adtstool.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Cutter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Supplier<Stream<AdtsFrame>> stream;
    private final Consumer<AdtsFrame> consumer;
    private final int sampleRate;

    public Cutter(Supplier<Stream<AdtsFrame>> stream, Consumer<AdtsFrame> consumer) throws IOException {
        this.stream = stream;
        this.consumer = consumer;
        sampleRate = getSampleRate();
    }

    private int getSampleRate() throws IOException {
        log.info("Finding sample rate");
        Optional<AdtsFrame> frame = stream.get().findFirst();
        if (frame.isPresent()) {
            log.info("Input sample rate is {} Hz", frame.get().samplingFrequency());
            return frame.get().samplingFrequency();
        } else {
            throw new IOException("Input does not contain any ADTS frames");
        }
    }

    public void write(LocalTime start, LocalTime end) {
        double frameLength = (1024d / sampleRate) * 1000;
        long offset = (long) (start.getLong(ChronoField.MILLI_OF_DAY) / frameLength);
        long length = (long) (Duration.between(start, end).toMillis() / frameLength);
        log.info("Writing frames from {} to {}", start, end);
        stream.get().skip(offset).limit(length).forEachOrdered(consumer);
    }
}
