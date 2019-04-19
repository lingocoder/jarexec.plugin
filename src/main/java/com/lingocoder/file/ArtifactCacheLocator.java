package com.lingocoder.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Optional;

import static com.lingocoder.common.GradleShared.*;
import static com.lingocoder.common.MavenShared.*;

public class ArtifactCacheLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger("ArtifactCacheLocator");

    private static final File GRADLE_DIR = new File(GRADLE);

    private static final File MAVEN_LOCAL_DIR = new File(M2_REPO);

    protected static final Optional<URI> MAVEN_LOCAL =
            !MAVEN_LOCAL_DIR.exists() ? Optional.empty()
                    : Optional.of(MAVEN_LOCAL_DIR.toURI());
    /**
     * Looks for a local Maven repository in one of two standard locations on the file system:
     * <ol>
     *     <li><em>The default local Maven repository location: <strong>$HOME/.m2/repository/</strong></em>
     *     <li><em>A path referenced by an <strong>$M2_REPO_ENV</strong> environment variable</em></li>
     * </ol>
     * @return Either the location on the file system of a local Maven repository, or {@code Optional.empty()} if none was found.
     */
    public Optional<URI> locateMavenLocal(){

        String mvnLocal = MAVEN_LOCAL_DIR.getAbsolutePath();

        if(MAVEN_LOCAL_DIR.exists()) {
            LOGGER.debug(LOCATED_MVN_LOCAL, mvnLocal);
        } else {
            LOGGER.warn(MVN_LOCAL_NOT_FOUND, mvnLocal);
        }
        return MAVEN_LOCAL;
    }

    public Optional<URI> locateGradleJarsCache() {
        return this.buildGradleCacheURI("jars-3");
    }

    public Optional<URI> locateGradleModulesCache() {
        return this.buildGradleCacheURI("modules-2");
    }

    private Optional<URI> buildGradleCacheURI(String whichCache){
       return !GRADLE_DIR.exists() ? Optional.empty()
               : Optional.of(Paths.get(GRADLE_DIR.toURI())
               .resolve("caches").resolve(whichCache).toFile().toURI());
    }
}
