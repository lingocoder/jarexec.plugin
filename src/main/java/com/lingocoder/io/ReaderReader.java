package com.lingocoder.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ReaderReader  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderReader.class);

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

    public Reader prepare(InputStream inner){

        return new InputStreamReader(inner);
    }
}
