package com.lingocoder.process;

import com.lingocoder.process.io.ProcessIO;
import com.lingocoder.io.ReaderReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A convenience class that creates and executes operating system {@link Process}es.
 */
public class ProcessHelper{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessHelper.class);

    /**
     *
     * @param cmdArray A {@link String} array containing a program to run and its arguments.
     *
     * @return
     */
    public ProcessIO runProcess(String[] cmdArray) {

        ProcessIO io = null;

        ReaderReader reader = new ReaderReader();

        Process proc;

        int exit = -1;

        String stdout;

        String stderr;

        try {

            LOGGER.debug("{}", "From ProcessHelper. Before Runtime.exec() call");

            ProcessBuilder procBuilder = new ProcessBuilder();

            procBuilder.redirectErrorStream(true);

            procBuilder.command(cmdArray);

            proc = procBuilder.start();

            if(proc.isAlive()){

                try {

                    LOGGER.debug("{}", "From ProcessHelper. Before Process.waitFor() call");

                    exit = proc.waitFor();

                    LOGGER.debug("{}", "From ProcessHelper. After Process.waitFor() call");
                } catch (InterruptedException e) {

                    LOGGER.error(e.getMessage(), e);
                }

                InputStream procIs = proc.getInputStream();

                Reader charRdr = reader.prepare(procIs);

                stdout = reader.read(charRdr);

                LOGGER.debug("{} '{}'", "From ProcessHelper. Read Process' stdout:", stdout);

                Reader errRdr = reader.prepare(proc.getErrorStream());

                stderr = reader.read(errRdr);

                LOGGER.debug("{} '{}'", "From ProcessHelper. Read Process' stderr:", stderr);

                io = new ProcessIO(exit == 0, stdout , stderr);

                LOGGER.debug("{} '{}'", "From ProcessHelper. Preparing ProcessIO. Exit status is:", exit);

            }

        } catch (IOException e) {

            LOGGER.error(e.getMessage(), e);

        } finally {

            LOGGER.debug("{}", "From ProcessHelper finally{} block");
        }

        LOGGER.debug("{} '{}'", "From ProcessHelper. Returning ProcessIO:", io);

        return io;
    }

}


