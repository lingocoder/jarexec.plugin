package com.lingocoder.file;

import java.io.File;
import java.util.Optional;

public class DefaultRepoResult implements RepoResult {

    private final File folder;

    private final File file;

    public DefaultRepoResult(File folder, File file) {
        this.folder = folder;
        this.file = file;
    }

    @Override
    public Optional<File> getFolder() {
        return this.folder != null && this.folder.exists() ? Optional.of(this.folder) : Optional.empty();
    }

    @Override
    public Optional<File> getFile() {
        return this.file != null && this.file.exists() ? Optional.of(this.file) : Optional.empty();
    }
}
