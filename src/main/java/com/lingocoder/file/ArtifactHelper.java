package com.lingocoder.file;

import com.lingocoder.common.MavenShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.lingocoder.common.Shared.LOC_ERR_MSG;
import static com.lingocoder.common.Shared.rejectMissingParam;

public class ArtifactHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger("ArtifactHelper");

    private final URI repoLocation;

    private final ArtifactCacheLocator cacheLocator = new ArtifactCacheLocator();

    public ArtifactHelper() { LOGGER.debug("Initializing ArtifactHelper()");

        /* FIXME – Either a generic constructor or spin-off an Interface. */
        this.repoLocation = cacheLocator.locateMavenLocal().orElseThrow(MavenShared::mvnLocExcptn);

        if (!Files.exists(Paths.get(this.repoLocation))){
            throw new NonExistentLocationException(String.format(LOC_ERR_MSG, Paths.get(repoLocation).toFile().getAbsolutePath()));
        }
    }

    public ArtifactHelper(URI repoLocation) {LOGGER.debug("Initializing ArtifactHelper(URI repoLocation)");

        rejectMissingParam("repoLocation", repoLocation);

        if (!Files.exists(Paths.get(repoLocation))){
            throw new NonExistentLocationException(String.format(LOC_ERR_MSG, Paths.get(repoLocation).toFile().getAbsolutePath()));
        }
        this.repoLocation = repoLocation;
    }

    public Optional<Path> groupToPath(final String group) {

        rejectMissingParam("group", group);

        Path path = Paths.get(repoLocation).resolve(Paths.get(group.replace(".", File.separator)));

        return Files.exists(path) ? Optional.of(path) : Optional.empty();
    }

    public Optional<Path> groupArtifactToPath(final String group, final String artifact) {

        rejectMissingParam("artifact", artifact);

        return this.groupToPath(group).map(p -> p.resolve(artifact));
    }

    public Optional<Path> groupArtifactVerToPath(final String group, final String artifact, final String version) {

        rejectMissingParam("version", version);

        return this.groupArtifactToPath(group, artifact).map(p -> p.resolve(version));
    }

    public Optional<Path> gavClassifierToPath(final String group, final String artifact, final String version, final Optional<String> classifier) {

        rejectMissingParam("version", version);

        StringBuffer artifactName = new StringBuffer(artifact);

        artifactName.append("-").append(version);

        classifier.ifPresent(s -> artifactName.append("-").append(s));

        artifactName.append(".jar");

        return this.groupArtifactToPath(group, artifact).map(p -> p.resolve(version).resolve(artifactName.toString())).filter(Files::exists);
    }

    /**
     * Fetch a dependency from the local Maven repo as a jar {@code File}.
     *
     * @param coordinates - Expected to be in the standard colon-separated form:
     *                   "group:artifact:version[:classifier]".
     * @return - The jar {@code File} located at the given coordinates.
     */
    public Optional<File> fromMavenLocal(final String coordinates) {

        String group = this.extractGroup(coordinates);
        final String artifact = this.extractArtifact(coordinates);
        final String version = this.extractVersion(coordinates);
        Optional<String> classifier = this.extractClassifier(coordinates);

        return this.gavClassifierToPath(group,artifact,version,classifier).filter(Files::exists).map(Path::toFile);
    }

    public String extractGroup(final String coordinates){

        return this.splitCoordinates(coordinates)[0];

    }

    public String extractArtifact(final String coordinates){

        return this.splitCoordinates(coordinates)[1];

    }

    public String extractVersion(final String coordinates){

        return this.splitCoordinates(coordinates)[2];
    }

    public Optional<String> extractClassifier(final String coordinates){

        String[] gavCoords = this.splitCoordinates(coordinates);

        return gavCoords.length == 4 ? Optional.of( gavCoords[3]) : Optional.empty();
    }

    public String[] splitCoordinates(final String coordinates){

        rejectMissingParam("coordinates", coordinates);

        return coordinates.split(":");
    }

    /**
     * Looks for a local Maven repository in one of three standard locations on the file system:
     * <ol>
     *     <li><em>The default local Maven repository location: <strong>$HOME/.m2/repository/</strong></em>
     *     <li><em>A path referenced by an <strong>$M2_REPO_ENV</strong> environment variable</em></li>
     *     <li><em>A path referenced by a <strong>-Dmaven.local.repo=${...}</strong> system property</em></li>
     * </ol>
     * @return Either the location on the file system of a local Maven repository, or {@code Optional.empty()} if none was found.
     */
    public Optional<URI> locateMavenLocal(){

        return this.repoLocation != null ? Optional.of(this.repoLocation) : this.cacheLocator.locateMavenLocal();
    }
}
