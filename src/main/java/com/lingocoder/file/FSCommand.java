package com.lingocoder.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FSCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(FSCommand.class);

    public static boolean move(Path from, Path to){
        boolean moved;
        try {
            Path target = Files.move(from, to, REPLACE_EXISTING);
            moved = (!exists(from)) && (exists(target));
        } catch (IOException e) {
            moved = false;
            LOGGER.error(e.getMessage(), e);
        }
        return moved;
    }


    public static Path resolveToPath(final Path rootDir, Optional<String> dottedPackages, Optional<String> fileName) {

        if(!rootDir.toFile().exists()){
            throw new NonExistentLocationException("'" + rootDir.toFile() + "' does not exist");
        }

        Path resolved = dottedPackages.map(pkg -> pkg.split("\\.")).map(dirs -> {Path resvd = get(rootDir.toUri()); for(String dir : dirs){resvd = resvd.resolve(dir);} return resvd;}).orElse(rootDir);


        return resolved.resolve(fileName.orElse(""));
    }
}
