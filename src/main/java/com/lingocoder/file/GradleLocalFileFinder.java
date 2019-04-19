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