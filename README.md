# ADTS Tool

ADTS Tool is a command line tool for lossless cutting of [ADTS](http://wiki.multimedia.cx/index.php?title=ADTS) files. ADTS files typically contain AAC audio and often have an extension of .aac.

Lossless cutting works by cutting files on ADTS frame boundaries. A frame contains 1024 samples, which means an input with a sample rate of 44.1 kHz can only be cut to an accuracy of ~23 ms.

ADTS Tool requires Java 8 to build and run.

## Building

Clone the repo and build using the Gradle wrapper:

    $ git clone https://github.com/bencampion/adts-tool.git
    $ ./gradlew clean build

This will produce zip and tar archives in `build/distributions` that contain all dependencies along with start scripts for Bash and Windows.

## Usage

Getting help:

    $ bin/adts-tool
    Usage: adts-tool [options]
      Options:
      * -e, --end
           End time (hh:mm:ss.xx)
      * -i, --input
           Input file
      * -o, --output
           Output file
      * -s, --start
           Start time (hh:mm:ss.xxx)

Copy 90 seconds from the file _src.acc_, starting at 00:04:30, to the file _dest.acc_:

    $ bin/adts-tool --start 00:04:30 --end 00:06:00 --input src.acc --output dest.aac

## Licence

ADTS Tool is licenced under the Apache License 2.0.
