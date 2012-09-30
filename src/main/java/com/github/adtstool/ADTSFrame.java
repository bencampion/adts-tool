package com.github.adtstool;

/**
 * Object wrapper for an ADTS frame.
 * 
 * @author Ben
 */
public class ADTSFrame {

    private static final String[] PROFILES = { "Main profile",
            "Low Complexity profile (LC)",
            "Scalable Sampling Rate profile (SSR)", "(reserved)" };
    private static final int[] SAMPLING_FREQUENCIES = { 96000, 88200, 64000,
            48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350,
            -13, -14, -15 };
    private static final String[] CHANNEL_CONFIGURATIONS = {
            "(not specified in header)", "1.0 Mono", "2.0 Stereo",
            "3.0 Stereo", "4.0 Surround", "5.0 Surround", "5.1 Surround",
            "7.1 Surround" };

    private final byte[] bytes;

    /**
     * Creates a new ADTS frame wrapper.
     * 
     * @param bytes
     *            frame data
     */
    public ADTSFrame(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * MPEG identifier. Should always be set to 1.
     * 
     * @return 1
     */
    public int getID() {
        return (bytes[1] >>> 3) & 0x1;
    }

    /**
     * Indicates which layer is used. Should always be set to 0.
     * 
     * @return 0
     */
    public int getLayer() {
        return (bytes[1] >>> 1) & 0x3;
    }

    /**
     * Indicates whether error checking data is present or not.
     * 
     * @return true if protection is present
     */
    public boolean hasProtection() {
        return (bytes[1] & 0x1) == 0x0;
    }

    /**
     * Returns the index of the AAC profile used.
     * 
     * @return integer between 0-3
     */
    public int getProfileIndex() {
        return (bytes[2] >>> 6) & 0x3;
    }

    /**
     * Returns the name of the AAC profile used.
     * 
     * @return name of the AAC profile
     */
    public String getProfile() {
        return PROFILES[getProfileIndex()];
    }

    /**
     * Indicates the sampling frequency used.
     * 
     * @return integer between 0-15
     */
    public int getSamplingFrequencyIndex() {
        return (bytes[2] >>> 2) & 0xf;
    }

    /**
     * Returns the sampling frequency in Hz.
     * 
     * @return the sampling frequency in Hz or a negative number if the sampling
     *         frequency index is reserved.
     */
    public int getSamplingFrequency() {
        return SAMPLING_FREQUENCIES[getSamplingFrequencyIndex()];
    }

    /**
     * Returns the value of the private bit.
     * 
     * @return true if set
     */
    public boolean hasPrivateBit() {
        return (bytes[2] & 0x2) == 0x2;
    }

    /**
     * Indicates the channel configuration used.
     * 
     * @return integer between 0-7
     */
    public int getChannelConfigurationIndex() {
        return ((bytes[2] & 0x1) << 2) | ((bytes[3] >>> 6) & 0x3);
    }

    /**
     * Returns a textual description of the channel configuration.
     * 
     * @return a description of the channel configuration
     */
    public String getChannelConfiguration() {
        return CHANNEL_CONFIGURATIONS[getChannelConfigurationIndex()];
    }

    /**
     * Returns the value of the original copy bit.
     * 
     * @return true if set
     */
    public boolean isOriginalCopy() {
        return (bytes[3] & 0x20) == 0x20;
    }

    /**
     * Returns the value of the home bit.
     * 
     * @return true if set
     */
    public boolean isHome() {
        return (bytes[3] & 0x10) == 0x10;
    }

    /**
     * Returns one bit of the copyright identification field.
     * 
     * @return 1 if set
     */
    public int getCopyrightIdentificationBit() {
        return (bytes[3] >>> 3) & 0x1;
    }

    /**
     * Checks if this frame contains the first bit of the copyright
     * identification field.
     * 
     * @return true if it is the start copyright identification field
     */
    public boolean isCopyrightIdentificationStart() {
        return (bytes[3] & 0x4) == 0x4;
    }

    /**
     * Returns the frame size.
     * 
     * @return the size of the frame in bytes
     */
    public int getFrameSize() {
        return bytes.length;
    }

    /**
     * Returns the state of the bit reservoir.
     * 
     * @return the state of the bit reservoir
     */
    public int getADTSBufferFullness() {
        return ((bytes[5] & 0x1f) << 6) | ((bytes[6] >>> 2) & 0x3f);
    }

    /**
     * Returns the number of raw data blocks in the frame.
     * 
     * @return the number of raw data blocks in the frame
     */
    public int getNumberOfRawDataBlocksInFrame() {
        return (bytes[6] & 0x3) + 1;
    }

    /**
     * Returns a reference to the underlying byte array backing this frame.
     * 
     * @return the byte array backing this frame
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s, %s, %d Hz", getProfile(),
                getChannelConfiguration(), getSamplingFrequency());
    }
}
