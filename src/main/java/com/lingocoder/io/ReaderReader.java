package com.lingocoder.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * {@code ReaderReader} reads a {@link Reader}.
 */
public class ReaderReader  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderReader.class);

    /**
     * Consume the contents of the given {@link Reader} and transform it
     * into a {@link String}.
     * 
     * @param reader The {@link Reader} whose contents are to be read.
     * 
     * @return The contents of the given {@link Reader} as a {@link String}.
     */
    public String read(Reader reader) {

        StringBuffer string = new StringBuffer();

        Thread rdrRunner = new Thread( () -> {

            try (BufferedReader readerReSrc = new BufferedReader(reader, 256) ) {

                String line;

                while( (line = readerReSrc.readLine()) != null ){

                    string.append(line);
                }
            }

            catch(IOException ioe){ LOGGER.error(ioe.getMessage(), ioe); }
        });

        rdrRunner.start();

        try {
            rdrRunner.join();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.debug("ReaderReader got '{}' for stdout", string.toString().trim());

        return string.toString().trim();
    }

    /**
     * Wrap the given {@link InputStream} inside a {@link Reader}.
     * 
     * @param inner The {@link InputStream} to be wrapped.
     * 
     * @return A {@link Reader} that wraps the {@code inner} {@link InputStream}.
     */
    public Reader prepare(InputStream inner){

        return new InputStreamReader(inner);
    }
}
