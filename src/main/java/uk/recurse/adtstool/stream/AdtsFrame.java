package uk.recurse.adtstool.stream;

import uk.recurse.bitwrapper.annotation.Bits;
import uk.recurse.bitwrapper.annotation.Bytes;

import java.nio.ByteBuffer;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public interface AdtsFrame {

    List<Integer> SAMPLING_FREQUENCIES = unmodifiableList(asList(96000, 88200, 64000,
            48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350));

    @Bits(offset = 18, length = 4)
    int samplingFrequencyIndex();

    default int samplingFrequency() {
        int i = samplingFrequencyIndex();
        if (i < SAMPLING_FREQUENCIES.size()) {
            return SAMPLING_FREQUENCIES.get(i);
        }
        throw new IllegalArgumentException("Unsupported sampling rate");
    }

    @Bits(offset = 30, length = 13)
    int length();

    @Bytes(lengthExp = "length()")
    ByteBuffer data();
}
