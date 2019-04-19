package com.lingocoder.jar;

import java.nio.file.Path;
import java.util.Optional;

public class DefaultJarCreationResult implements JarCreationResult {

    private final Optional<Path> pathToExe;

    private final Optional<String> outPut;

    private final boolean isExecutable;

    private final int status;

    public DefaultJarCreationResult(Optional<Path> pathToExe, Optional<String> outPut, boolean isExecutable, int status) {

        this.pathToExe = pathToExe;

        this.outPut = outPut;

        this.isExecutable = isExecutable;

        this.status = status;

    }

    @Override
    public Optional<Path> getPathToExe() {
        return pathToExe;
    }

    @Override
    public Optional<String> getOutPut() {
        return outPut;
    }

    @Override
    public boolean isExecutable() {
        return isExecutable;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
