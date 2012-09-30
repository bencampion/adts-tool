package com.github.adtstool;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reads ADTS frames from an input stream. If an IOExeception occurs while
 * reading from the input then the reader assumes that the end of the input has
 * been reached.
 * 
 * @author Ben
 */
public class ADTSReader implements Closeable, Iterator<ADTSFrame> {

    private static final Log LOGGER = LogFactory.getLog(ADTSReader.class);
    private final DataInputStream input;
    private ADTSFrame nextFrame;
    private int framesRead = 0;
    private boolean eof = false;
    private IOException ioException;

    /**
     * Creates a new ADTS reader.
     * 
     * @param in
     *            input stream containing ADTS frames
     * @throws IOException
     *             on I/O error
     */
    public ADTSReader(InputStream in) {
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        input = new DataInputStream(in);
    }

    /**
     * Reads the next frame from the input and stores it in the nextFrame
     * attribute.
     * 
     * @throws IOException
     *             on I/O error
     */
    private void readNextFrame() {
        try {
            input.mark(6);
            final int firstByte = input.read();
            if (firstByte == -1) {
                eof();
            }
            else if (firstByte == 0xff && (input.read() & 0xf6) == 0xf0) {
                input.read();
                final int frameSize = ((input.read() & 0x3) << 11)
                        | (input.read() << 3) | ((input.read() >>> 5) & 0x7);
                if (frameSize > 6) {
                    input.reset();
                    final byte[] buffer = new byte[frameSize];
                    try {
                        input.readFully(buffer);
                        nextFrame = new ADTSFrame(buffer);
                    }
                    catch (EOFException e) {
                        LOGGER.warn("Discarding incomplete frame at EOF");
                        eof();
                    }
                }
                else {
                    LOGGER.warn("Ingnoring bad frame less than 7 bytes long");
                }
            }
            else {
                LOGGER.warn("Input is not in sync at frame " + framesRead);
                input.reset();
                findSyncword();
                readNextFrame();
            }
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage());
            ioException = e;
            eof();
        }
    }

    /**
     * Called when the end of the input stream has been reached.
     */
    private void eof() {
        LOGGER.info("Reached EOF after reading " + framesRead + " frames");
        nextFrame = null;
        eof = true;
    }

    /**
     * Advances the input to the next ADTS syncword.
     * 
     * @throws IOException
     *             on I/O error
     */
    private void findSyncword() throws IOException {
        LOGGER.info("Searching for syncword");
        boolean inSync = false;
        do {
            input.mark(2);
            if (input.read() == 0xff && (input.read() & 0xf6) == 0xf0) {
                inSync = true;
            }
            input.reset();
        } while (!inSync && input.read() != -1);
        if (inSync) {
            LOGGER.info("Syncword found");
        }
        else {
            LOGGER.warn("Syncword not found");
        }
    }

    /**
     * Returns true if the input stream still contains ADTS frames. Invoking
     * this method does not advance the input.
     * 
     * @return true if there are more ADTS frames
     */
    @Override
    public boolean hasNext() {
        if (!eof && nextFrame == null) {
            readNextFrame();
        }
        return !eof;
    }

    /**
     * Returns the next ADTS frame from the input.
     * 
     * @return the next ADTS frame
     * @throws NoSuchElementException
     *             if there are not more frames to be read
     */
    @Override
    public ADTSFrame next() {
        final ADTSFrame frame = peek();
        framesRead++;
        readNextFrame();
        return frame;
    }

    /**
     * The remove operation is not supported by this implementation of Iterator.
     * 
     * @throws UnsupportedOperationException
     *             if this method is invoked
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Peek at the next frame with out advancing the input.
     * 
     * @return the next ADTS frame
     * @throws NoSuchElementException
     *             if there are no more frames to be read
     */
    public ADTSFrame peek() {
        if (!hasNext()) {
            throw new NoSuchElementException("No ADTS frames left in input");
        }
        return nextFrame;
    }

    /**
     * Returns the number of frames read by the reader.
     * 
     * @return number of frames
     */
    public int framesRead() {
        return framesRead;
    }

    /**
     * Closes the underlying InputStream.
     */
    @Override
    public void close() throws IOException {
        input.close();
    }

    /**
     * Returns the IOException last thrown by underlying InputStream. This
     * method returns null if no such exception exists.
     * 
     * @return last exception thrown
     */
    public IOException ioException() {
        return ioException;
    }
}
