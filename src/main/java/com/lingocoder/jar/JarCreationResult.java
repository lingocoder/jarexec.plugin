package com.lingocoder.jar;

import java.nio.file.Path;
import java.util.Optional;

public interface JarCreationResult {

    Optional<Path> getPathToExe();

    Optional<String> getOutPut();

    boolean isExecutable();

    int getStatus();
}
