package com.lingocoder.file;

import com.lingocoder.common.GradleShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import static com.lingocoder.common.Shared.rejectMissingParam;

/**
 * <p>Searches a local Gradle installation for locally-cached artifacts.</p>
 *
 * <p>Consumers of this class are responsible for installing and configuring Gradle with
 * the standard Gradle environment variables. For example, <em>{@code GRADLE_USER_HOME}</em>.</p>
 */
public class GradleLocalFileFinder implements CachedArtifactFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger("GradleLocalFileFinder");

    private static final DefaultRepoResult DEFAULT_RESULT = new DefaultRepoResult(new File("(#S@&%*!)"), new File("(#S@&%*!)"));

    private final ArtifactCacheLocator cacheLocator = new ArtifactCacheLocator();

    private final ArtifactHelper artifactHelp;

    private final URI gradle;

    /**
     * <p>Create an instance with default state. The created instance will attempt
     * to locate and work with a local Gradle installation's file system dependency
     * cache it assumes will be installed at a standard default location. It will
     * look for environment variables such as <em>{@code GRADLE_USER_HOME}</em>
     * and <em>{@code GRADLE_HOME}</em>.</p>
     * 
     * <p>If no such environment is found, it will assume the caller intends for this
     * instance to work with a Gradle installation located in the home directory of
     * the current user.</p>
     * 
     * <p>The methods of this instance operate in the context of the locations described
     * above.</p>
     */
    public GradleLocalFileFinder() {

        this.gradle = Paths.get(GradleShared.GRADLE).toUri().resolve("caches");

        this.artifactHelp = new ArtifactHelper(this.gradle);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public RepoResult find(String coordinates) {

        rejectMissingParam("coordinates", coordinates);

        RepoResult result = lookInGradleCaches(coordinates);

        return result.getFile().isEmpty() || !result.getFile().get().exists() ? lookInMavenCache(coordinates) : result;
    }

    private RepoResult lookInGradleCaches(String coordinates) {

        /* TODO – Allow empty version coordinate */
        RepoResult result = DEFAULT_RESULT;

        String[] gav = this.artifactHelp.splitCoordinates(coordinates);

        try (Stream<Path> pathStream = Files.walk(Paths.get(this.gradle)).parallel()) {

            Optional<Path> found = pathStream.filter(p -> !Files.isDirectory(p)).filter(p -> p.toFile().getName().endsWith(".jar")).filter(p -> !p.toFile().getName().contains("source")).filter(p -> p.toFile().getName().contains(gav[1])).filter(p -> p.toFile().getName().contains(gav[2])).findAny();

            result = found.map(Path::toFile).map(file -> new DefaultRepoResult(file.getParentFile(), file)).orElse(DEFAULT_RESULT);

        } catch (IOException e) {

            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    private RepoResult lookInMavenCache(String coordinates) {

        return new MavenLocalFileFinder(cacheLocator.locateMavenLocal().orElse(Paths.get(".").toUri())).find(coordinates);
    }
}