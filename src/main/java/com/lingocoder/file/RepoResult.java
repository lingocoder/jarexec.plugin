package com.lingocoder.file;

import java.io.File;
import java.util.Optional;


public interface RepoResult {
    Optional<File> getFolder();

    Optional<File> getFile();
}
