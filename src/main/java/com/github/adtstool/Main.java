package com.github.adtstool;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a CLI interface for cutting ADTS files.
 * 
 * @author Ben
 */
public class Main {

    private static final Log LOGGER = LogFactory.getLog(Main.class);
    private static final Options OPTIONS = new Options();

    static {
        OPTIONS.addOption("h", "help", false, "Displays this help message");
        OPTIONS.addOption("s", "start", true, "Start time");
        OPTIONS.addOption("e", "end", true, "End time");
    }

    /**
     * Main method for running program.
     * 
     * @param args
     *            command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            System.exit(0);
        }
        try {
            CommandLine cmd = new BasicParser().parse(OPTIONS, args);
            if (args.length == 0 || cmd.hasOption("help")) {
                printHelp();
                System.exit(0);
            }
            String[] files = cmd.getArgs();
            if (files.length != 2) {
                fatalError("Incorrect number of files specified (must be 2)");
            }
            else if (files[0].equals(files[1])) {
                fatalError("Cannot write output to input file");
            }
            String start = cmd.getOptionValue("start", "0");
            String end = cmd.getOptionValue("end",
                    Integer.toString(Integer.MAX_VALUE));
            LOGGER.info("Input: " + files[0]);
            InputStream in = new FileInputStream(files[0]);
            LOGGER.info("Output: " + files[1]);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(
                    files[1]));
            trim(in, out, start, end);
        }
        catch (Exception e) {
            fatalError(e.getMessage());
        }
    }

    /**
     * Prints help message to the standard output.
     */
    private static void printHelp() {
        String cmdLineSyntax = "ADTSTool.jar [options] infile outfile";
        String footer = "Start/end times are specified in number of frames "
                + "(1024 samples) or as times using the hh:mm:ss[.xxx] syntax.";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(cmdLineSyntax, null, OPTIONS, footer);
    }

    /**
     * Trims an ADTS file to a specified length. Start times are rounded down to
     * the nearest frame and end times are rounded up to the nearest frame.
     * 
     * @param in
     *            source input
     * @param out
     *            destination output
     * @param startTime
     *            start time of the output file
     * @param endTime
     *            end time of the output file
     * @throws ParseException
     *             if the times do not parse
     * @see #timeToFrames(java.lang.String, double)
     */
    private static void trim(InputStream in, OutputStream out,
            String startTime, String endTime) throws ParseException,
            IOException {
        ADTSReader reader = new ADTSReader(in);
        ADTSWriter writer = new ADTSWriter(out);
        try {
            if (!reader.hasNext()) {
                fatalError("Input contains no ADTS frames");
            }
            double frameLength = 1024d / reader.peek().getSamplingFrequency();
            int start = (int) Math.floor(timeToFrames(startTime, frameLength));
            LOGGER.debug("Start frame: " + start);
            int end = (int) Math.ceil(timeToFrames(endTime, frameLength));
            LOGGER.debug("End frame: " + end);
            writer.write(reader, start, end);
        }
        finally {
            LOGGER.info(writer.framesWritten() + " frames written");
            reader.close();
            writer.close();
        }
    }

    /**
     * Converts a time string into fames. If the string is a natural number then
     * it is interpreted as a frame count otherwise it will be interpreted as a
     * time using the hh:mm:ss[.xxx] syntax.
     * 
     * @param time
     *            a time string
     * @param frameLength
     *            the length of the frame in samples
     * @return number of frames
     * @throws ParseException
     *             if the time could not be parsed
     */
    private static double timeToFrames(String time, double frameLength)
            throws ParseException {
        if (time.matches("^\\d{1,9}$")) {
            return Integer.parseInt(time);
        }
        else if (time.matches("^(\\d{0,2}:){0,2}\\d{0,2}(\\.\\d{0,3})?")) {
            String[] parts = time.split(":");
            double seconds = 0;
            for (int i = parts.length - 1; i >= 0; i--) {
                int exp = parts.length - i - 1;
                seconds += Double.parseDouble(parts[i]) * Math.pow(60, exp);
            }
            return seconds / frameLength;
        }
        else {
            throw new ParseException("Invalid start/end time");
        }
    }

    /**
     * Logs a message at most severe level and terminates the the application
     * with a status code of 1.
     * 
     * @param msg
     *            error message
     */
    private static void fatalError(String msg) {
        LOGGER.error(msg);
        System.exit(1);
    }
}
