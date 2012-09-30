package com.github.adtstool;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes ADTS frames to an output stream.
 * 
 * @author Ben
 */
public class ADTSWriter extends FilterOutputStream {

    private int framesWritten = 0;

    /**
     * Creates a new ADTS writer.
     * 
     * @param out
     *            output stream to write ADTS frames to
     */
    public ADTSWriter(OutputStream out) {
        super(out);
    }

    /**
     * Writes frames from an ADTSReader to the output stream.
     * 
     * @param reader
     *            the ADTSReader
     * @param start
     *            the first frame to write (inclusive)
     * @param end
     *            the last frame to write (exclusive)
     * @throws IOException
     *             on I/O error
     */
    public void write(ADTSReader reader, int start, int end) throws IOException {
        while (reader.hasNext() && reader.framesRead() < end) {
            ADTSFrame frame = reader.next();
            if (reader.framesRead() >= start) {
                write(frame);
            }
        }
    }

    /**
     * Writes an ADTS frame to the output stream.
     * 
     * @param frame
     *            frame to write
     * @throws IOException
     *             on I/O error
     */
    public void write(ADTSFrame frame) throws IOException {
        write(frame.getBytes());
        framesWritten++;
    }

    /**
     * Returns the number of frames written by the writer.
     * 
     * @return number of frames
     */
    public int framesWritten() {
        return framesWritten;
    }
}
